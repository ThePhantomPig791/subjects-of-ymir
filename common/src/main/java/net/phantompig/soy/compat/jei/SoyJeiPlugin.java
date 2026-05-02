package net.phantompig.soy.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.recipe.SoyRecipeTypes;

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
        SubjectsOfYmir.LOGGER.info("comp recipes: {}", recipeManager.getAllRecipesFor(SoyRecipeTypes.COMPRESSION.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(SoyItems.COMPRESSION_TABLE.get().getDefaultInstance(), CompressionCategory.RECIPE_TYPE);
    }
}
