package net.phantompig.soy.menu;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.item.DataHoldingItem;
import net.phantompig.soy.item.GasHoldingItem;
import net.phantompig.soy.item.SpinalFluidHoldingItem;
import net.phantompig.soy.recipe.CompressionRecipe;
import net.phantompig.soy.recipe.SoyRecipeTypes;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class CompressionMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    private final CompressionInputs inputs;
    private final CompressionResultContainer output;

    private final Player player;

    public CompressionMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public CompressionMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(SoyMenus.COMPRESSION.get(), containerId);
        this.access = access;

        this.inputs = new CompressionInputs(this, 3);
        this.output = new CompressionResultContainer();

        this.player = playerInventory.player;

        // 0, 1, 2 = inputs
        // 3 = output, 4 = misc output
        // 5 -> 41 = player inventory (32 -> 41 hotbar)
        int k;
        for(k = 0; k < 3; ++k) {
            this.addSlot(new Slot(this.inputs, k, 14 + k * 20, 35 + (k == 1 ? 6 : 0)));
        }
        this.addSlot(new CompressionOutputSlot(this, this.inputs, this.player, this.output, 0, 134, 25));
        this.addSlot(new CompressionByproductSlot(this.output, 1, 134, 45));
        for(k = 0; k < 3; ++k) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index < 5) {
                if (!this.moveItemStackTo(itemStack2, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                // slot.onQuickCraft(itemStack2, itemStack);
            } else if (index < 41) {
                if (itemStack.getItem() instanceof DataHoldingItem) {
                    if (!this.moveItemStackTo(itemStack2, 4, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemStack2, 0, 5, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack2);
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, SoyBlocks.COMPRESSION_TABLE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            for (int i = 0; i < 3; i++) {
                ItemStack itemStack = this.inputs.items.get(i);
                if (!itemStack.isEmpty()) {
                    if (!player.getInventory().add(itemStack)) player.drop(itemStack, false);
                }
            }
            ItemStack itemStack = this.output.itemStacks.get(1);
            if (!itemStack.isEmpty()) {
                if (!player.getInventory().add(itemStack)) player.drop(itemStack, false);
            }
        }
    }

    protected static void inputSlotsChanged(AbstractContainerMenu menu, Level level, Player player, CompressionInputs inputs, CompressionResultContainer result) {
        if (!level.isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<CompressionRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(SoyRecipeTypes.COMPRESSION.get(), inputs, level);
            if (optional.isPresent()) {
                CompressionRecipe recipe = optional.get();
                if (result.setRecipeUsed(level, serverPlayer, recipe)) {
                    ItemStack itemStack2 = recipe.assemble(inputs, level.registryAccess());
                    if (itemStack2.isItemEnabled(level.enabledFeatures())) {
                        itemStack = itemStack2;
                    }
                }
            }

            result.setItem(0, itemStack);
            menu.setRemoteSlot(3, itemStack);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 3, itemStack));
        }
    }

    public void slotsChanged(Container container) {
        this.access.execute((level, blockPos) -> inputSlotsChanged(this, level, this.player, this.inputs, this.output));
    }


    public static class CompressionInputs implements Container {
        private final int size;
        private final NonNullList<ItemStack> items;

        private final AbstractContainerMenu menu;

        private CompressionInputs(AbstractContainerMenu menu, int size) {
            this.menu = menu;
            this.size = size;
            items = NonNullList.withSize(size, ItemStack.EMPTY);
        }

        public NonNullList<ItemStack> getItems() {
            return items;
        }

        @Override
        public int getContainerSize() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) return false;
            }
            return true;
        }

        @NotNull
        @Override
        public ItemStack getItem(int slot) {
            return items.get(slot);
        }

        @NotNull
        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack itemStack = ContainerHelper.removeItem(this.items, slot, amount);
            if (!itemStack.isEmpty()) {
                this.menu.slotsChanged(this);
            }

            return itemStack;
        }

        @NotNull
        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(this.items, slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            this.items.set(slot, stack);
            this.menu.slotsChanged(this);
        }

        @Override
        public void setChanged() {}

        @Override
        public boolean stillValid(Player player) { return true; }

        @Override
        public void clearContent() {
            this.items.clear();
        }
    }

    public static class CompressionOutputSlot extends Slot {
        public final CompressionMenu menu;
        public final CompressionInputs inputs;
        public final Player player;

        public CompressionOutputSlot(CompressionMenu menu, CompressionInputs inputs, Player player, Container container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.menu = menu;
            this.inputs = inputs;
            this.player = player;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            if (this.container instanceof CompressionResultContainer compressionResult && compressionResult.recipeUsed != null) {
                ArrayList<Ingredient> ingredients = new ArrayList<>(compressionResult.recipeUsed.getIngredients());
                for (ItemStack input : this.inputs.items) {
                    for (int i = 0; i < ingredients.size(); i++) {
                        if (ingredients.get(i).test(input)) {
                            input.shrink(1);
                            ingredients.remove(i);
                            break;
                        }
                    }
                }

                if (compressionResult.recipeUsed instanceof CompressionRecipe compressionRecipe) {
                    Item resultItem = compressionResult.getItem(1).getItem();
                    switch (compressionRecipe.byproduct) {
                        case NONE -> {}
                        case SPINAL_FLUID -> {
                            if (resultItem instanceof SpinalFluidHoldingItem spfhi) {
                                addByproduct(spfhi, compressionRecipe, compressionResult);
                            }
                        }
                        case GAS -> {
                            if (resultItem instanceof GasHoldingItem ghi) {
                                addByproduct(ghi, compressionRecipe, compressionResult);
                            }
                        }
                    }
                }
            }

            NonNullList<ItemStack> nonNullList = player.level().getRecipeManager().getRemainingItemsFor(SoyRecipeTypes.COMPRESSION.get(), this.inputs, player.level());
            nonNullList.forEach(itemStack -> {
                if (!this.menu.moveItemStackTo(itemStack, 0, 3, false)) {
                    if (!this.menu.moveItemStackTo(itemStack, 5, 35, true)) {
                        this.player.drop(itemStack, false);
                    }
                }
            });

            this.menu.slotsChanged(this.container);

            PlayerUtil.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.6f, 1.9f);
            PlayerUtil.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.6f, 1.3f);
        }
    }

    private static void addByproduct(DataHoldingItem dhi, CompressionRecipe compressionRecipe, CompressionResultContainer compressionResult) {
        if (Math.random() < compressionRecipe.byproductChance) {
            dhi.add(compressionResult.getItem(1), compressionRecipe.byproductAmount);
        }
    }

    public static class CompressionByproductSlot extends Slot {
        public static final ResourceLocation NO_ITEM_ICON = SubjectsOfYmir.rsrc("item/empty_compression_byproduct");

        public CompressionByproductSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Nullable
        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, NO_ITEM_ICON);
        }
    }

    public static class CompressionResultContainer implements Container, RecipeHolder {
        private final NonNullList<ItemStack> itemStacks;
        @Nullable
        private Recipe<?> recipeUsed = null;

        public CompressionResultContainer() {
            this.itemStacks = NonNullList.withSize(2, ItemStack.EMPTY);
        }

        @Override
        public int getContainerSize() {
            return itemStacks.size();
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : itemStacks) {
                if (!stack.isEmpty()) return false;
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return itemStacks.get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ContainerHelper.removeItem(itemStacks, slot, amount);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(itemStacks, slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            itemStacks.set(slot, stack);
        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            this.itemStacks.clear();
        }

        @Override
        public void setRecipeUsed(@Nullable Recipe<?> recipe) {
            this.recipeUsed = recipe;
        }

        @Nullable
        @Override
        public Recipe<?> getRecipeUsed() {
            return this.recipeUsed;
        }
    }
}
