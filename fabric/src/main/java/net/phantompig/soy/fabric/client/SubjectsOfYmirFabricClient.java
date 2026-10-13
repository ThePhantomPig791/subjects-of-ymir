package net.phantompig.soy.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.phantompig.soy.client.SubjectsOfYmirClient;
import net.phantompig.soy.fluid.SoyFluids;
import net.phantompig.soy.fluid.SpinalFluid;

public class SubjectsOfYmirFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SubjectsOfYmirClient.init();
        SubjectsOfYmirClient.setup();

        FluidRenderHandlerRegistry.INSTANCE.register(SoyFluids.SPINAL_FLUID.get(), SoyFluids.FLOWING_SPINAL_FLUID.get(),
                new SimpleFluidRenderHandler(
                        SpinalFluid.TEXTURE_STILL,
                        SpinalFluid.TEXTURE_FLOW,
                        SpinalFluid.TEXTURE_OVERLAY,
                        SpinalFluid.COLOR
                ));
    }
}
