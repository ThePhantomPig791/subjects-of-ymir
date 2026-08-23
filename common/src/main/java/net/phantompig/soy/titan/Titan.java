package net.phantompig.soy.titan;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.block.SoyBlockTags;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.entity.TitanCorpseEntity;
import net.phantompig.soy.network.ScreenShakeMessage;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.power.ability.GrabbingAbility;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.stat.SoyStats;
import net.phantompig.soy.titan.hardening.HardeningSystem;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.phantompig.soy.compat.curiostrinkets.SoyCuriosTrinketsUtil;
import net.threetag.palladium.power.SuperpowerUtil;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.json.GsonUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import oshi.annotation.concurrent.Immutable;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@Immutable
public class Titan {
    public static final UUID TITAN_HEALTH_ATTRIBUTE_UUID = UUID.fromString("09ed062b-73c1-4a2d-0803-8512514aa03e");
    public static final UUID TITAN_ARMOR_ATTRIBUTE_UUID = UUID.fromString("12abc62b-73c1-4a2d-0000-8512514aa03e");
    public static final UUID TITAN_ATTACK_DAMAGE_ATTRIBUTE_UUID = UUID.fromString("ba2e2d7f-1f67-4800-8c9e-31b9e693ecdb");
    public static final UUID TITAN_KNOCKBACK_RESISTANCE_ATTRIBUTE_UUID = UUID.fromString("ba2e2d2a-1f23-4800-0826-31b9e691eceb");

    public static final ResourceLocation TITAN_SHIFT_ADVANCEMENT = SubjectsOfYmir.rsrc("titan_shift");


    public final ResourceLocation id;
    public final ResourceLocation powerPath;
    public final List<String> variants;
    public final int resolution;
    public final float scale;
    public final int maxProgress;
    public final int maxCharge;
    @Nullable
    public final Color baseEyeColor;
    public final double weight;
    public final int defaultMaxStamina;

    public final TitanStats stats;

    public final boolean canSpeak;

    private Titan(ResourceLocation id, List<String> variants, int resolution, float scale, int maxProgress, int maxCharge, Color baseEyeColor, TitanStats stats, double weight, int defaultMaxStamina, boolean canSpeak) {
        this.id = id;
        this.variants = variants;
        this.resolution = resolution;
        this.scale = scale;
        this.maxProgress = maxProgress;
        this.maxCharge = maxCharge;
        if (baseEyeColor.getAlpha() == 0) this.baseEyeColor = null;
        else this.baseEyeColor = baseEyeColor;
        this.stats = stats;
        this.weight = weight;
        this.defaultMaxStamina = defaultMaxStamina;
        this.canSpeak = canSpeak;

        this.powerPath = id.withPath("titan/" + id.getPath());
    }


    public void startShift(LivingEntity entity, int charge) {
        if (!(entity instanceof SoyPlayerExtension ext)) return;

        SuperpowerUtil.addSuperpower(entity, this.powerPath);
        if (entity.getAttribute(Attributes.MAX_HEALTH).getModifier(TITAN_HEALTH_ATTRIBUTE_UUID) == null) {
            entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(TITAN_HEALTH_ATTRIBUTE_UUID, "titan extra health", this.stats.extraHealth, AttributeModifier.Operation.ADDITION));
        }
        if (entity.getAttribute(Attributes.ARMOR).getModifier(TITAN_ARMOR_ATTRIBUTE_UUID) == null) {
            entity.getAttribute(Attributes.ARMOR).addPermanentModifier(new AttributeModifier(TITAN_ARMOR_ATTRIBUTE_UUID, "titan armor", this.stats.extraArmor, AttributeModifier.Operation.ADDITION));
        }
        if (entity.getAttribute(Attributes.ATTACK_DAMAGE).getModifier(TITAN_ATTACK_DAMAGE_ATTRIBUTE_UUID) == null) {
            entity.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(TITAN_ATTACK_DAMAGE_ATTRIBUTE_UUID, "titan attack damage", this.stats.extraDamage, AttributeModifier.Operation.ADDITION));
        }
        if (entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getModifier(TITAN_KNOCKBACK_RESISTANCE_ATTRIBUTE_UUID) == null) {
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier(TITAN_KNOCKBACK_RESISTANCE_ATTRIBUTE_UUID, "titan knockback resistance", 1, AttributeModifier.Operation.ADDITION));
        }

        entity.extinguishFire();
        entity.removeAllEffects();
        entity.resetFallDistance();

        if (entity instanceof Player player) {
            ext.soy$getTitanInstance().playerInventory = player.getInventory().save(new ListTag());
            player.getInventory().clearContent();
            if (SoyConfig.Server.shouldSaveCuriosTrinketsInventory() && SoyCuriosTrinketsUtil.INSTANCE.isLoaded()) {
                if (ext.soy$getTitanInstance().curiosTrinketsInventory == null) ext.soy$getTitanInstance().curiosTrinketsInventory = new CompoundTag();
                SoyCuriosTrinketsUtil.INSTANCE.write(player, ext.soy$getTitanInstance().curiosTrinketsInventory);
                SoyCuriosTrinketsUtil.INSTANCE.clear(player);
            }

            player.awardStat(SoyStats.TIMES_SHIFTED, 1);

            PlayerUtil.playSound(player, entity.getX(), entity.getY(), entity.getZ(), SoySounds.SHIFT_LOCAL.get(), SoundSource.PLAYERS);
        }

        PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 64, SoySounds.SHIFT_LOCAL.get(), SoundSource.PLAYERS);
        this.emitSteam(entity, 1.5f, 5 * charge, false);

        strikeLightning(entity);
        strikeLightning(entity);
        strikeLightning(entity);

        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 10, true, false));
        entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20, 10, true, false));

        ext.soy$getTitanInstance().startScaleChange();
        ext.soy$getTitanInstance().setDecay(TitanInstance.START_CORPSE_DECAY);
        ext.soy$getTitanInstance().canShiftTicks = 0;
        ext.soy$getTitanInstance().setMarksTimer(ext.soy$getTitanInstance().getMarksTimer() + charge * charge);

        entity.level().getEntities(null, entity.getBoundingBox().inflate(15 + 3 * Math.sqrt(charge))).forEach(e -> {
            double strength = charge / Math.max(Math.sqrt(e.distanceTo(entity)), 1);
            if (e instanceof ServerPlayer player) {
                SoyNetwork.NETWORK.sendToPlayer(player, new ScreenShakeMessage(3500, (float) (5 + strength) / 100));
            }
        });

        if (SoyConfig.Server.shouldTitansExplodeBlocksOnShift()) {
            final int evaporationRange = 3 + (int) Math.sqrt(charge);
            final int evaporationRangeSqr = evaporationRange * evaporationRange;
            for (int dx = -evaporationRange; dx <= evaporationRange; dx++) {
                for (int dy = -evaporationRange; dy <= evaporationRange; dy++) {
                    for (int dz = -evaporationRange; dz <= evaporationRange; dz++) {
                        if (dx * dx + dy * dy + dz * dz > evaporationRangeSqr) continue;
                        BlockPos pos = entity.blockPosition().offset(dx, dy, dz);
                        if (entity.level().getFluidState(pos).is(FluidTags.WATER)) {
                            final BlockState state = entity.level().getBlockState(pos);
                            if (state.getBlock() instanceof BucketPickup bucketPickup) {
                                bucketPickup.pickupBlock(entity.level(), pos, state);
                            } else if (state.getBlock() instanceof LiquidBlock || state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT) || state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS) || state.is(Blocks.SEA_PICKLE)) {
                                entity.level().destroyBlock(pos, true);
                                entity.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }

    public void tickDuringShift(LivingEntity entity, int progress, int charge) {
        if (!(entity instanceof SoyPlayerExtension ext)) return;
        ext.soy$getTitanInstance().setProgress(++progress);
        entity.level().explode(entity, null, null, entity.getX(), entity.getEyeY(), entity.getZ(), (float) Math.sqrt(charge / 5f), false, SoyConfig.Server.shouldTitansExplodeBlocksOnShift() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE, false).explode();
        ext.soy$getTitanInstance().setMarksTimer(ext.soy$getTitanInstance().getMarksTimer() + 4);
        ext.soy$getTitanInstance().ticksShifted++;

        entity.level().getEntities(null, entity.getBoundingBox().inflate(15 + 3 * Math.sqrt(charge))).forEach(e -> {
            double strength = charge / Math.max(Math.sqrt(e.distanceTo(entity)), 1) / 40;
            if (!(entity.equals(e))) {
                e.addDeltaMovement(e.position().subtract(entity.position()).normalize().scale(strength / Math.pow(e.getBoundingBox().getYsize(), 1.5)));
                if (e instanceof ServerPlayer player) {
                    player.connection.send(new ClientboundSetEntityMotionPacket(player));
                }
            }
        });

        if (progress > 2 && SoyProperties.GRABBED.get(entity)) {
            LivingEntity grabber = GrabbingAbility.getGrabber(entity);
            if (grabber != null && !GrabbingAbility.canGrab(grabber, entity)) GrabbingAbility.stopGrabbing(grabber);
        }

        final float invPro = 0.5f - ((float) progress) / this.maxProgress;
        if (invPro > 0) {
            PlayerUtil.spawnParticleForAll(
                    entity.level(),
                    128,
                    (ParticleOptions) SoyParticles.TRANSFORM_ARC.get(),
                    false,
                    entity.getX(),
                    entity.getEyeY(),
                    entity.getZ(),
                    3,
                    5,
                    3,
                    0.2f,
                    (int) (6 * invPro)
            );
            PlayerUtil.spawnParticleForAll(
                    entity.level(),
                    128,
                    (ParticleOptions) SoyParticles.TRANSFORM_ZAP.get(),
                    false,
                    entity.getX(),
                    entity.getEyeY(),
                    entity.getZ(),
                    3,
                    5,
                    3,
                    0.2f,
                    (int) (6 * invPro)
            );
            PlayerUtil.spawnParticleForAll(
                    entity.level(),
                    128,
                    (ParticleOptions) SoyParticles.TRANSFORM_ZOP.get(),
                    false,
                    entity.getX(),
                    entity.getEyeY(),
                    entity.getZ(),
                    3,
                    5,
                    3,
                    0.2f,
                    (int) (6 * invPro)
            );
            PlayerUtil.spawnParticleForAll(
                    entity.level(),
                    128,
                    (ParticleOptions) SoyParticles.TRANSFORM_CRACK.get(),
                    false,
                    entity.getX(),
                    entity.getEyeY(),
                    entity.getZ(),
                    3,
                    5,
                    3,
                    0.2f,
                    (int) (6 * invPro)
            );
            PlayerUtil.spawnParticleForAll(
                    entity.level(),
                    128,
                    (ParticleOptions) SoyParticles.TRANSFORM_RAY.get(),
                    false,
                    entity.getX(),
                    entity.getY() + 4,
                    entity.getZ(),
                    10,
                    1f,
                    10,
                    0.3f,
                    (int) (4 * invPro) + 1
            );
        }
        PlayerUtil.spawnParticleForAll(
                entity.level(),
                128,
                (ParticleOptions) SoyParticles.TRANSFORM_SPARK.get(),
                false,
                entity.getX(),
                entity.getEyeY(),
                entity.getZ(),
                3,
                5,
                3,
                0.2f,
                4
        );
    }

    public void completedShift(LivingEntity entity, int charge) {
        if (entity instanceof ServerPlayer pl) {
            pl.getAdvancements().award(pl.server.getAdvancements().getAdvancement(TITAN_SHIFT_ADVANCEMENT), "shift");
        }
        if (entity instanceof SoyPlayerExtension ext) {
            ext.soy$getTitanInstance().setStamina((int) (ext.soy$getTitanInstance().getStamina() * 0.6));
        }
    }

    public void unshift(LivingEntity entity) {
        unshift(entity, true);
    }

    public void unshift(LivingEntity entity, boolean spawnCorpse) {
        boolean decay = (entity instanceof HardeningSystemHolder hh && hh.soy$getHardeningSystem().getAllHardening() == 0);
        unshift(entity, spawnCorpse, decay);
    }

    public void unshiftWithAdverseEffects(LivingEntity entity) {
        unshiftWithAdverseEffects(entity, true, true);
    }
    public void unshiftWithAdverseEffects(LivingEntity entity, boolean spawnCorpse, boolean shouldCorpseDecay) {
        unshift(entity, spawnCorpse, shouldCorpseDecay);
        applyAdverseEffects(entity);
    }
    private void applyAdverseEffects(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS,
                1200,
                1,
                false,
                false
        ));
        entity.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                800,
                0,
                false,
                false
        ));
        entity.addEffect(new MobEffectInstance(
                MobEffects.CONFUSION,
                160,
                0,
                false,
                false
        ));
        entity.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                240,
                0,
                false,
                false
        ));
        if (entity instanceof Player player) {
            player.causeFoodExhaustion(8);
        }
        if (entity instanceof SoyPlayerExtension ext) {
            ext.soy$getTitanInstance().setStamina((int) (ext.soy$getTitanInstance().getStamina() * 0.1));
            ext.soy$getTitanInstance().setMarksTimer(6000);
        }
    }

    public void unshift(LivingEntity entity, boolean spawnCorpse, boolean shouldCorpseDecay) {
        if (!(entity instanceof SoyPlayerExtension ext) || ext.soy$getTitanInstance().titan == null || !(entity instanceof HardeningSystemHolder hardh)) return;
        ext.soy$getTitanInstance().strengthIncreases.clear();
        ext.soy$getTitanInstance().ticksShifted = 0;

        SuperpowerUtil.removeSuperpower(entity, this.powerPath);
        entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(TITAN_HEALTH_ATTRIBUTE_UUID);
        entity.getAttribute(Attributes.ARMOR).removeModifier(TITAN_ARMOR_ATTRIBUTE_UUID);
        entity.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(TITAN_ATTACK_DAMAGE_ATTRIBUTE_UUID);
        entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).removeModifier(TITAN_KNOCKBACK_RESISTANCE_ATTRIBUTE_UUID);

        entity.heal(20);
        entity.removeAllEffects();
        entity.extinguishFire();
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
        if (entity instanceof ServerPlayer player) {
            player.heal(1);
            player.hurt(player.damageSources().magic(), 1);
            player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), List.of(player.getAttribute(Attributes.MAX_HEALTH), player.getAttribute(Attributes.ARMOR))));
        }

        if (hardh.soy$getHardeningSystem().getAllHardening() >= 0.9f) {
            spawnCorpse = false;

            double radius = hardh.soy$getHardeningSystem().getAllHardening();
            radius = (radius / (radius + 0.1)) * (0.2 * radius / (radius + 1) + 1.5);
            double yCoeff = radius * entity.getBoundingBox().getYsize() * 0.15;
            radius *= entity.getBoundingBox().getXsize();
            double radiusSqr = radius * radius;
            for (double dx = -radius; dx <= radius; dx++) {
                for (double dy = -2 * radius; dy <= 2 * radius; dy++) {
                    for (double dz = -radius; dz <= radius; dz++) {
                        if ((dx * dx) + (dy * dy) / yCoeff + (dz * dz) > radiusSqr) continue;
                        BlockPos pos = BlockPos.containing(entity.getX() + dx, entity.getY() + dy, entity.getZ() + dz);
                        if (entity.level().getBlockState(pos).is(SoyBlockTags.HARDENING_CAN_REPLACE)) {
                            entity.level().setBlockAndUpdate(pos, SoyBlocks.HARDENING_BLOCK.get().defaultBlockState());
                        }
                    }
                }
            }
            PlayerUtil.playSoundToAll(entity.level(),
                    entity.getX(),
                    entity.getEyeY(),
                    entity.getZ(),
                    48,
                    SoySounds.SHORT_HARDEN.get(),
                    SoundSource.BLOCKS,
                    2f,
                    (float) (0.1 * Math.random() + 0.7)
            );
            PlayerUtil.spawnParticleForAll(
                    entity.level(),
                    48,
                    new DustParticleOptions(new Vector3f(0.8f, 0.95f, 1), 2),
                    false,
                    entity.getX(),
                    entity.getEyeY(),
                    entity.getZ(),
                    6,
                    10,
                    6,
                    2,
                    80
            );
            //applyAdverseEffects(entity);
        }


        if (spawnCorpse) {
            TitanCorpseEntity corpse = new TitanCorpseEntity(SoyEntities.TITAN_CORPSE.get(), entity.level());
            corpse.setPos(entity.getPosition(0));
            corpse.setXRot(entity.getXRot());
            corpse.setYRot(entity.getYRot());
            corpse.setYHeadRot(entity.yHeadRot);
            corpse.setDeltaMovement(entity.getDeltaMovement().scale(1.1));
            corpse.hasImpulse = true; // TODO fix this. no momentum is carried over

            TitanInstance.copyTo(ext.soy$getTitanInstance(), corpse.titanInstance);
            HardeningSystem.copyTo(hardh.soy$getHardeningSystem(), corpse.hardeningSystem);
            if (corpse.titanInstance.titan == null) {
                corpse.discard();
            } else {
                if (shouldCorpseDecay) corpse.titanInstance.isCorpse = true;
                corpse.titanInstance.setDecay(TitanInstance.START_CORPSE_DECAY);
                corpse.titanInstance.setScaleImmediate();
                SuperpowerUtil.addSuperpower(corpse, corpse.titanInstance.titan.powerPath);

                entity.level().addFreshEntity(corpse);

                if (SoyProperties.GRABBED.get(entity)) { // TODO test when colossal titan is added
                    LivingEntity grabber = GrabbingAbility.getGrabber(entity);
                    GrabbingAbility.startGrabbing(grabber, corpse);
                    SoyProperties.GRABBED.set(entity, false);
                }
            }
        }


        ext.soy$getTitanInstance().setProgress(0);
        ext.soy$getTitanInstance().setCharge(0);
        ext.soy$getTitanInstance().resetScale();

        entity.teleportTo(entity.getX(), entity.getY() + ext.soy$getTitanInstance().titan.scale * 1.4, entity.getZ());
        entity.addDeltaMovement(entity.getLookAngle().scale(-0.5));
        if (entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }

        HardeningSystem hardening = hardh.soy$getHardeningSystem();
        hardening.setAllHardening(0);
        hardening.setKnuckles(0);
        hardening.setHands(0);


        if (entity instanceof Player player) {
            player.getInventory().dropAll();
            if (ext.soy$getTitanInstance().playerInventory != null) {
                player.getInventory().load(ext.soy$getTitanInstance().playerInventory);
                ext.soy$getTitanInstance().playerInventory = null;
            }
            if (SoyConfig.Server.shouldSaveCuriosTrinketsInventory() && SoyCuriosTrinketsUtil.INSTANCE.isLoaded() && ext.soy$getTitanInstance().curiosTrinketsInventory != null) {
                SoyCuriosTrinketsUtil.INSTANCE.read(player, ext.soy$getTitanInstance().curiosTrinketsInventory);
                ext.soy$getTitanInstance().curiosTrinketsInventory = null;
            }
        }
    }

    public void tick(LivingEntity entity) {
        if (entity instanceof SoyPlayerExtension ext) {
            if (entity instanceof Player player) player.awardStat(SoyStats.TIME_AS_TITAN, 1);

            ext.soy$getTitanInstance().ticksShifted++;
            if (ext.soy$getTitanInstance().ticksShifted < 150) {
                final double x = ext.soy$getTitanInstance().ticksShifted / 10d + 10;
                final float newSteamParticleAmount = (float) (-0.08888 * Math.pow(x, 2) + 2.6666 * x);
                this.emitSteam(entity,  0.1f, (int) newSteamParticleAmount / 6);
            }

            if (entity.getHealth() != entity.getMaxHealth()) {
                float emptyHealthPercent = 1 - entity.getHealth() / entity.getMaxHealth();
                for (int count = 0; count <= 3.5 * emptyHealthPercent; count++) {
                    if (0.5 * Math.random() < emptyHealthPercent) emitSteam(entity, 0.1f * emptyHealthPercent * (float) (Math.random() - 0.5), 1);
                }
            }
        }
    }

    private void emitSteam(Entity entity, float speed, int count) {
        this.emitSteam(entity, speed, count, true);
    }
    private void emitSteam(Entity entity, float speed, int count, boolean force) {
        PlayerUtil.spawnParticleForAll(
                entity.level(),
                128,
                (ParticleOptions) SoyParticles.LARGE_STEAM.get(),
                force,
                entity.getX() + entity.getBoundingBox().getXsize() * (Math.random() - 0.5),
                entity.getY() + entity.getBoundingBox().getYsize() / 3 + 6 * (Math.random() - 0.5),
                entity.getZ() + entity.getBoundingBox().getZsize() * (Math.random() - 0.5),
                0, 0, 0,
                speed,
                count
        );
    }

    public void onFall(LivingEntity entity, float fallDistance) {
        if (fallDistance > entity.getBoundingBox().getYsize() / 4) {
            var pos = entity.getPosition(0).add(0, -1, 0);
            float strength = (float) Math.pow(fallDistance, entity.getBoundingBox().getYsize() / 18) / 5;
            entity.level().explode(entity,null, null,  pos.x, pos.y, pos.z, strength, false, SoyConfig.Server.shouldTitansExplodeBlocksOnFall() ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE, false);

            for (int count = 0; count <= Math.min(10 * strength, 50); count++) {
                PlayerUtil.spawnParticleForAll(
                        entity.level(),
                        128,
                        (ParticleOptions) SoyParticles.DIRT_CLOUD.get(),
                        true,
                        entity.getX() + entity.getBoundingBox().getXsize() * (Math.random() - 0.5),
                        entity.getY(),
                        entity.getZ() + entity.getBoundingBox().getZsize() * (Math.random() - 0.5),
                        0, 0, 0,
                        2f * strength * (float) (Math.random() - 0.5),
                        1
                );
            }

            entity.level().getEntities(null, entity.getBoundingBox().inflate(15 + 3 * Math.sqrt(strength))).forEach(e -> {
                if (e instanceof ServerPlayer player) {
                    double str = strength / Math.max(Math.sqrt(player.distanceTo(entity)), 1);
                    SoyNetwork.NETWORK.sendToPlayer(player, new ScreenShakeMessage(1000, (float) (10 + str) / 200));
                    PlayerUtil.playSound(player, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 2, 1 - strength / 50);
                }
            });

            if (entity instanceof SoyPlayerExtension ext) {
                ext.soy$getTitanInstance().exhaust((int) (fallDistance * 2));
            }
        }
    }


    public static Titan fromJson(ResourceLocation id, JsonObject json) {
        var builder = new TitanBuilder();
        builder.id = id;
        builder.variants = GsonHelper.isArrayNode(json, "variants") ? GsonHelper.getAsJsonArray(json, "variants", new JsonArray()).asList().stream().map(JsonElement::getAsString).toList() : List.of("default");
        builder.resolution = GsonHelper.getAsInt(json, "resolution", 128);
        builder.scale = GsonHelper.getAsFloat(json, "scale", 1);
        builder.maxProgress = GsonHelper.getAsInt(json, "max_progress", 50);
        builder.maxCharge = GsonHelper.getAsInt(json, "max_charge", 60);
        builder.baseEyeColor = GsonUtil.getAsColor(json, "base_eye_color", null);
        builder.stats = TitanStats.fromJson(json.getAsJsonObject("stats"));
        builder.weight = GsonHelper.getAsDouble(json, "weight", 1);
        builder.defaultMaxStamina = GsonHelper.getAsInt(json, "default_max_stamina", 100);
        builder.canSpeak = GsonHelper.getAsBoolean(json, "can_speak", false);
        return builder.create();
    }


    private static void strikeLightning(LivingEntity entity) {
        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
        lightningBolt.setPos(entity.getPosition(0));
        lightningBolt.setVisualOnly(true);
        entity.level().addFreshEntity(lightningBolt);
    }


    public static class TitanBuilder {
        public ResourceLocation id;
        public List<String> variants;
        public int resolution;
        public float scale;
        public int maxProgress;
        public int maxCharge;
        @Nullable
        public Color baseEyeColor;
        public TitanStats stats;
        public double weight;
        public int defaultMaxStamina;
        public boolean canSpeak;

        public TitanBuilder() {}

        public Titan create() {
            return new Titan(id, variants, resolution, scale, maxProgress, maxCharge, baseEyeColor, stats, weight, defaultMaxStamina, canSpeak);
        }

        public TitanBuilder withStats(TitanStats stats) {
            this.stats = stats;
            return this;
        }
    }

    public static class TitanStats {
        public float speed;
        public float jump;
        public double extraHealth;
        public double extraArmor;
        public double extraDamage;
        public int attackSpeed;

        public TitanStats() {}

        public static TitanStats fromJson(JsonObject json) {
            TitanStats stats = new TitanStats();
            stats.jump = GsonHelper.getAsFloat(json, "jump", 1);
            stats.speed = GsonHelper.getAsFloat(json, "speed", 1);
            stats.extraHealth = GsonHelper.getAsDouble(json, "extra_health", 0);
            stats.extraArmor = GsonHelper.getAsDouble(json, "extra_armor", 0);
            stats.extraDamage = GsonHelper.getAsDouble(json, "extra_attack_damage", 0);
            stats.attackSpeed = GsonHelper.getAsInt(json, "attack_speed", 20);
            return stats;
        }

        public static TitanStats fromNetwork(FriendlyByteBuf buf) {
            TitanStats stats = new TitanStats();
            stats.jump = buf.readFloat();
            stats.speed = buf.readFloat();
            stats.extraHealth = buf.readDouble();
            stats.extraArmor = buf.readDouble();
            stats.extraDamage = buf.readDouble();
            stats.attackSpeed = buf.readInt();
            return stats;
        }
        public static void toNetwork(TitanStats stats, FriendlyByteBuf buf) {
            buf.writeFloat(stats.jump);
            buf.writeFloat(stats.speed);
            buf.writeDouble(stats.extraHealth);
            buf.writeDouble(stats.extraArmor);
            buf.writeDouble(stats.extraDamage);
            buf.writeInt(stats.attackSpeed);
        }
    }

    public Component getName() {
        return Component.translatable("titan." + this.id.getNamespace() + "." + this.id.getPath());
    }

    @Override
    public String toString() {
        return "Titan{" +
                "id=" + id +
                ", variants=" + variants +
                '}';
    }

    public static Titan fromNetwork(FriendlyByteBuf buf) {
        return new Titan(
                buf.readResourceLocation(),
                buf.readList(FriendlyByteBuf::readUtf),
                buf.readInt(),
                buf.readFloat(),
                buf.readInt(),
                buf.readInt(),
                buf.readBoolean() ? new Color(buf.readInt(), buf.readInt(), buf.readInt()) : new Color(buf.readInt(), buf.readInt(), buf.readInt(), 0),
                TitanStats.fromNetwork(buf),
                buf.readDouble(),
                buf.readInt(),
                buf.readBoolean()
        );
    }
    public static void toNetwork(Titan titan, FriendlyByteBuf buf) {
        buf.writeResourceLocation(titan.id);
        buf.writeCollection(titan.variants, FriendlyByteBuf::writeUtf);
        buf.writeInt(titan.resolution);
        buf.writeFloat(titan.scale);
        buf.writeInt(titan.maxProgress);
        buf.writeInt(titan.maxCharge);
        buf.writeBoolean(titan.baseEyeColor != null); // represents if the titan has a defined base eye color
        buf.writeInt(titan.baseEyeColor != null ? titan.baseEyeColor.getRed() : 0);
        buf.writeInt(titan.baseEyeColor != null ? titan.baseEyeColor.getGreen() : 0);
        buf.writeInt(titan.baseEyeColor != null ? titan.baseEyeColor.getBlue() : 0);
        TitanStats.toNetwork(titan.stats, buf);
        buf.writeDouble(titan.weight);
        buf.writeInt(titan.defaultMaxStamina);
        buf.writeBoolean(titan.canSpeak);
    }
}
