package net.phantompig.soy.fluid;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.FLUID);

    public static final RegistrySupplier<Fluid> SPINAL_FLUID = FLUIDS.register("spinal_fluid", SpinalFluid.Source::new);
    public static final RegistrySupplier<Fluid> FLOWING_SPINAL_FLUID = FLUIDS.register("flowing_spinal_fluid", SpinalFluid.Flowing::new);

    public static void init() {
        FLUIDS.register();
        SoyFluidTags.init();
    }
}
