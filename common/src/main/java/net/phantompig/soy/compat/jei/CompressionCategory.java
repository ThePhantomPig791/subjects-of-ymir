package net.phantompig.soy.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.crafting.Ingredient;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.client.screen.CompressionScreen;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.recipe.CompressionRecipe;
import org.jetbrains.annotations.Nullable;

public class CompressionCategory implements IRecipeCategory<CompressionRecipe> {
    public static final RecipeType<CompressionRecipe> RECIPE_TYPE =
            RecipeType.create(SubjectsOfYmir.MOD_ID, "compression", CompressionRecipe.class);

    private static final int BG_WIDTH = 142;
    private static final int BG_HEIGHT = 60;

    private final IDrawable icon;
    private final IDrawable background;

    public CompressionCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(SoyItems.COMPRESSION_TABLE.get().getDefaultInstance());
        this.background = guiHelper.createDrawable(CompressionScreen.BACKGROUND_LOCATION, 11, 14, BG_WIDTH, BG_HEIGHT);
    }

    @Override
    public RecipeType<CompressionRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.subjects_of_ymir.category.compression");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return BG_WIDTH;
    }

    @Override
    public int getHeight() {
        return BG_HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompressionRecipe recipe, IFocusGroup iFocusGroup) {
        int k = 0;
        for (Ingredient ingredient : recipe.ingredients) {
            builder.addInputSlot(3 + k * 20, 21 + (k == 1 ? 6 : 0)).addIngredients(ingredient);
            k++;
        }

        builder.addOutputSlot(123, 11).addItemStack(recipe.result);

        IRecipeSlotBuilder byproductSlot = builder.addOutputSlot(123, 31);
        switch (recipe.byproduct) {
            case GAS -> {
                byproductSlot.addItemStack(SoyItems.GAS_CANISTER.get().getDefaultInstance());
            }
            case SPINAL_FLUID -> {
                byproductSlot.addIngredients(Ingredient.of(SoyItems.INJECTION.get().getDefaultInstance(), SoyItems.VIAL.get().getDefaultInstance()));
            }
        }
        byproductSlot.addRichTooltipCallback((iRecipeSlotView, iTooltipBuilder) -> {
            iTooltipBuilder.add(Component.translatable("recipe.compression.byproduct"));
            if (recipe.byproduct != CompressionRecipe.ByproductType.NONE) {
                iTooltipBuilder.add(FormattedText.composite(
                        Component.translatable("recipe.compression.byproduct_amount", recipe.byproductAmount),
                        Component.literal(" "),
                        Component.translatable("recipe.compression.byproduct." + recipe.byproduct.toString().toLowerCase())
                ));
                iTooltipBuilder.add(Component.translatable("recipe.compression.byproduct_chance", (int) (recipe.byproductChance * 100) + "%"));
            }
        });
    }

    @Override
    public void draw(CompressionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }
}
