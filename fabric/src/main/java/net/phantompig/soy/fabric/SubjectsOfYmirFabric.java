package net.phantompig.soy.fabric;

import net.phantompig.soy.SubjectsOfYmir;
import net.fabricmc.api.ModInitializer;

public class SubjectsOfYmirFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SubjectsOfYmir.init();
    }

}
