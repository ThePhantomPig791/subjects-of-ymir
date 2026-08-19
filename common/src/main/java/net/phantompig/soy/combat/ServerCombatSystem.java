package net.phantompig.soy.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.entity.SoyDamageSources;
import net.phantompig.soy.item.BladeHandleItem;
import net.phantompig.soy.item.BladeItem;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.network.*;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.titan.hardening.HardeningSystem;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.phantompig.soy.util.ShapeUtil;
import net.threetag.palladium.util.EntityUtil;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladiumcore.util.Platform;
import org.joml.Vector3f;

import java.util.List;

public class ServerCombatSystem {
    public final ServerPlayer player;
    public SoyPlayerExtension extension;

    public int attackTimer, nextStageTimer, cooldown;

    public int attackStage = 1; // not zero based!!! (because of how armin named the animations, i'm too lazy to change it)
    public AttackType attackType = AttackType.PUNCH;

    public ServerCombatSystem(ServerPlayer player) {
        this.player = player;
        if (player instanceof SoyPlayerExtension ext) {
            extension = ext;
        }
    }

    public HardeningSystem getHardening() {
        if (player instanceof HardeningSystemHolder hardh) {
            return hardh.soy$getHardeningSystem();
        }
        return null;
    }


    private float getExtraStrength() {
        float sum = 0;
        var effect = this.player.getEffect(MobEffects.DAMAGE_BOOST);
        if (effect != null) sum += 0.5f * effect.getAmplifier();
        sum += extension.getTitanInstance().strengthIncreases.values().stream().reduce(Float::sum).orElse(0f);
        return sum;
    }
    private int getMaxAttackTime() {
        if (extension.getTitanInstance().titan == null) return 0;
        return extension.getTitanInstance().titan.stats.attackSpeed + getHardening().getAttackTimeIncrease();
    }

    public enum AttackType {
        PUNCH(3),
        GROUND(3),
        KICK(2);

        final int maxAttackStage;
        AttackType(int maxAttackStage) {
            this.maxAttackStage = maxAttackStage;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public void attack() {
        setSwingingFists(false);
        setSwingingLegs(false);
        if (extension.getTitanInstance().titan == null) return;
        final int maxAttackTime = getMaxAttackTime();
        if (attackTimer >= maxAttackTime * 12 / 20f || cooldown > 0 || extension == null || this.player.isSpectator()) {
            attackStage = 1;
            if (cooldown == 0) sendUpdateAttackTicker(-(cooldown = maxAttackTime));
            return;
        }

        player.setYBodyRot(player.getYHeadRot());

        if (player.getXRot() > 50) {
            attackType = AttackType.KICK;
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    getMaxAttackTime(),
                    4,
                    false,
                    false
            ));
            setSwingingLegs(true);
        } else if (player.getXRot() > 30) {
            attackType = AttackType.GROUND;
            player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    getMaxAttackTime() / 2,
                    0,
                    false,
                    false
            ));
            setSwingingFists(true);
        } else {
            attackType = AttackType.PUNCH;
            setSwingingFists(true);
        }

        if (nextStageTimer > 0) {
            if (++attackStage > attackType.maxAttackStage) attackStage = 1;
        } else attackStage = 1;

        String animationId = attackType.toString() + attackStage;

        player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(150)).forEach(e -> {
            if (e instanceof ServerPlayer pl) SoyNetwork.NETWORK.sendToPlayer(pl, new TitanAttackAnimationMessage(player, animationId));
        });

        nextStageTimer = (int) (1.2 * attackTimer) + 20;
        sendUpdateAttackTicker((int) (-(attackTimer = maxAttackTime) * 12f / 20));
        sendUpdateStageTimer(nextStageTimer);
    }

    public void startHolding() {
        if (this.player.getFoodData().getFoodLevel() > 0) SoyProperties.ODM_HOLD_ATTACK_INCREASING.set(this.player, true);
    }

    public void stopHolding() {
        SoyProperties.ODM_HOLD_ATTACK_INCREASING.set(this.player, false);

        ItemStack mainHandItem = this.player.getMainHandItem();
        ItemStack offHandItem = this.player.getOffhandItem();
        if (mainHandItem.getItem() instanceof BladeHandleItem handleMain && offHandItem.getItem() instanceof BladeHandleItem handleOff) {
            if (!(handleMain.getStack(mainHandItem).getItem() instanceof BladeItem || handleOff.getStack(offHandItem).getItem() instanceof BladeItem)) return;
        }

        if (SoyProperties.ODM_HOLD_ATTACK.get(this.player) == 5) {
            List<Entity> entitiesInRange = getEntitiesInOdmRange();
            entitiesInRange.forEach(e -> {
                if (e instanceof LivingEntity living) {
                    if (mainHandItem.getItem() instanceof BladeHandleItem handle) {
                        handle.hurtEnemy(mainHandItem, living, this.player);
                    }
                    if (offHandItem.getItem() instanceof BladeHandleItem handle) {
                        handle.hurtEnemy(offHandItem, living, this.player);
                    }
                }
            });

            Vec3 sweepLocation = this.player.getEyePosition().add(this.player.getLookAngle().scale(2));
            PlayerUtil.playSoundToAll(this.player.level(), sweepLocation.x, sweepLocation.y, sweepLocation.z, 32, SoySounds.BLADE_SLASH.get(), SoundSource.PLAYERS, 1, 0.8f + (float) (0.3f * Math.random()));
            PlayerUtil.playSoundToAll(this.player.level(), sweepLocation.x, sweepLocation.y, sweepLocation.z, 32, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5f, 1.6f);
            PlayerUtil.spawnParticleForAll(this.player.level(), 32, ParticleTypes.SWEEP_ATTACK, false, sweepLocation.x, sweepLocation.y, sweepLocation.z, 0, 0, 0, 0, 1);
            PlayerUtil.spawnParticleForAll(this.player.level(), 16, ParticleTypes.CLOUD, false, sweepLocation.x, sweepLocation.y, sweepLocation.z, 0, 0, 0, 0.1f, (int) (4 * Math.random()));

            this.player.causeFoodExhaustion(1);
        }
    }

    public void tickOdmHoldAttack() {
        if (!this.player.getMainHandItem().is(SoyItems.BLADE_HANDLE.get()) || !this.player.getOffhandItem().is(SoyItems.BLADE_HANDLE.get())) {
            stopHolding();
        }
        int odmHoldAttackProgress = SoyProperties.ODM_HOLD_ATTACK.get(this.player);
        if (SoyProperties.ODM_HOLD_ATTACK_INCREASING.get(this.player)) {
            if (odmHoldAttackProgress < 5) {
                SoyProperties.ODM_HOLD_ATTACK.set(this.player, odmHoldAttackProgress + 1);
            }
            if (odmHoldAttackProgress == 5 && !getEntitiesInOdmRange().isEmpty()) {
                this.stopHolding();
            }
        } else {
            if (odmHoldAttackProgress > 0) {
                SoyProperties.ODM_HOLD_ATTACK.set(this.player, odmHoldAttackProgress - 1);
            }
        }
    }

    public List<Entity> getEntitiesInOdmRange() {
        List<Entity> list = this.player.level().getEntities(this.player, this.player.getBoundingBox().move(this.player.getLookAngle().scale(2)).inflate(2, 0, 2));
        list.removeIf(e -> !(e instanceof LivingEntity));
        return list;
    }

    public void tick() {
        tickOdmHoldAttack();

        if (extension.getTitanInstance().titan == null) return;
        if (extension.getTitanInstance().getProgress() == 0) {
            attackTimer = nextStageTimer = cooldown = 0;
        } else {
            if (attackTimer > 0) {
                attackTimer--;
            }
            if (attackTimer == Mth.lerpInt(12 / 20f, 0, getMaxAttackTime())) {
                attackEffect();
            }
            if (nextStageTimer > 0) nextStageTimer--;
            if (cooldown > 0) cooldown--;
        }
    }

    public void attackEffect() {
        if (extension == null || extension.getTitanInstance().titan == null) {
            return;
        }

        double staminaToUse = player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        final boolean hit =
                player.level().getBlockCollisions(player, player.getBoundingBox().inflate(-1, -2, -1).move(player.getLookAngle().scale(5))).iterator().hasNext()
                || !player.level().getEntities(player, player.getBoundingBox().inflate(-1, -2, -1).move(player.getLookAngle().scale(5))).isEmpty();
        if (hit) {
            player.level().getEntities(null, player.getBoundingBox().inflate(64)).forEach(e -> {
                if (e instanceof ServerPlayer p) {
                    double strength = 20 / Math.max(Math.sqrt(player.distanceTo(p)), 1) / p.getBoundingBox().getYsize();
                    SoyNetwork.NETWORK.sendToPlayer(p, new ScreenShakeMessage(1200, (float) (5 + strength) / 200));
                }
            });
        }
        if (attackType == AttackType.PUNCH) {
            if (attackStage < 3) {
                if (hit) explodeInFrontPartialLooking(1.5f, 5, 0, -0.2f);
            } else {
                if (hit) explodeInFrontPartialLooking(2, 5, 0, -0.1f);

                cooldown = getMaxAttackTime() * 3 / 2;
                nextStageTimer = 0;
                sendUpdateAttackTicker(-cooldown);
                sendUpdateStageTimer(nextStageTimer);

                staminaToUse *= 1.25;
            }
        }
        if (attackType == AttackType.GROUND) {
            if (attackStage < 3) {
                if (hit) explodeInFrontPartialLooking(1.5f, 6, 0.1f, -0.7f);
            } else {
                if (hit) explodeInFrontPartialLooking(2.5f, 7, 0.1f, -0.7f);

                cooldown = getMaxAttackTime() * 2;
                nextStageTimer = 0;
                sendUpdateAttackTicker(-cooldown);
                sendUpdateStageTimer(nextStageTimer);

                staminaToUse *= 1.5;
            }
        }
        if (attackType == AttackType.KICK) {
            if (hit) {
                explodeInFrontFlat(1.5f, 4, -0.85f, 0);
            }

            cooldown = (int) (getMaxAttackTime() * 1.5f);
            nextStageTimer += nextStageTimer / 2;
            sendUpdateAttackTicker(-cooldown);
            sendUpdateStageTimer(nextStageTimer);
        }

        if (hit) exhaust((int) staminaToUse);
        PlayerUtil.playSoundToAll(player.level(), player.getX(), player.getEyeY(), player.getZ(), 32, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 3, (float) (0.8 - staminaToUse / 25));
        setSwingingFists(false);
        setSwingingLegs(false);
    }

    public void exhaust(int stamina) {
        extension.getTitanInstance().exhaust(stamina);
    }

    public void sendUpdateAttackTicker(int ticks) {
        SoyNetwork.NETWORK.sendToPlayer(this.player, new SetAttackTickerMessage(ticks));
    }
    public void sendUpdateStageTimer(int ticks) {
        SoyNetwork.NETWORK.sendToPlayer(this.player, new SetNextAttackStageTimerMessage(ticks));
    }

    public void setSwingingFists(boolean s) {
        SoyProperties.SWINGING_FISTS.set(this.player, s);
    }
    public void setSwingingLegs(boolean s) {
        SoyProperties.SWINGING_LEGS.set(this.player, s);
    }

    public void explodeInFrontPartialLooking(float strength, float distance, float startHeightOffset, float endHeightOffset) {
        strength *= (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 5;
        strength += getExtraStrength();

        var start = player.getEyePosition().add(0, startHeightOffset * player.getEyeHeight(), 0);
        var delta = player.getLookAngle().multiply(1, 0.5, 1).normalize().scale(distance);
        HitResult hit = EntityUtil.rayTraceWithEntities(player, start, start.add(delta).add(0, endHeightOffset * player.getEyeHeight(), 0), distance, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, en -> true);
        Vec3 hitPos = hit.getLocation();

        explodeAndFling(strength, hitPos, start);
    }

    public void explodeInFrontFlat(float strength, float distance, float startHeightOffset, float endHeightOffset) {
        strength *= (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 5;
        strength += getExtraStrength();

        var start = player.getEyePosition().add(0, startHeightOffset * player.getEyeHeight(), 0);
        final float yRot = (float) Math.toRadians(player.yBodyRot + 90);
        var end = new Vec3(Mth.cos(yRot), 0, Math.sin(yRot)).scale(distance);
        HitResult hit = EntityUtil.rayTraceWithEntities(player, start, start.add(end).add(0, endHeightOffset * player.getEyeHeight(), 0), distance, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, en -> true);
        Vec3 hitPos = hit.getLocation();

        explodeAndFling(strength, hitPos, start);
    }

    public void explodeAndFling(float strength, Vec3 hitPos, Vec3 start) {
        Vec3 delta = hitPos.subtract(start);
        AABB box = player.getBoundingBox().setMinY(player.getBoundingBox().minY + player.getBoundingBox().getYsize() * 0.5f).move(delta);
        box = box.move(hitPos.subtract(box.getCenter()).scale(0.5));

        var center = box.getCenter();
        PlayerUtil.spawnParticleForAll(
                player.level(), 32,
                new BlockParticleOption(ParticleTypes.BLOCK, player.level().getBlockState(BlockPos.containing(center))),
                false,
                center.x, center.y, center.z,
                (float) box.getXsize() / 2, (float) box.getYsize() / 2, (float)  box.getZsize() / 2,
                0.5f, (int) (box.getXsize() * box.getYsize() * box.getZsize() / 3)

        );
        PlayerUtil.spawnParticleForAll(
                player.level(), 32,
                (ParticleOptions) SoyParticles.DIRT_CLOUD.get(),
                false,
                center.x, center.y, center.z,
                (float) box.getXsize() / 2, (float) box.getYsize() / 10, (float)  box.getZsize() / 2,
                0.2f, 5

        );
        player.level().explode(player, SoyDamageSources.titanPunch(player.level(), player), null, hitPos.x, hitPos.y, hitPos.z, strength, false, getExplosionInteraction(), false);

        player.level().getEntities(player, box).forEach(e -> {
            e.hurt(SoyDamageSources.titanPunch(player.level(), player), strength);
            if (e instanceof LivingEntity livingE && livingE.isDeadOrDying()) {
                PlayerUtil.playSoundToAll(player.level(), e.getX(), e.getEyeY(), e.getZ(), 64, SoySounds.TITAN_KILL.get(), SoundSource.PLAYERS, 2, 0.9f + (float) (0.1 * Math.random()));
            }
            var knockback = delta.normalize().scale(strength * 10 / Math.pow(e.getBoundingBox().getYsize(), 1.25));
            var projectedMovement = ShapeUtil.vectorProjection(player.getDeltaMovement(), knockback);
            e.addDeltaMovement(knockback.add(projectedMovement));
            if (e instanceof ServerPlayer sp) {
                sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
            }
        });

        if (!Platform.isProduction()) {
            ShapeUtil.highlightVector(player.level(), player.position(), delta);
            ShapeUtil.outlineBox(player.level(), box, new Vector3f(0.5f, 0, 0));
        }
    }

    private Level.ExplosionInteraction getExplosionInteraction() {
        return SoyConfig.Server.shouldTitansExplodeBlocksOnAttack() ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE;
    }
}
