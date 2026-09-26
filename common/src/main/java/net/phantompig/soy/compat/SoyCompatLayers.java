package net.phantompig.soy.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.compat.alienevo.AlienEvoCompatLayer;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.power.SuperpowerUtil;

import java.util.ArrayList;
import java.util.List;

public class SoyCompatLayers {
    public static final List<SoyCompatLayer> layers = new ArrayList<>();

    public static void init() {
        layers.clear();
        if (BuiltInRegistries.ITEM.containsKey(AlienEvoCompatLayer.OMNITRIX_ITEM_ID)) {
            layers.add(new AlienEvoCompatLayer());
            SubjectsOfYmir.LOGGER.info("Loaded compatibility layer for Alien Evolution");
        }
        SubjectsOfYmir.LOGGER.info("Loaded {} compatibility layers", layers.size());
    }


    public static void onShift(LivingEntity entity) {
        final List<ResourceLocation> ids = getDisabledPowerIds(entity);
        final ArrayList<ResourceLocation> removed = new ArrayList<>();
        SuperpowerUtil.getSuperpowerIds(entity).forEach(rl -> {
            if (ids.contains(rl)) {
                SuperpowerUtil.removeSuperpower(entity, rl);
                removed.add(rl);
            }
        });
        removed.addAll(SoyProperties.DISABLED_POWERS.get(entity));
        SoyProperties.DISABLED_POWERS.set(entity, removed);
    }

    public static void onUnshift(LivingEntity entity) {
        SoyProperties.DISABLED_POWERS.get(entity).forEach(rl -> {
            SuperpowerUtil.addSuperpower(entity, rl);
        });
        SoyProperties.DISABLED_POWERS.set(entity, List.of());
    }


    public static boolean shouldDisableShifterAbilities(LivingEntity entity) {
        var bl = layers.stream().anyMatch(layer -> layer.shouldDisableShifterAbilities(entity));
        SubjectsOfYmir.LOGGER.info("should disable: {}", bl);
        return bl;
    }

    public static boolean shouldForceUnshift(LivingEntity entity) {
        return layers.stream().anyMatch(layer -> layer.shouldForceUnshift(entity));
    }

    public static List<ResourceLocation> getDisabledPowerIds(LivingEntity entity) {
        return layers.stream().map(layer -> layer.getDisabledPowerIds(entity)).flatMap(List::stream).toList();
    }
}