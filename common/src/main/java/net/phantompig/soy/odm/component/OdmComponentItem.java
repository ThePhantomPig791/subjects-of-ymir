package net.phantompig.soy.odm.component;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.phantompig.soy.item.BladeItem;
import net.phantompig.soy.item.GasHoldingItem;
import net.phantompig.soy.item.SoyItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OdmComponentItem extends GasHoldingItem {
    public final OdmComponent odmComponent;

    public OdmComponentItem(Properties properties, OdmComponent component) {
        super(properties, component.gasCapacity());
        this.odmComponent = component;
    }

    public int getBladeCapacity() {
        return this.odmComponent.bladeCapacity();
    }

    public List<ItemStack> getBlades(ItemStack stack) {
        if (stack.getOrCreateTag().getList("Blades", Tag.TAG_COMPOUND).isEmpty()) stack.getOrCreateTag().put("Blades", new ListTag());
        return stack.getOrCreateTag().getList("Blades", Tag.TAG_COMPOUND).stream().map(tag -> ItemStack.of((CompoundTag) tag)).toList();
    }

    public int getBladeCount(ItemStack stack) {
        int c = 0;
        for (ItemStack b : this.getBlades(stack)) {
            c += b.getCount();
        }
        return c;
    }

    public ItemStack takeBlade(ItemStack stack) {
        ListTag blades = stack.getOrCreateTag().getList("Blades", Tag.TAG_COMPOUND);
        if (blades.isEmpty()) return ItemStack.EMPTY;
        ItemStack bladeStack = ItemStack.of((CompoundTag) blades.remove(0));
        ItemStack remainder = bladeStack.copyWithCount(bladeStack.getCount() - 1);
        bladeStack = bladeStack.copyWithCount(1);
        if (EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.INFINITY_ARROWS)) {
            blades.add(bladeStack.save(new CompoundTag()));
        }
        if (!remainder.isEmpty()) {
            blades.add(remainder.save(new CompoundTag()));
            stack.getOrCreateTag().put("Blades", blades);
        }
        return bladeStack;
    }

    public boolean hasBlade(ItemStack stack) {
        return !stack.getOrCreateTag().getList("Blades", Tag.TAG_COMPOUND).isEmpty();
    }

    public boolean addBlade(ItemStack stack, ItemStack blade) {
        ListTag blades = stack.getOrCreateTag().getList("Blades", Tag.TAG_COMPOUND);
        if (this.getBladeCount(stack) < this.getBladeCapacity()) {
            blades.add(blade.save(new CompoundTag()));
            stack.getOrCreateTag().put("Blades", blades);
            return true;
        }
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.odmComponent.gasCapacity() != 0 && super.isBarVisible(stack);
    }

    /**
     * Consumes gas from the given ItemStack.
     * @return The amount of gas the component couldn't consume. I.E. the leftover gas that needs to be taken from somewhere else
     */
    public int consume(ItemStack stack, int amount) {
        if (EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.INFINITY_ARROWS)) return 0;
        int remaining = get(stack);
        int newAmount = remaining - amount;
        this.set(stack, Math.max(newAmount, 0));
        return Math.max(0, -newAmount);
    }

    public boolean canStrafe(Direction direction) {
        return this.odmComponent.gasStrafeStrength().containsKey(direction);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY) {
            if (other.isEmpty()) {
                if (this.hasBlade(stack)) {
                    access.set(takeBlade(stack));
                    player.playSound(SoundEvents.ARMOR_EQUIP_IRON,0.6f, 0.8f);
                    return true;
                }
            } else if (other.getItem() instanceof BladeItem && addBlade(stack, other.copyWithCount(1))) {
                other.shrink(1);
                player.playSound(SoundEvents.ARMOR_EQUIP_IRON,0.6f, 0.8f);
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, components, isAdvanced);
        components.add(Component.translatable("tooltip.subjects_of_ymir.odm_component"));
        if (this.getBladeCapacity() > 0) {
            components.add(Component.translatable("tooltip.subjects_of_ymir.blades", this.getBladeCount(stack), this.getBladeCapacity()));
        }
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack s = super.getDefaultInstance();
        for (int i = 0; i < this.getBladeCapacity(); i++) {
            addBlade(s, SoyItems.BLADE.get().getDefaultInstance());
        }
        return s;
    }

    public boolean shouldAddDefaultInstanceToCreativeMenu() {
        return this.max > 0 || this.getBladeCapacity() > 0;
    }
}
