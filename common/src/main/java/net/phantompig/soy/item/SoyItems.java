package net.phantompig.soy.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.block.SoyBlocks;
import net.threetag.palladiumcore.registry.CreativeModeTabRegistry;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> RAW_ICEBURST = ITEMS.register("raw_iceburst", () -> new IceburstItem(new Item.Properties()));
    public static final RegistrySupplier<Item> ICEBURST_ORE = ITEMS.register("iceburst_ore", () -> new BlockItem(SoyBlocks.ICEBURST_ORE.get(), new Item.Properties()));
    public static final RegistrySupplier<Item> DEEPSLATE_ICEBURST_ORE = ITEMS.register("deepslate_iceburst_ore", () -> new BlockItem(SoyBlocks.DEEPSLATE_ICEBURST_ORE.get(), new Item.Properties()));

    public static final RegistrySupplier<Item> IRON_BAMBOO = ITEMS.register("iron_bamboo", () -> new Item(new Item.Properties()));
    public static final RegistrySupplier<Item> IRON_BAMBOO_LEAF = ITEMS.register("iron_bamboo_leaf", () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> COMPRESSION_TABLE = ITEMS.register("compression_table", () -> new BlockItem(SoyBlocks.COMPRESSION_TABLE.get(), new Item.Properties()));
    public static final RegistrySupplier<Item> INJECTION = ITEMS.register("injection", () -> new InjectionItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final RegistrySupplier<Item> BAZOOKA = ITEMS.register("bazooka", () -> new BazookaItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 10));


    public static class CreativeModeTabs {
        public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.CREATIVE_MODE_TAB);

        public static final RegistrySupplier<CreativeModeTab> SOY = TABS.register("soy",
            () -> CreativeModeTabRegistry.create(Component.translatable("itemGroup.subjects_of_ymir.soy"),
                () ->  {
                    ItemStack stack = new ItemStack(INJECTION.get());
                    if (stack.getItem() instanceof DataHoldingItem d) {
                        d.set(stack, 100);
                    }
                    return stack;
                }
            )
        );
    }


    public static void init() {
        ITEMS.register();
        CreativeModeTabs.TABS.register();

        CreativeModeTabRegistry.addToTab(CreativeModeTabs.SOY, entries -> {
            entries.add(INJECTION.get());
            entries.add(INJECTION.get().getDefaultInstance());
            entries.add(RAW_ICEBURST.get());
            entries.add(ICEBURST_ORE.get());
            entries.add(DEEPSLATE_ICEBURST_ORE.get());
            entries.add(IRON_BAMBOO.get());
            entries.add(IRON_BAMBOO_LEAF.get());
            entries.add(COMPRESSION_TABLE.get());
            entries.add(BAZOOKA.get().getDefaultInstance());
        });
    }
}
