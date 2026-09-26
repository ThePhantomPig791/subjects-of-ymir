package net.phantompig.soy.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface SoyCompatLayer {
    boolean shouldDisableShifterAbilities(LivingEntity entity);

    boolean shouldForceUnshift(LivingEntity entity);

    /**
     * These powers will be removed from the entity while it is transformed into a titan.
     * Please keep this in mind; all powers in this list should have
     * persistent data of some kind to avoid being affected by SOY removing and re-adding them.
     */
    List<ResourceLocation> getDisabledPowerIds(LivingEntity entity);
}