package net.phantompig.soy.forge;

import net.phantompig.soy.SubjectsOfYmir;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.phantompig.soy.client.SubjectsOfYmirClient;
import net.threetag.palladiumcore.forge.PalladiumCoreForge;
import net.threetag.palladiumcore.util.Platform;

@Mod(SubjectsOfYmir.MOD_ID)
public class SubjectsOfYmirForge {

    public SubjectsOfYmirForge() {
        // Submit our event bus to let PalladiumCore register our content on the right time
        PalladiumCoreForge.registerModEventBus(SubjectsOfYmir.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SubjectsOfYmir.init();

        if (Platform.isClient()) {
            SubjectsOfYmirClient.init();
        }
    }
}
