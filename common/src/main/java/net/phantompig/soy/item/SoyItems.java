package net.phantompig.soy.item;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.entity.SoyPaintingVariants;
import net.phantompig.soy.odm.component.OdmComponents;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.item.AddonArmorItem;
import net.threetag.palladiumcore.item.PalladiumRecordItem;
import net.threetag.palladiumcore.item.SimpleArmorMaterial;
import net.threetag.palladiumcore.item.SimpleToolTier;
import net.threetag.palladiumcore.registry.CreativeModeTabRegistry;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

import java.util.*;

public class SoyItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> RIBCAGE = ITEMS.register("ribcage", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
    public static final RegistrySupplier<Item> BROKEN_SPINE = ITEMS.register("broken_spine", () -> new Item(new Item.Properties()));
    public static final RegistrySupplier<Item> SPINE = ITEMS.register("spine", () -> new SpineItem(new Item.Properties().food(new FoodProperties.Builder().build()).rarity(Rarity.RARE).stacksTo(16)));

    public static final RegistrySupplier<Item> RAW_ICEBURST = ITEMS.register("raw_iceburst", () -> new IceburstItem(new Item.Properties()));
    public static final RegistrySupplier<Item> ICEBURST_ORE = ITEMS.register("iceburst_ore", () -> new BlockItem(SoyBlocks.ICEBURST_ORE.get(), new Item.Properties()));
    public static final RegistrySupplier<Item> DEEPSLATE_ICEBURST_ORE = ITEMS.register("deepslate_iceburst_ore", () -> new BlockItem(SoyBlocks.DEEPSLATE_ICEBURST_ORE.get(), new Item.Properties()));

    public static final RegistrySupplier<Item> IRON_BAMBOO = ITEMS.register("iron_bamboo", () -> new Item(new Item.Properties()));
    public static final RegistrySupplier<Item> IRON_BAMBOO_LEAF = ITEMS.register("iron_bamboo_leaf", () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> IRON_BAMBOO_SWORD = ITEMS.register("iron_bamboo_sword", () -> new SwordItem(ToolTiers.IRON_BAMBOO, 3, -1.5f, new Item.Properties()));
    public static final RegistrySupplier<Item> IRON_BAMBOO_DAGGER = ITEMS.register("iron_bamboo_dagger", () -> new SwordItem(ToolTiers.IRON_BAMBOO, 1, -0.5f, new Item.Properties()));
    public static final RegistrySupplier<Item> IRON_BAMBOO_RING = ITEMS.register("iron_bamboo_ring", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistrySupplier<Item> COMPRESSION_TABLE = ITEMS.register("compression_table", () -> new BlockItem(SoyBlocks.COMPRESSION_TABLE.get(), new Item.Properties()));

    public static final RegistrySupplier<Item> INJECTION = ITEMS.register("injection", () -> new InjectionItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistrySupplier<Item> VIAL = ITEMS.register("vial", () -> new SpinalFluidHoldingItem(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().build()), 300, true));

    public static final RegistrySupplier<Item> HARDENING_BLOCK = ITEMS.register("hardening_block", () -> new BlockItem(SoyBlocks.HARDENING_BLOCK.get(), new Item.Properties()));

    public static final RegistrySupplier<Item> RAW_ULTRAHARD_STEEL = ITEMS.register("raw_ultrahard_steel", () -> new Item(new Item.Properties()));
    public static final RegistrySupplier<Item> ULTRAHARD_STEEL_INGOT = ITEMS.register("ultrahard_steel_ingot", () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> BLADE = ITEMS.register("blade", () -> new BladeItem(new Item.Properties().stacksTo(16)));
    public static final RegistrySupplier<Item> GAS_CANISTER = ITEMS.register("gas_canister", () -> new GasHoldingItem(new Item.Properties().stacksTo(1), 500));

    public static final RegistrySupplier<Item> ODM_HANDLE = ITEMS.register("odm_handle", () -> new OdmHandleItem(new Item.Properties().stacksTo(1)));

    public static final RegistrySupplier<Item> MUSIC_DISC_YOUSEEBIGGIRL = ITEMS.register("music_disc_youseebiggirl", () -> new PalladiumRecordItem(5, SoySounds.MUSIC_DISC_YOUSEEBIGGIRL, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 359));
    public static final RegistrySupplier<Item> MUSIC_DISC_TKT = ITEMS.register("music_disc_tkt", () -> new PalladiumRecordItem(7, SoySounds.MUSIC_DISC_TKT, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 175));
    public static final RegistrySupplier<Item> MUSIC_DISC_COUNTER_ATTACK_MANKIND = ITEMS.register("music_disc_counter_attack_mankind", () -> new PalladiumRecordItem(15, SoySounds.MUSIC_DISC_COUNTER_ATTACK_MANKIND, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 366));
    public static final RegistrySupplier<Item> MUSIC_DISC_CALL_OF_SILENCE = ITEMS.register("music_disc_call_of_silence", () -> new PalladiumRecordItem(1, SoySounds.MUSIC_DISC_CALL_OF_SILENCE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 178));
    public static final RegistrySupplier<Item> MUSIC_DISC_BAUKLOTZE = ITEMS.register("music_disc_bauklotze", () -> new PalladiumRecordItem(13, SoySounds.MUSIC_DISC_BAUKLOTZE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 236));

    public static final RegistrySupplier<Item> FLARE_GUN = ITEMS.register("flare_gun", () -> new FlareGunItem(new Item.Properties().durability(256)));
    public static final RegistrySupplier<Item> FLARE_CARTRIDGE = ITEMS.register("flare_cartridge", () -> new FlareCartridgeItem(new Item.Properties().stacksTo(16)));

    public static final String[] UNIFORM_EMBLEMS = {"survey_corps", "military_police", "garrison", "cadet"};
    public static final LinkedHashMap<ResourceLocation, RegistrySupplier<Item>> UNIFORMS = new LinkedHashMap<>();



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
        OdmComponents.init(ITEMS);

        ITEMS.register();
        CreativeModeTabs.TABS.register();

        // these are only AddonArmorItems because i already had the armor renderers made and didn't feel like re-doing it with the vanilla system
        String name;
        for (String emblem : UNIFORM_EMBLEMS) {
            name = emblem + "_cloak";
            UNIFORMS.put(SubjectsOfYmir.rsrc(name), ITEMS.register(name, () -> new TooltippedAddonArmorItem("tooltip.subjects_of_ymir.emblem." + emblem, ArmorMaterials.UNIFORM, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties()).enableOpenable(true, 0, new ResourceLocation("item.armor.equip_leather"), new ResourceLocation("item.armor.equip_leather"), new ResourceLocation("item.armor.equip_leather"))));
            UNIFORMS.put(SubjectsOfYmir.rsrc(name = emblem + "_jacket"), ITEMS.register(name, () -> new TooltippedAddonArmorItem("tooltip.subjects_of_ymir.emblem." + emblem, ArmorMaterials.UNIFORM, ArmorItem.Type.CHESTPLATE, new Item.Properties())));
        }
        UNIFORMS.put(SubjectsOfYmir.rsrc(name = "cloak"), ITEMS.register(name, () -> new AddonArmorItem(ArmorMaterials.UNIFORM, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties()).enableOpenable(true, 0, new ResourceLocation("item.armor.equip_leather"), new ResourceLocation("item.armor.equip_leather"), new ResourceLocation("item.armor.equip_leather"))));
        UNIFORMS.put(SubjectsOfYmir.rsrc(name = "uniform_jacket"), ITEMS.register(name, () -> new AddonArmorItem(ArmorMaterials.UNIFORM, ArmorItem.Type.CHESTPLATE, new Item.Properties())));
        UNIFORMS.put(SubjectsOfYmir.rsrc(name = "uniform_leggings"), ITEMS.register(name, () -> new OdmAttachableAddonArmorItem(ArmorMaterials.UNIFORM, ArmorItem.Type.LEGGINGS, new Item.Properties())));
        UNIFORMS.put(SubjectsOfYmir.rsrc(name = "uniform_boots"), ITEMS.register(name, () -> new FallDamageResistantAddonArmorItem(ArmorMaterials.UNIFORM, ArmorItem.Type.BOOTS, new Item.Properties(), 12)));

        CreativeModeTabRegistry.addToTab(CreativeModeTabs.SOY, entries -> {
            entries.add(INJECTION.get());
            entries.add(INJECTION.get().getDefaultInstance());
            entries.add(VIAL.get());
            entries.add(VIAL.get().getDefaultInstance());
            entries.add(SPINE.get());
            entries.add(RIBCAGE.get());
            entries.add(BROKEN_SPINE.get());
            entries.add(HARDENING_BLOCK.get());
            entries.add(RAW_ICEBURST.get());
            entries.add(ICEBURST_ORE.get());
            entries.add(DEEPSLATE_ICEBURST_ORE.get());
            entries.add(RAW_ULTRAHARD_STEEL.get());
            entries.add(ULTRAHARD_STEEL_INGOT.get());
            entries.add(IRON_BAMBOO.get());
            entries.add(IRON_BAMBOO_LEAF.get());
            entries.add(IRON_BAMBOO_SWORD.get());
            entries.add(IRON_BAMBOO_DAGGER.get());
            entries.add(IRON_BAMBOO_RING.get());
            entries.add(COMPRESSION_TABLE.get());
            entries.add(BLADE.get());
            entries.add(GAS_CANISTER.get());
            entries.add(GAS_CANISTER.get().getDefaultInstance());
            entries.add(ODM_HANDLE.get());
            OdmComponents.initCreativeMenu(entries);
            for (RegistrySupplier<Item> s : UNIFORMS.values()) {
                entries.add(s.get());
            }
            entries.add(FLARE_GUN.get());
            entries.add(FLARE_CARTRIDGE.get());
            entries.add(MUSIC_DISC_YOUSEEBIGGIRL.get());
            entries.add(MUSIC_DISC_TKT.get());
            entries.add(MUSIC_DISC_COUNTER_ATTACK_MANKIND.get());
            entries.add(MUSIC_DISC_CALL_OF_SILENCE.get());
            entries.add(MUSIC_DISC_BAUKLOTZE.get());
            for (RegistrySupplier<PaintingVariant> reference : SoyPaintingVariants.PAINTING_VARIANTS.getEntries()) {
                ItemStack itemStack = new ItemStack(Items.PAINTING);
                CompoundTag compoundTag = itemStack.getOrCreateTagElement("EntityTag");
                compoundTag.putString("variant", reference.getId().toString());
                entries.add(itemStack);
            }
        });

        SoyItemTags.init();
    }
}
