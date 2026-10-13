package net.phantompig.soy.forge.fluid;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.fluid.SpinalFluid;

public class SoyFluidTypesForge {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, SubjectsOfYmir.MOD_ID);

    public static final RegistryObject<FluidType> SPINAL_FLUID = FLUID_TYPES.register("spinal_fluid", () -> new BaseFluidType(
            SpinalFluid.TEXTURE_STILL, SpinalFluid.TEXTURE_FLOW, SpinalFluid.TEXTURE_OVERLAY,
            SpinalFluid.COLOR, SpinalFluid.FOG,
            FluidType.Properties.create()
                    .descriptionId("block.subjects_of_ymir.spinal_fluid")
                    .fallDistanceModifier(0F)
                    .canExtinguish(true)
                    .canConvertToSource(false)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

    public static void init(IEventBus bus) {
        FLUID_TYPES.register(bus);
    }
}
