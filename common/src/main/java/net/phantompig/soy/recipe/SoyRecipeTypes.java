package net.phantompig.soy.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.RECIPE_TYPE);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeType<CompressionRecipe>> COMPRESSION = RECIPE_TYPES.register("compression", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "compression";
        }
    });
    public static final RegistrySupplier<RecipeSerializer<CompressionRecipe>> COMPRESSION_SERIALIZER = RECIPE_SERIALIZERS.register("compression", CompressionRecipe.Serializer::new);

    public static void init() {
        RECIPE_TYPES.register();
        RECIPE_SERIALIZERS.register();
    }
}
