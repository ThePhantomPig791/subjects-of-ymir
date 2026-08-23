package net.phantompig.soy.power.ability;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.property.*;

import java.util.UUID;

public class SprintChargeAbility extends Ability {
    public static final PalladiumProperty<ResourceLocation> SHED_POWER = new ResourceLocationProperty("shed_power").configurable("Shed source power (leave null for current power)");
    public static final PalladiumProperty<String> SHED_ABILITY = new StringProperty("shed_ability").configurable("Shed source ability - should be a \"subjects_of_ymir:shed\" ability");
    public static final PalladiumProperty<UUID> ATTRIBUTE_UUID = new UUIDProperty("uuid").configurable("Used for the movement speed attribute");

    public SprintChargeAbility() {
        this.withProperty(SHED_POWER, null);
        this.withProperty(SHED_ABILITY, "example");
        this.withProperty(ATTRIBUTE_UUID, UUID.fromString("73c2510b-6bdd-4378-9a29-c23fcb4d8d95"));
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled) {
            UUID uuid = entry.getProperty(ATTRIBUTE_UUID);
            boolean blocking = holder.getAbilities().get("block") != null && holder.getAbilities().get("block").getProperty(BlockAbility.TIMER) > 0;
            if (entity.isSprinting()) {
                if (!blocking) {
                    if (entry.getEnabledTicks() % 20 == 0) {
                        Power power = entry.getProperty(SHED_POWER) == null ? holder.getPower() : PowerManager.getInstance(entity.level()).getPower(entry.getProperty(SHED_POWER));
                        if (PowerManager.getPowerHandler(entity).isEmpty()) return;
                        int shed = PowerManager.getPowerHandler(entity).get().getPowerHolder(power).getAbilities().get(entry.getProperty(SHED_ABILITY)).getProperty(ShedAbility.VALUE);
                        double max = 0.2 * (1 - shed / 15d);

                        if (entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid) == null) {
                            entity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(uuid, "sprint charge speed", max / 10, AttributeModifier.Operation.ADDITION));
                        } else {
                            double value = entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid).getAmount();
                            if (value >= max) return;
                            entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(uuid);
                            entity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(uuid, "sprint charge speed", value + max / 10, AttributeModifier.Operation.ADDITION));
                        }
                    }
                }
                if (entity instanceof SoyPlayerExtension ext) {
                    if (entry.getEnabledTicks() % 10 == 0) {
                        Power power = entry.getProperty(SHED_POWER) == null ? holder.getPower() : PowerManager.getInstance(entity.level()).getPower(entry.getProperty(SHED_POWER));
                        if (PowerManager.getPowerHandler(entity).isEmpty()) return;
                        int shed = PowerManager.getPowerHandler(entity).get().getPowerHolder(power).getAbilities().get(entry.getProperty(SHED_ABILITY)).getProperty(ShedAbility.VALUE);

                        if (shed < 13) {
                            ext.soy$getTitanInstance().exhaustSafe(50 * (13 - shed));
                        }
                    }
                }
            } else if (!blocking && entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid) != null) {
                entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(uuid);
            }
            if (blocking && !entity.level().isClientSide() && entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid) != null && !entity.isInWater() && !entity.isInLava() && !entity.isInPowderSnow) {
                double value = entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid).getAmount();
                if (entry.getEnabledTicks() % 2 == 0 && value > 0.02) {
                    entity.level().explode(entity, null, null, entity.getX(), entity.getY() + entity.getEyeHeight() / 4, entity.getZ(), (float) value * 30, false, Level.ExplosionInteraction.BLOCK, false);
                    entity.level().explode(entity, null, null, entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), (float) value * 30, false, Level.ExplosionInteraction.BLOCK, false);
                    if (entity instanceof SoyPlayerExtension ext) {
                        ext.soy$getTitanInstance().exhaust(100); // in addition to the 50 from earlier
                        ext.soy$getTitanInstance().regainStaminaCooldown = 30;
                    }
                    PlayerUtil.spawnParticleForAll(
                            entity.level(),
                            128,
                            (SimpleParticleType) SoyParticles.DIRT_CLOUD.get(),
                            true,
                            entity.getX(),
                            entity.getY() + entity.getBoundingBox().getYsize() / 5,
                            entity.getZ(),
                            (float) (Math.random() * entity.getBoundingBox().getXsize() / 3),
                            (float) (Math.random() * entity.getBoundingBox().getYsize() / 4),
                            (float) (Math.random() * entity.getBoundingBox().getZsize() / 3),
                            0.2f,
                            4
                    );
                    PlayerUtil.spawnParticleForAll(
                            entity.level(),
                            128,
                            (SimpleParticleType) SoyParticles.LARGE_STEAM.get(),
                            true,
                            entity.getX(),
                            entity.getY() + entity.getBoundingBox().getYsize() / 3,
                            entity.getZ(),
                            (float) (Math.random() * entity.getBoundingBox().getXsize() / 3),
                            (float) (Math.random() * entity.getBoundingBox().getYsize() / 3),
                            (float) (Math.random() * entity.getBoundingBox().getZsize() / 3),
                            0.08f,
                            3
                    );
                    entity.level().getEntities(entity, entity.getBoundingBox().inflate(3)).forEach(e -> {
                        e.addDeltaMovement(entity.getDeltaMovement().scale(2 / e.getBoundingBox().getYsize()));
                        if (e instanceof ServerPlayer sp) {
                            sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
                        }
                    });
                    if (entity instanceof SoyPlayerExtension ext) {
                        ext.soy$getTitanInstance().exhaust(1);
                    }
                }
            }
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        UUID uuid = entry.getProperty(ATTRIBUTE_UUID);
        if (entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid) != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(uuid);
        }
    }

    @Override
    public String getDocumentationDescription() {
        return "Increases a speed attribute while sprinting, with a max taken from a referenced \"subjects_of_ymir:shed\" ability";
    }
}
