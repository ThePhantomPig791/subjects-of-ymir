package net.phantompig.soy.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FlareCartridgeItem extends Item implements DyeableLeatherItem {
    public FlareCartridgeItem(Properties properties) {
        super(properties);
    }

    public boolean hasColor(ItemStack stack) {
        CompoundTag compoundTag = stack.getTagElement("display");
        return compoundTag != null && compoundTag.contains("color", 99);
    }

    public String getColorString(ItemStack stack) {
        return hasColor(stack) ? convertColorToString(stack.getTagElement("display").getInt("color")) : "Blank";
    }

    @Override
    public int getColor(ItemStack stack) {
        return hasColor(stack) ? stack.getTagElement("display").getInt("color") : 0xFFFFFF;
    }

    private static String convertColorToString(int color) {
        return "#" + Integer.toHexString(color).toUpperCase();
    }
}
