package net.phantompig.soy.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyPaintingVariants {
    public static DeferredRegister<PaintingVariant> PAINTING_VARIANTS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.PAINTING_VARIANT);

    public static RegistrySupplier<PaintingVariant> DEVIL = PAINTING_VARIANTS.register("devil", () -> new PaintingVariant(64, 32));
    public static RegistrySupplier<PaintingVariant> DINA = PAINTING_VARIANTS.register("dina", () -> new PaintingVariant(16, 16));
    public static RegistrySupplier<PaintingVariant> BEGINNING = PAINTING_VARIANTS.register("beginning", () -> new PaintingVariant(16, 32));

    public static void init() {
        PAINTING_VARIANTS.register();
    }
}
