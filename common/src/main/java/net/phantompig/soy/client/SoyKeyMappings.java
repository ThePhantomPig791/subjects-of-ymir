package net.phantompig.soy.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.phantompig.soy.item.OdmHandleItem;
import net.phantompig.soy.network.OdmHandlePressMessage;
import net.phantompig.soy.network.SoyNetwork;
import net.threetag.palladiumcore.event.InputEvents;
import net.threetag.palladiumcore.registry.client.KeyMappingRegistry;

@Environment(EnvType.CLIENT)
public class SoyKeyMappings {
    public static final KeyMapping MAIN_HOOK = new KeyMapping("key.subjects_of_ymir.main_hook", 69, "key.categories.gameplay");
    public static final KeyMapping OFF_HOOK = new KeyMapping("key.subjects_of_ymir.off_hook", 81, "key.categories.gameplay");

    public static void init() {
        KeyMappingRegistry.register(MAIN_HOOK);
        KeyMappingRegistry.register(OFF_HOOK);

        InputEvents.KEY_PRESSED.register((client, keyCode, scanCode, action, modifiers) -> {
            if (client.player == null) return;
            if (client.player.getMainHandItem().getItem() instanceof OdmHandleItem) {
                while (MAIN_HOOK.consumeClick()) {
                    SoyNetwork.NETWORK.sendToServer(new OdmHandlePressMessage(true));
                }
            }
            if (client.player.getOffhandItem().getItem() instanceof OdmHandleItem) {
                while (OFF_HOOK.consumeClick()) {
                    SoyNetwork.NETWORK.sendToServer(new OdmHandlePressMessage(false));
                }
            }
        });
    }
}