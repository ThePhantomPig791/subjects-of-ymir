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

    // to.getItem() should be an instance of this. aka if the "this" object is an InjectionItem, then "to" should be an injection item
    public int move(int fromAmount, ItemStack to) {
        if (get(to) >= max) return -1;
        int newAmount = fromAmount + get(to);
        if (newAmount == get(to)) return -1;
        set(to, Math.min(newAmount, max));
        return newAmount > max ? newAmount - max : 0;
    }
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
}
