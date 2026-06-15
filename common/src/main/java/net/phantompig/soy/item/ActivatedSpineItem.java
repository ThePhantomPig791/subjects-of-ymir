package net.phantompig.soy.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.TitanInstance;
import org.jetbrains.annotations.NotNull;

public class ActivatedSpineItem extends Item {
    public ActivatedSpineItem(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, livingEntity);
        if (level.isClientSide()) return itemStack;

        Player player = livingEntity instanceof Player ? (Player) livingEntity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        }

        TitanInstance.sequentialRandomizeFor(livingEntity);
        if (livingEntity instanceof SoyPlayerExtension ext) {
            ext.getTitanInstance().setCharge(520);
        }

        itemStack.shrink(1);
        return itemStack;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (
                !level.isClientSide()
                && player instanceof SoyPlayerExtension ext
                && ext.getTitanInstance().titan == null
        ) {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.pass(itemStack);
        }
    }
}
