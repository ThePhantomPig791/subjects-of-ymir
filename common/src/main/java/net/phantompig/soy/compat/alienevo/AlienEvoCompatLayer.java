package net.phantompig.soy.compat.alienevo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.compat.SoyCompatLayer;

import java.util.List;

public class AlienEvoCompatLayer implements SoyCompatLayer {
    public static final ResourceLocation OMNITRIX_ITEM_ID = new ResourceLocation("alienevo:prototype_omnitrix");

    @Override
    public boolean shouldDisableShifterAbilities(LivingEntity entity) {
        return isAlien(entity);
    }

    @Override
    public boolean shouldForceUnshift(LivingEntity entity) {
        return isAlien(entity);
    }

    @Override
    public List<ResourceLocation> getDisabledPowerIds(LivingEntity entity) {
        return List.of(new ResourceLocation("alienevo:prototype_omnitrix"));
    }

    public static boolean isAlien(LivingEntity entity) {
        return entity.getTags().contains("AlienEvo.Transformation");
    }
}