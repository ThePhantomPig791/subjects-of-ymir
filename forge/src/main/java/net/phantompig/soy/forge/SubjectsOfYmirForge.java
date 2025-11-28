package net.phantompig.soy.forge;

import net.phantompig.soy.SubjectsOfYmir;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.threetag.palladiumcore.forge.PalladiumCoreForge;

@Mod(SubjectsOfYmir.MOD_ID)
public class SubjectsOfYmirForge {

    public SubjectsOfYmirForge() {
        // Submit our event bus to let PalladiumCore register our content on the right time
        PalladiumCoreForge.registerModEventBus(SubjectsOfYmir.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SubjectsOfYmir.init();
    }
}
