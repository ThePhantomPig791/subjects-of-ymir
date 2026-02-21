package net.phantompig.soy.fabric;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.phantompig.soy.SubjectsOfYmir;
import net.fabricmc.api.ModInitializer;
import net.phantompig.soy.feature.SoyFeatures;

public class SubjectsOfYmirFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SubjectsOfYmir.init();
        registerPlacedFeatures();
    }

    private static void registerPlacedFeatures() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.VEGETAL_DECORATION, SoyFeatures.PLACED_ICEBURST_GEODE);
    }
}
