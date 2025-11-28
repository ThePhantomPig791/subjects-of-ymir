package net.phantompig.soy;

import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.titan.TitanRegistry;

public class SubjectsOfYmir {

    public static final String MOD_ID = "subjects_of_ymir";


    public static void init() {
        TitanRegistry.init();

    }

    public static ResourceLocation rsrc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
