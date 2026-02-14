package net.phantompig.soy.power.ability;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.entity.TitanCorpseEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.titan.TitanInstance;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.SuperpowerUtil;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.icon.ItemIcon;

public class TitanShiftAbility extends Ability {

    public TitanShiftAbility() {
        this.withProperty(ICON, new ItemIcon(Items.BONE));
        this.withProperty(HIDDEN_IN_BAR, false);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        var titanInstance = ((SoyPlayerExtension) entity).getTitanInstance();
        if (titanInstance.titan == null) return;
        int progress = titanInstance.getProgress();
        int charge = titanInstance.getCharge();
        final int maxCharge = titanInstance.titan.maxCharge;

        if (enabled && charge < maxCharge) {
            titanInstance.setCharge(++charge);
            // SubjectsOfYmir.LOGGER.info("charge {}", charge);
        } else {
            if (charge >= 1) {
                // SubjectsOfYmir.LOGGER.info("pr {}" , titanInstance.getProgress());
                if (progress < titanInstance.titan.maxProgress && !titanInstance.forceUnshift) {
                    if (progress < 0) titanInstance.setProgress(0);
                    if (titanInstance.getProgress() == 0) {
                        // first shifting tick
                        titanInstance.titan.startShift(entity, charge);
                        titanInstance.startScaleChange();
                        titanInstance.canShiftTicks = 0;

                        if (entity instanceof Player player) {
                            titanInstance.playerInventory = player.getInventory().save(new ListTag());
                            player.getInventory().clearContent();

                            PlayerUtil.playSound(player, entity.getX(), entity.getY(), entity.getZ(), SoySounds.SHIFT_LOCAL.get(), SoundSource.PLAYERS);
                        }
                        PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 64, SoySounds.SHIFT_LOCAL.get(), SoundSource.PLAYERS);

                        strikeLightning(entity);
                        strikeLightning(entity);
                        strikeLightning(entity);

                        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 10, true, false));
                        entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20, 10, true, false));
                    }

                    // each shifting tick
                    titanInstance.setProgress(++progress);

                    entity.level().explode(entity, null, null, entity.getX(), entity.getEyeY(), entity.getZ(), (float) Math.sqrt(charge / 5f), false, Level.ExplosionInteraction.MOB, false).explode();

                } else {
                    // completed shift
                    titanInstance.titan.completedShift(entity, charge);
                    titanInstance.setCharge(0);
                }
            }
        }

        if (progress == titanInstance.titan.maxProgress) {
            titanInstance.titan.tick(entity);
        }

        // triggered from the unshift ability. i moved it here so you can "cancel" mid-shift, the other option was to make another property for tracking cancelled shifts
        if (titanInstance.forceUnshift) {
            titanInstance.forceUnshift = false;

            // corpse
            TitanCorpseEntity corpse = new TitanCorpseEntity(SoyEntities.TITAN_CORPSE.get(), entity.level());
            corpse.setPos(entity.getPosition(0));
            corpse.setXRot(entity.getXRot());
            corpse.setYRot(entity.getYRot());
            corpse.setYHeadRot(entity.yHeadRot);
            corpse.setDeltaMovement(entity.getDeltaMovement());

            TitanInstance.copyPropertiesTo(titanInstance, corpse.titanInstance);
            if (corpse.titanInstance.titan == null) {
                corpse.discard();
                return;
            }
            corpse.titanInstance.isCorpse = true;
            corpse.titanInstance.setScaleImmediate();
            SuperpowerUtil.addSuperpower(corpse, corpse.titanInstance.titan.powerPath);

            entity.level().addFreshEntity(corpse);

            // shifter entity
            if (entity instanceof Player player) {
                player.getInventory().dropAll();
                player.getInventory().load(titanInstance.playerInventory);
                titanInstance.playerInventory = null;
            }

            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
            entity.heal(6);

            titanInstance.setProgress(0);
            titanInstance.setCharge(0);
            titanInstance.resetScale();
            titanInstance.titan.unshift(entity);

            entity.teleportTo(entity.getX(), entity.getY() + titanInstance.titan.scale * 1.4, entity.getZ());
            entity.addDeltaMovement(entity.getLookAngle().scale(-0.5));
            if (entity instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        }
    }

    private static void strikeLightning(LivingEntity entity) {
        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
        lightningBolt.setPos(entity.getPosition(0));
        lightningBolt.setVisualOnly(true);
        entity.level().addFreshEntity(lightningBolt);
    }


    @Override
    public String getDocumentationDescription() {
        return "Shifts into a titan; charges up while enabled then shifts once disabled";
    }
}
