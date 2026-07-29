package net.phantompig.soy.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class DataHoldingItem extends Item {
    public final String dataTag;
    public final int max;
    public final Color barColor;
    public boolean showBarAtMax, showBarAtZero;

    public DataHoldingItem(Properties properties, String dataTag, int max, Color barColor) {
        super(properties);
        this.dataTag = dataTag;
        this.max = max;
        this.barColor = barColor;
    }

    @NotNull
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        set(stack, this.max);
        return stack;
    }

    public void set(ItemStack stack, int amount) {
        if (amount > max) amount = max;
        stack.getOrCreateTag().putInt(this.dataTag, amount);
    }

    public int add(ItemStack stack, int amount) {
        int newAmount = this.get(stack) + amount;
        stack.getOrCreateTag().putInt(this.dataTag, Math.min(newAmount, this.max));
        return Math.max(0, newAmount - max);
    }

    public int get(ItemStack stack) {
        return stack.getOrCreateTag().getInt(this.dataTag);
    }

    /**
     * Use the static methods instead. They're a lot less confusing.
     * @see DataHoldingItem#moveOne(ItemStack, ItemStack)
     * @see DataHoldingItem#moveAll(ItemStack, ItemStack)
     * Moves all the data from another Itemstack to this.
     * to.getItem() should be an instance of this. aka if the "this" object is an InjectionItem, then "to" should be an injection item
     * @param fromAmount The amount of data held by the "from" Itemstack
     * @param to The "to" ItemStack which this Item represents
     * @return How much wasn't moved; the remainder which should be set correspondingly in the "from" ItemStack
     */
    public int move(int fromAmount, ItemStack to) {
        if (get(to) >= max) return -1;
        int newAmount = fromAmount + get(to);
        if (newAmount == get(to)) return -1;
        set(to, Math.min(newAmount, max));
        return newAmount > max ? newAmount - max : 0;
    }

    /**
     * @see DataHoldingItem#move(int, ItemStack)
     */
    public boolean moveOne(int fromAmount, ItemStack to) {
        if (fromAmount == 0) return false;
        if (get(to) >= max) return false;
        add(to, 1);
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return (showBarAtMax || get(stack) != max) && (showBarAtZero || get(stack) != 0);
    }
    @Override
    public int getBarColor(ItemStack stack) {
        return new Color(Math.min(255, 100 + barColor.getRed() * get(stack) / this.max), Math.min(255, 100 + barColor.getGreen() * get(stack) / this.max), Math.min(255, 100 + barColor.getBlue() * get(stack) / this.max)).getRGB();
    }
    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.ceil(13f * get(stack) / this.max);
    }

    public static boolean moveOne(ItemStack from, ItemStack to) {
        if (!(from.getItem() instanceof DataHoldingItem fromItem) || !(to.getItem() instanceof DataHoldingItem toItem)) return false;
        if (toItem.moveOne(fromItem.get(from), to)) {
            fromItem.add(from, -1);
            return true;
        }
        return false;
    }
    public static boolean moveAll(ItemStack from, ItemStack to) {
        if (!(from.getItem() instanceof DataHoldingItem fromItem) || !(to.getItem() instanceof DataHoldingItem toItem)) return false;
        int extra = toItem.move(fromItem.get(from), to);
        if (extra >= 0) {
            fromItem.set(from, extra);
            return true;
        }
        return false;
    }
}
