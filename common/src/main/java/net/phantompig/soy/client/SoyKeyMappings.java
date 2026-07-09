package net.phantompig.soy.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.threetag.palladiumcore.registry.client.KeyMappingRegistry;

@Environment(EnvType.CLIENT)
public class SoyKeyMappings {
    public static final KeyMapping MAIN_HOOK = new KeyMapping("key.subjects_of_ymir.main_hook", 69, "key.categories.gameplay");
    public static final KeyMapping OFF_HOOK = new KeyMapping("key.subjects_of_ymir.off_hook", 81, "key.categories.gameplay");

    public static void init() {
        KeyMappingRegistry.register(MAIN_HOOK);
        KeyMappingRegistry.register(OFF_HOOK);
    }
}