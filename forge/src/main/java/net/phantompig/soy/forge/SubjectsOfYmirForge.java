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

        SubjectsOfYmir.LOGGER.info("Subjects of Ymir initialized on Forge!");
        SubjectsOfYmir.LOGGER.error("Unfortunately, Ymir.exe could not be found. It seems that she's been very disillusioned from the whole Titan Power Slave thing. Zeke really must have gotten to her. Sorry about that.");
        SubjectsOfYmir.LOGGER.error("Since Ymir couldn't make it today, I suppose she will have no Subjects.");
    }
}
