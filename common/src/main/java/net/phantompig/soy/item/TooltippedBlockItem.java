package net.phantompig.soy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TooltippedBlockItem extends BlockItem {
    public final Component tooltip;

    public TooltippedBlockItem(Component tooltip, Block block, Properties properties) {
        super(block, properties);
        this.tooltip = tooltip;
    }
    public TooltippedBlockItem(String tooltip, Block block, Properties properties) {
        this(Component.translatable(tooltip).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC), block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(tooltip);
    }
}
