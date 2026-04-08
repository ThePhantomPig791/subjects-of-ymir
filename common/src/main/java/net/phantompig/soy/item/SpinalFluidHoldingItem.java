package net.phantompig.soy.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class SpinalFluidHoldingItem extends DataHoldingItem {
    public SpinalFluidHoldingItem(Properties properties, int max) {
        super(properties, "SpinalFluid", max, new Color(131, 89, 255));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand == InteractionHand.OFF_HAND) return InteractionResultHolder.pass(stack);
        if (level.isClientSide()) return InteractionResultHolder.pass(stack);
        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offhandStack.getItem() instanceof SpinalFluidHoldingItem osfhi && stack.getItem() instanceof SpinalFluidHoldingItem msfhi) {
            if (player.isCrouching()) {
                int extra = msfhi.move(osfhi.get(offhandStack), stack);
                if (extra > -1) {
                    osfhi.set(offhandStack, extra);
                    handleUnstackableness(stack);
                    transferSound(player.level(), player.getX(), player.getY(), player.getZ());
                    return InteractionResultHolder.success(stack);
                }
            } else if (msfhi.moveOne(osfhi.get(offhandStack), stack)) {
                msfhi.handleUnstackableness(stack);
                osfhi.add(offhandStack, -1);
                osfhi.handleUnstackableness(offhandStack);
                transferSound(player.level(), player.getX(), player.getY(), player.getZ());
                return InteractionResultHolder.pass(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    private static void transferSound(Level level, double x, double y, double z) {
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.HONEY_BLOCK_STEP, SoundSource.PLAYERS, 0.5f, 1.4f);
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5f, 1.8f);
    }

    private void handleUnstackableness(ItemStack stack) {
        if (stack.getMaxStackSize() == 1) return;
        if (get(stack) == 0) {
            if (stack.getOrCreateTag().contains("UnstackableTag")) stack.getOrCreateTag().remove("UnstackableTag");
        } else {
            stack.getOrCreateTag().putInt("UnstackableTag", (int) (100000 * Math.random()));
        }
    }
}
