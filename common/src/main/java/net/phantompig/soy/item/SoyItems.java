package net.phantompig.soy.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.block.SoyBlocks;
import net.threetag.palladiumcore.item.SimpleToolTier;
import net.threetag.palladiumcore.registry.CreativeModeTabRegistry;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> RIB = ITEMS.register("rib", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
    public static final RegistrySupplier<Item> RIBCAGE = ITEMS.register("ribcage", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
    public static final RegistrySupplier<Item> BROKEN_RIB = ITEMS.register("broken_rib", () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> RAW_ICEBURST = ITEMS.register("raw_iceburst", () -> new IceburstItem(new Item.Properties()));
    public static final RegistrySupplier<Item> ICEBURST_ORE = ITEMS.register("iceburst_ore", () -> new BlockItem(SoyBlocks.ICEBURST_ORE.get(), new Item.Properties()));
    public static final RegistrySupplier<Item> DEEPSLATE_ICEBURST_ORE = ITEMS.register("deepslate_iceburst_ore", () -> new BlockItem(SoyBlocks.DEEPSLATE_ICEBURST_ORE.get(), new Item.Properties()));

    public static final RegistrySupplier<Item> IRON_BAMBOO = ITEMS.register("iron_bamboo", () -> new Item(new Item.Properties()));
    public static final RegistrySupplier<Item> IRON_BAMBOO_LEAF = ITEMS.register("iron_bamboo_leaf", () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> IRON_BAMBOO_SWORD = ITEMS.register("iron_bamboo_sword", () -> new SwordItem(ToolTiers.IRON_BAMBOO, 3, -1.5f, new Item.Properties()));
    public static final RegistrySupplier<Item> IRON_BAMBOO_DAGGER = ITEMS.register("iron_bamboo_dagger", () -> new SwordItem(ToolTiers.IRON_BAMBOO, 1, -0.5f, new Item.Properties()));


    public static class CreativeModeTabs {
        public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.CREATIVE_MODE_TAB);

        public static final RegistrySupplier<CreativeModeTab> SOY = TABS.register("soy",
                () -> CreativeModeTabRegistry.create(Component.translatable("itemGroup.subjects_of_ymir.soy"),
                        () ->  new ItemStack(RIB.get())));
    }

    public static class ToolTiers {
        public static final SimpleToolTier IRON_BAMBOO = new SimpleToolTier(
                2,
                200,
                4.5f,
                0.5f,
                3,
                () -> Ingredient.of(SoyItems.IRON_BAMBOO.get())
        );
    }


    public static void init() {
        ITEMS.register();
        CreativeModeTabs.TABS.register();

        CreativeModeTabRegistry.addToTab(CreativeModeTabs.SOY, entries -> {
            entries.add(RIB.get());
            entries.add(RIBCAGE.get());
            entries.add(BROKEN_RIB.get());
            entries.add(RAW_ICEBURST.get());
            entries.add(ICEBURST_ORE.get());
            entries.add(DEEPSLATE_ICEBURST_ORE.get());
            entries.add(IRON_BAMBOO.get());
            entries.add(IRON_BAMBOO_LEAF.get());
            entries.add(IRON_BAMBOO_SWORD.get());
            entries.add(IRON_BAMBOO_DAGGER.get());
        });
    }
}
