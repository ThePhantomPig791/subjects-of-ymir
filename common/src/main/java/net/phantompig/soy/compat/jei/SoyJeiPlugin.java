package net.phantompig.soy.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.menu.CompressionMenu;
import net.phantompig.soy.menu.SoyMenus;
import net.phantompig.soy.odm.component.OdmComponents;
import net.phantompig.soy.recipe.SoyRecipeTypes;
import net.threetag.palladiumcore.registry.RegistrySupplier;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class SoyJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return SubjectsOfYmir.rsrc("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CompressionCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager recipeManager = level.getRecipeManager();

        registration.addRecipes(CompressionCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(SoyRecipeTypes.COMPRESSION.get()));

        registration.addItemStackInfo(List.of(SoyItems.IRON_BAMBOO.get().getDefaultInstance(), SoyItems.IRON_BAMBOO_LEAF.get().getDefaultInstance()), Component.translatable("info.item.iron_bamboo"), Component.translatable("info.item.iron_bamboo_leaf"));
        registration.addItemStackInfo(List.of(SoyItems.RAW_ICEBURST.get().getDefaultInstance(), SoyItems.ICEBURST_ORE.get().getDefaultInstance(), SoyItems.DEEPSLATE_ICEBURST_ORE.get().getDefaultInstance()), Component.translatable("info.item.iceburst"));
        registration.addItemStackInfo(List.of(SoyItems.BLADE_HANDLE.get().getDefaultInstance()), Component.translatable("info.item.blade_handle", Component.keybind("key.attack"), Component.keybind("key.use")));
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAliases(VanillaTypes.ITEM_STACK, OdmComponents.ODM_ITEMS.values().stream().map(RegistrySupplier::get).map(Item::getDefaultInstance).toList(), List.of("odm", "3dm", "vertical", "movement", "iceburst", "gas"));
        registration.addAliases(VanillaTypes.ITEM_STACK, SoyItems.UNIFORMS.get(SubjectsOfYmir.rsrc("uniform_leggings")).get().getDefaultInstance(), List.of("3dm", "omni", "omni directional", "omni-directional", "vertical", "movement", "gas"));
        registration.addAliases(VanillaTypes.ITEM_STACK, SoyItems.RAW_ICEBURST.get().getDefaultInstance(), List.of("odm", "3dm", "vertical", "movement", "gas", "omni", "omni directional", "omni-directional"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(SoyItems.COMPRESSION_TABLE.get().getDefaultInstance(), CompressionCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CompressionMenu.class, SoyMenus.COMPRESSION.get(), CompressionCategory.RECIPE_TYPE, 0, 3, 5, 36);
    }
}
