package net.phantompig.soy.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface SoyCompatLayer {
    String getName();

    default boolean shouldDisableShifterAbilities(LivingEntity entity) {
        return false;
    }

    default boolean shouldForceUnshift(LivingEntity entity) {
        return false;
    }

    default boolean shouldDisableBite(LivingEntity entity) {
        return false;
    }

    /**
     * These powers will be removed from the entity while it is transformed into a titan.
     * Please keep this in mind; all powers in this list should have some kind of
     * persistent data to avoid being negatively affected by SOY removing and re-adding them.
     */
    default List<ResourceLocation> getDisabledPowerIds(LivingEntity entity) {
        return List.of();
    }
}