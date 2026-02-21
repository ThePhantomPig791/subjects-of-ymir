package net.phantompig.soy.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public abstract class DataHoldingItem extends Item {
    public final String dataTag;
    public final int max;
    public final Color barColor;

    public DataHoldingItem(Properties properties, String dataTag, int max, Color barColor) {
        super(properties);
        this.dataTag = dataTag;
        this.max = max;
        this.barColor = barColor;
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

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return get(stack) != max;
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
