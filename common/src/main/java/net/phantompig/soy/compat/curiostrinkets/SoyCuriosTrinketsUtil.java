package net.phantompig.soy.compat.curiostrinkets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.threetag.palladium.compat.curiostinkets.CuriosTrinketsSlotInv;
import net.threetag.palladium.compat.curiostinkets.CuriosTrinketsUtil;

import java.util.ArrayList;
import java.util.List;

public class SoyCuriosTrinketsUtil {
    public static SoyCuriosTrinketsUtil INSTANCE = new SoyCuriosTrinketsUtil();

    public SoyCuriosTrinketsUtil() {}

    public static List<ItemStack> getItemsInSlot(LivingEntity entity, CuriosTrinketsUtil.Slot slot) {
        CuriosTrinketsSlotInv inv = CuriosTrinketsUtil.getInstance().getSlot(entity, slot);
        List<ItemStack> items = new ArrayList();

        for(int i = 0; i < inv.getSlots(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }

        return items;
    }

    public boolean isLoaded() {
        return false;
    }

    public void write(LivingEntity entity, CompoundTag tag) {}

    public void read(LivingEntity entity, CompoundTag tag) {}

    public void clear(LivingEntity entity) {}
}
