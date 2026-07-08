package net.phantompig.soy.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public abstract class ItemStackHoldingItem extends Item {
    public final Predicate<ItemStack> canHold;
    public final SoundEvent insertSound;

    public ItemStackHoldingItem(Predicate<ItemStack> canHold, SoundEvent insertSound, Properties properties) {
        super(properties);
        this.canHold = canHold;
        this.insertSound = insertSound;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (this.getStack(itemStack).isEmpty()) {
            ItemStack otherStack = player.getItemInHand(usedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (this.canHold.test(otherStack)) {
                this.insertStack(itemStack, otherStack, player);
                return InteractionResultHolder.consume(itemStack);
            }
        }
        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY) {
            ItemStack storedCartridge = getStack(stack);
            if (other.isEmpty() || ItemEntity.areMergable(storedCartridge, other)) {
                if (!storedCartridge.isEmpty()) {
                    access.set(ItemEntity.merge(storedCartridge, other, storedCartridge.getCount() + other.getCount()));
                    this.setStack(stack, ItemStack.EMPTY);
                    playInsertSound(player, 1.2f);
                    return true;
                }
            } else {
                if (this.canHold.test(other) && storedCartridge.isEmpty()) {
                    insertStack(stack, other, player);
                    return true;
                }
            }
        }
        return false;
    }

    private void insertStack(ItemStack gunStack, ItemStack flareStack, Player player) {
        this.setStack(gunStack, flareStack.copyWithCount(1));
        flareStack.shrink(1);
        playInsertSound(player, 1.6f);
    }

    public ItemStack getStack(ItemStack stack) {
        return stack.getOrCreateTagElement("HeldItemStack").isEmpty() ? ItemStack.EMPTY : ItemStack.of(stack.getTagElement("HeldItemStack"));
    }
    public void setStack(ItemStack stack, ItemStack toHold) {
        stack.getOrCreateTag().put("HeldItemStack", toHold.save(new CompoundTag()));
    }

    public void playInsertSound(Player player, float pitch) {
        player.playSound(this.insertSound,0.6f, pitch);
    }
}
