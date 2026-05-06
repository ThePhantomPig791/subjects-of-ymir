package net.phantompig.soy.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.phantompig.soy.item.GasHoldingItem;

public class ShapedRecipeWithGasHoldingItems extends ShapedRecipe {
    public ShapedRecipeWithGasHoldingItems(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result, boolean showNotification) {
        super(id, group, category, width, height, recipeItems, result, showNotification);
    }
    public ShapedRecipeWithGasHoldingItems(ResourceLocation id, ShapedRecipe original) {
        this(id, original.getGroup(), original.category(), original.getWidth(), original.getHeight(), original.getIngredients(), original.result, original.showNotification());
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        int totalGas = 0;
        for (ItemStack stack : container.getItems()) {
            if (stack.getItem() instanceof GasHoldingItem ghi) {
                totalGas += ghi.get(stack);
            }
        }
        ItemStack result = super.assemble(container, registryAccess);
        if (result.getItem() instanceof GasHoldingItem ghi) {
            ghi.add(result, totalGas);
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SoyRecipeTypes.SHAPED_RECIPE_WITH_GAS_HOLDING_ITEMS.get();
    }

    public static class Serializer implements RecipeSerializer<ShapedRecipeWithGasHoldingItems> {
        public Serializer() {
        }

        public ShapedRecipeWithGasHoldingItems fromJson(ResourceLocation recipeId, JsonObject json) {
            return new ShapedRecipeWithGasHoldingItems(recipeId, SHAPED_RECIPE.fromJson(recipeId, json));
        }

        public ShapedRecipeWithGasHoldingItems fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new ShapedRecipeWithGasHoldingItems(recipeId, SHAPED_RECIPE.fromNetwork(recipeId, buffer));
        }

        public void toNetwork(FriendlyByteBuf buffer, ShapedRecipeWithGasHoldingItems recipe) {
            SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
