package net.phantompig.soy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.threetag.palladium.item.AddonArmorItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TooltippedAddonArmorItem extends AddonArmorItem {
    public final Component tooltip;

    public TooltippedAddonArmorItem(Component tooltip, ArmorMaterial armorMaterial, Type type, Properties properties) {
        super(armorMaterial, type, properties);
        this.tooltip = tooltip;
    }
    public TooltippedAddonArmorItem(String tooltip, ArmorMaterial armorMaterial, Type type, Properties properties) {
        this(Component.translatable(tooltip).withStyle(ChatFormatting.ITALIC), armorMaterial, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(tooltip);
    }
}
