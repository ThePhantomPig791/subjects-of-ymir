package net.phantompig.soy.client;

import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.client.renderer.PlayerInNapeRenderLayer;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;

public class SubjectsOfYmirClient {
    public static void init() {
        PackRenderLayerManager.registerParser(SubjectsOfYmir.rsrc("player_in_nape"), PlayerInNapeRenderLayer::parse);
    }
}
