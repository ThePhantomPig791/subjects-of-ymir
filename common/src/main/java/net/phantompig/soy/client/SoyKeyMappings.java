package net.phantompig.soy.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.threetag.palladiumcore.registry.client.KeyMappingRegistry;

@Environment(EnvType.CLIENT)
public class SoyKeyMappings {
    public static final KeyMapping RIGHT_HOOK = new KeyMapping("key.subjects_of_ymir.right_hook", 69, "key.categories.gameplay");
    public static final KeyMapping LEFT_HOOK = new KeyMapping("key.subjects_of_ymir.left_hook", 81, "key.categories.gameplay");

    public static void init() {
        KeyMappingRegistry.register(RIGHT_HOOK);
        KeyMappingRegistry.register(LEFT_HOOK);
    }
}
