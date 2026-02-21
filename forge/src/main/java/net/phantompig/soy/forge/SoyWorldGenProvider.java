package net.phantompig.soy.forge;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.feature.SoyFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SoyWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, SoyFeatures::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, SoyFeatures::bootstrapPlaced);

    public SoyWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(SubjectsOfYmir.MOD_ID));
    }
}
