package net.phantompig.soy.power.ability;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.armor.ArmorSystemHolder;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.property.*;

import java.util.UUID;

public class SprintChargeAbility extends Ability {
    public static final PalladiumProperty<Double> MAX_SPEED = new DoubleProperty("max_speed").configurable("The maximum speed increase that can be achieved by sprinting");
    public static final PalladiumProperty<Double> SPEED_PER_TIME = new DoubleProperty("speed_per_second").configurable("The amount of speed to add per time while sprinting");
    public static final PalladiumProperty<Integer> TIME = new IntegerProperty("time").configurable("The time between each speed increase");
    public static final PalladiumProperty<UUID> ATTRIBUTE_UUID = new UUIDProperty("uuid").configurable("Used for the movement speed attribute");
    public static final PalladiumProperty<String> BLOCK_ABILITY = new StringProperty("block_ability").configurable("The ability key for a block ability that, when active, will enable charging");

    public SprintChargeAbility() {
        this.withProperty(MAX_SPEED, 0.1);
        this.withProperty(SPEED_PER_TIME, 0.01);
        this.withProperty(TIME, 10);
        this.withProperty(ATTRIBUTE_UUID, UUID.fromString("73c2510b-6bdd-4378-9a29-c23fcb4d8d95"));
        this.withProperty(BLOCK_ABILITY, "block");
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && entity instanceof SoyPlayerExtension ext && entity instanceof ArmorSystemHolder arh && arh.soy$getArmorSystem().getLower() < 2) {
            UUID uuid = entry.getProperty(ATTRIBUTE_UUID);
            boolean blocking = holder.getAbilities().get(entry.getProperty(BLOCK_ABILITY)) != null && holder.getAbilities().get(entry.getProperty(BLOCK_ABILITY)).getProperty(BlockAbility.TIMER) > 0;
            if (entity.isSprinting() && ext.soy$getTitanInstance().getStamina() > 1) {
                if (!blocking) {
                    double value;
                    var attribute = entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid);
                    if (attribute == null) {
                        value = 0;
                    } else {
                        value = attribute.getAmount();
                    }

                    if ((entry.getEnabledTicks() + 1) % entry.getProperty(TIME) == 0) {
                        if (attribute != null) {
                            entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(uuid);
                        }
                        if (value >= entry.getProperty(MAX_SPEED)) value = entry.getProperty(MAX_SPEED) - entry.getProperty(SPEED_PER_TIME);
                        entity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(uuid, "sprint charge speed", value + entry.getProperty(SPEED_PER_TIME), AttributeModifier.Operation.ADDITION));
                    }

                    if (value > entry.getProperty(SPEED_PER_TIME) * 2 || value >= entry.getProperty(MAX_SPEED)) {
                        ext.soy$getTitanInstance().exhaustSafe(1);
                    }
                }
            } else if (!blocking && entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid) != null) {
                entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(uuid);
            }
            if (blocking && !entity.level().isClientSide() && entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid) != null && !entity.isInWater() && !entity.isInLava() && !entity.isInPowderSnow) {
                double speed = entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
                if (entry.getEnabledTicks() % 2 == 0 && speed > 0.15) {
                    entity.level().explode(entity, null, null, entity.getX(), entity.getY() + entity.getEyeHeight() / 4, entity.getZ(), (float) speed * 20, false, Level.ExplosionInteraction.BLOCK, false);
                    entity.level().explode(entity, null, null, entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), (float) speed * 25, false, Level.ExplosionInteraction.BLOCK, false);
                    ext.soy$getTitanInstance().exhaust(5);
                    ext.soy$getTitanInstance().regainStaminaCooldown = 30;
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
        return "Increases a speed attribute while sprinting and allows a charge to be enabled when a referenced block ability is enabled";
    }
}
