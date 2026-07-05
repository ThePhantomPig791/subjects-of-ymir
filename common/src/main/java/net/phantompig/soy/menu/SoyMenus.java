package net.phantompig.soy.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.MENU);

    @SuppressWarnings("MemberVisibilityCanBePrivate")
    public static final RegistrySupplier<MenuType<CompressionMenu>> COMPRESSION = MENU_TYPES.register("compression", () -> new MenuType<>(CompressionMenu::new, FeatureFlags.VANILLA_SET));

    public static void init() {
        MENU_TYPES.register();
    }
}
