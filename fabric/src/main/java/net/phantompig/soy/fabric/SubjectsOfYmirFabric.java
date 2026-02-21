package net.phantompig.soy.fabric;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.phantompig.soy.SubjectsOfYmir;
import net.fabricmc.api.ModInitializer;

public class SubjectsOfYmirFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SubjectsOfYmir.init();
        registerPlacedFeatures();

        SubjectsOfYmir.LOGGER.info("Subjects of Ymir initialized on Fabric!");
    }

    private static void registerPlacedFeatures() {
        BiomeModifications.addFeature(BiomeSelectors.tag(TagKey.create(Registries.BIOME, SubjectsOfYmir.rsrc("has_iceburst_geode"))), GenerationStep.Decoration.UNDERGROUND_DECORATION, ResourceKey.create(Registries.PLACED_FEATURE, SubjectsOfYmir.rsrc("iceburst_geode")));
    }
}
