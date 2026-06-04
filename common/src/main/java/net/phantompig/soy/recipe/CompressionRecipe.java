package net.phantompig.soy.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.phantompig.soy.menu.CompressionMenu;
import net.threetag.palladium.util.json.GsonUtil;

import java.util.ArrayList;
import java.util.List;

public class CompressionRecipe implements Recipe<CompressionMenu.CompressionInputs> {
    public final ResourceLocation id;
    public final NonNullList<Ingredient> ingredients;
    public final ItemStack result;

    public final ByproductType byproduct;
    public final float byproductChance;
    public final int byproductAmount;

    public CompressionRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result, ByproductType byproduct, float byproductChance, int byproductAmount) {
        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
        this.byproduct = byproduct;
        this.byproductChance = byproductChance;
        this.byproductAmount = byproductAmount;
    }

    @Override
    public boolean matches(CompressionMenu.CompressionInputs container, Level level) {
        ArrayList<ItemStack> unaccountedStacks = new ArrayList<>(container.getItems());
        ArrayList<Ingredient> unaccountedIngredients = new ArrayList<>(this.ingredients);

        for (int i = 0; i < unaccountedIngredients.size(); i++) {
            Ingredient ingredient = unaccountedIngredients.get(i);
            for (int k = 0; k < unaccountedStacks.size(); k++) {
                if (ingredient.test(unaccountedStacks.get(k))) {
                    unaccountedStacks.remove(k);
                    unaccountedIngredients.remove(i);
                    i--;
                    break;
                }
            }
        }

        return unaccountedStacks.isEmpty() && unaccountedIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(CompressionMenu.CompressionInputs container, RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SoyRecipeTypes.COMPRESSION_SERIALIZER.get();
    }
    @Override
    public RecipeType<?> getType() {
        return SoyRecipeTypes.COMPRESSION.get();
    }

    public enum ByproductType {
        NONE,
        SPINAL_FLUID,
        GAS
    }

    public static class Serializer implements RecipeSerializer<CompressionRecipe> {
        @Override
        public CompressionRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            if (!serializedRecipe.get("ingredients").isJsonArray()) throw new JsonParseException("Expected ingredients to be an array");
            JsonArray ingJson = serializedRecipe.getAsJsonArray("ingredients");
            List<Ingredient> ingredients = new ArrayList<>(3);
            ingJson.forEach(el -> {
                if (!el.isJsonObject()) throw new JsonParseException("Expected ingredients to be an array of JSON objects");
                ingredients.add(Ingredient.fromJson(el));
            });
            NonNullList<Ingredient> nonNullIngredients = NonNullList.withSize(3, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                if (ingredients.get(i) != null) nonNullIngredients.set(i, ingredients.get(i));
            }

            return new CompressionRecipe(
                    recipeId,
                    nonNullIngredients,
                    GsonUtil.getAsItemStack(serializedRecipe, "result", ItemStack.EMPTY),
                    ByproductType.valueOf(GsonHelper.getAsString(serializedRecipe, "byproduct_type", "none").toUpperCase()),
                    GsonHelper.getAsFloat(serializedRecipe, "byproduct_chance", 1),
                    GsonHelper.getAsInt(serializedRecipe, "byproduct_amount", 0)
            );
        }

        @Override
        public CompressionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            List<Ingredient> ingredients = buffer.readList(Ingredient::fromNetwork);
            NonNullList<Ingredient> nonNullIngredients = NonNullList.withSize(3, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                if (ingredients.get(i) != null) nonNullIngredients.set(i, ingredients.get(i));
            }
            return new CompressionRecipe(recipeId, nonNullIngredients, buffer.readItem(), ByproductType.values()[buffer.readByte()], buffer.readFloat(), buffer.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CompressionRecipe recipe) {
            buffer.writeCollection(recipe.ingredients, (buf, ingredient) -> ingredient.toNetwork(buf));
            buffer.writeItem(recipe.result);
            buffer.writeByte(recipe.byproduct.ordinal());
            buffer.writeFloat(recipe.byproductChance);
            buffer.writeInt(recipe.byproductAmount);
        }
    }
}
