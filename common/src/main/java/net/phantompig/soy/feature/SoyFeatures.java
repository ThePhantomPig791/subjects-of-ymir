package net.phantompig.soy.feature;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

import java.util.List;
import java.util.function.Supplier;

public class SoyFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.FEATURE);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.PLACED_FEATURE);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.CONFIGURED_FEATURE);

    public static final RegistrySupplier<IceburstGeodeFeature> ICEBURST_GEODE = FEATURES.register("iceburst_geode", () -> new IceburstGeodeFeature(OreConfiguration.CODEC));
    public static final ResourceKey<PlacedFeature> PLACED_ICEBURST_GEODE = createPlacedKey("iceburst_geode");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_ICEBURST_GEODE = createConfiguredKey("iceburst_geode");

    public static final Supplier<List<OreConfiguration.TargetBlockState>> ICEBURST_GEODE_REPLACEMENTS = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), Blocks.CALCITE.defaultBlockState()),
            OreConfiguration.target(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), Blocks.TUFF.defaultBlockState()),
            OreConfiguration.target(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), Blocks.SMOOTH_BASALT.defaultBlockState())
    ));


    private static ResourceKey<PlacedFeature> createPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, SubjectsOfYmir.rsrc(name));
    }
    public static ResourceKey<ConfiguredFeature<?, ?>> createConfiguredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, SubjectsOfYmir.rsrc(name));
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        registerPlaced(context, PLACED_ICEBURST_GEODE, configuredFeatures.getOrThrow(CONFIGURED_ICEBURST_GEODE),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(106), VerticalAnchor.aboveBottom(24)),
                        BiomeFilter.biome()
                )
        );
    }
    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        registerConfigured(context, CONFIGURED_ICEBURST_GEODE, ICEBURST_GEODE.get(), new OreConfiguration(ICEBURST_GEODE_REPLACEMENTS.get(), 5));
    }

    private static void registerPlaced(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void registerConfigured(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }


    public static void init() {
        FEATURES.register();
        CONFIGURED_FEATURES.register();
        PLACED_FEATURES.register();
    }
}
