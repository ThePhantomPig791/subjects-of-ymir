package net.phantompig.soy.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.util.PlayerUtil;

import java.awt.*;

public class GasHoldingItem extends DataHoldingItem {
    public GasHoldingItem(Properties properties, int max) {
        super(properties, "Gas", max, new Color(150, 211, 214));
        this.showBarAtMax = this.showBarAtZero = true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack main = player.getMainHandItem(), off = player.getOffhandItem();
        if (player.isCrouching()) {
            if (DataHoldingItem.moveOne(off, main)) {
                transferSound(player.level(), player.getX(), player.getY(), player.getZ(), 1.5f);
                return InteractionResultHolder.success(player.getItemInHand(usedHand));
            }
        } else if (DataHoldingItem.moveAll(off, main)) {
            transferSound(player.level(), player.getX(), player.getY(), player.getZ(), 0.6f);
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }
        return super.use(level, player, usedHand);
    }

    private static void transferSound(Level level, double x, double y, double z, float volume) {
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoySounds.GAS_BURST.get(), SoundSource.PLAYERS, volume, 1.8f + (float) (0.1 * Math.random()));
    }
}