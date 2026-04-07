package net.phantompig.soy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.TitanInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SpineItem extends Item {
    public SpineItem(Properties properties) {
        super(properties);
    }

    public static CompoundTag getTitanInstanceTag(ItemStack stack) {
        return stack.getOrCreateTag().getCompound("TitanInstance");
    }

    public static String getPreviousInheritorName(ItemStack stack) {
        var tag = getTitanInstanceTag(stack);
        if (tag.contains("PreviousInheritor") && tag.getCompound("PreviousInheritor").contains("Name")) {
            return tag.getCompound("PreviousInheritor").getString("Name");
        }
        return "";
    }
    public static UUID getPreviousInheritorUUID(ItemStack stack) {
        var tag = getTitanInstanceTag(stack);
        if (tag.contains("PreviousInheritor") && tag.getCompound("PreviousInheritor").contains("UUID")) {
            return tag.getCompound("PreviousInheritor").getUUID("UUID");
        }
        return UUID.randomUUID();
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, livingEntity);
        if (level.isClientSide()) return itemStack;

        Player player = livingEntity instanceof Player ? (Player)livingEntity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        TitanInstance.setFromSpineItem(livingEntity, itemStack);

        itemStack.shrink(1);
        return itemStack;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (
                !level.isClientSide()
                && !getTitanInstanceTag(itemStack).isEmpty()
                && !player.getUUID().equals(getPreviousInheritorUUID(itemStack))
                && player instanceof SoyPlayerExtension ext
                && ext.getTitanInstance().titan == null
        ) {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.pass(itemStack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        String name = getPreviousInheritorName(stack);
        if (!name.isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.subjects_of_ymir.spine", name).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
        }
    }
}
