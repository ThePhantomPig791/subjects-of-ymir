package net.phantompig.soy.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.phantompig.soy.client.SubjectsOfYmirClient;

public class SubjectsOfYmirFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SubjectsOfYmirClient.init();
    }
}
