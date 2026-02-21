package net.phantompig.soy.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<IceburstGeodeFeature> ICEBURST_GEODE = FEATURES.register("iceburst_geode", () -> new IceburstGeodeFeature(OreConfiguration.CODEC));

    public static void init() {
        FEATURES.register();
    }
}
