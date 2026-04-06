package net.phantompig.soy.power.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
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
        if (enabled && entry.getEnabledTicks() % 20 == 0) {
            Power power = entry.getProperty(SHED_POWER) == null ? holder.getPower() : PowerManager.getInstance(entity.level()).getPower(entry.getProperty(SHED_POWER));
            if (PowerManager.getPowerHandler(entity).isEmpty()) return;
            double max = 0.2 * (1 - PowerManager.getPowerHandler(entity).get().getPowerHolder(power).getAbilities().get(entry.getProperty(SHED_ABILITY)).getProperty(ShedAbility.VALUE) / 15d);

            UUID uuid = entry.getProperty(ATTRIBUTE_UUID);
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

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        UUID uuid = entry.getProperty(ATTRIBUTE_UUID);
        if (entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid) != null) {
            if (!entity.level().isClientSide() && !entity.isInWater() && !entity.isInLava() && !entity.isInPowderSnow) {
                double value = entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid).getAmount();
                if (value > 0.02) {
                    entity.level().explode(entity, entity.getX(), entity.getY() + entity.getEyeHeight() / 2, entity.getZ(), (float) value * 40, Level.ExplosionInteraction.BLOCK);
                    entity.level().explode(entity, entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), (float) value * 40, Level.ExplosionInteraction.BLOCK);
                }
            }
            entity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(uuid);
        }
    }

    @Override
    public String getDocumentationDescription() {
        return "Increases a speed attribute while sprinting, with a max taken from a referenced \"subjects_of_ymir:shed\" ability";
    }
}
