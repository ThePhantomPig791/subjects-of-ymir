package net.phantompig.soy.item;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.block.SoyBlocks;
import net.threetag.palladium.item.AddonArmorItem;
import net.threetag.palladiumcore.item.SimpleArmorMaterial;
import net.threetag.palladiumcore.item.SimpleToolTier;
import net.threetag.palladiumcore.registry.CreativeModeTabRegistry;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

import java.util.*;

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


    private static final String[] UNIFORM_EMBLEMS = {"garrison_", "military_police_", "survey_corps_"};
    private static final List<RegistrySupplier<Item>> UNIFORMS = new ArrayList<>();



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

    public static class ArmorMaterials {
        public static final ArmorMaterial UNIFORM = new SimpleArmorMaterial(
            "uniform",
            8,
            Util.make(new EnumMap(ArmorItem.Type.class), (enumMap) -> {
                enumMap.put(ArmorItem.Type.BOOTS, 3);
                enumMap.put(ArmorItem.Type.LEGGINGS, 4);
                enumMap.put(ArmorItem.Type.CHESTPLATE, 3);
                enumMap.put(ArmorItem.Type.HELMET, 1);
            }),
        0,
            () -> SoundEvents.ARMOR_EQUIP_LEATHER,
            0,
            0,
            () -> Ingredient.of(SoyItems.IRON_BAMBOO_LEAF.get())
        );
    }


    public static void init() {
        ITEMS.register();
        CreativeModeTabs.TABS.register();

        for (String emblem : UNIFORM_EMBLEMS) {
            UNIFORMS.add(ITEMS.register(emblem + "cloak", () -> new AddonArmorItem(ArmorMaterials.UNIFORM, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties()).enableOpenable(true, 0, new ResourceLocation("item.armor.equip_leather"), new ResourceLocation("item.armor.equip_leather"), new ResourceLocation("item.armor.equip_leather"))));
            UNIFORMS.add(ITEMS.register(emblem + "jacket", () -> new AddonArmorItem(ArmorMaterials.UNIFORM, ArmorItem.Type.CHESTPLATE, new Item.Properties())));
        }
        UNIFORMS.add(ITEMS.register("cloak", () -> new AddonArmorItem(ArmorMaterials.UNIFORM, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties()).enableOpenable(true, 0, new ResourceLocation("item.armor.equip_leather"), new ResourceLocation("item.armor.equip_leather"), new ResourceLocation("item.armor.equip_leather"))));
        UNIFORMS.add(ITEMS.register("uniform_jacket", () -> new AddonArmorItem(ArmorMaterials.UNIFORM, ArmorItem.Type.CHESTPLATE, new Item.Properties())));
        UNIFORMS.add(ITEMS.register("uniform_leggings", () -> new AddonArmorItem(ArmorMaterials.UNIFORM, ArmorItem.Type.LEGGINGS, new Item.Properties())));
        UNIFORMS.add(ITEMS.register("uniform_boots", () -> new AddonArmorItem(ArmorMaterials.UNIFORM, ArmorItem.Type.BOOTS, new Item.Properties())));

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

            for (RegistrySupplier<Item> s : UNIFORMS) {
                entries.add(s.get());
            }
        });
    }
}
