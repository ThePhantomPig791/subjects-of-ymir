package net.phantompig.soy.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.phantompig.soy.entity.SoyDamageSources;

public class BladeHandleItem extends ItemStackHoldingItem implements OdmHandleItem {

    public BladeHandleItem(Properties properties) {
        super(stack -> stack.getItem() instanceof BladeItem, SoundEvents.ARMOR_EQUIP_CHAIN, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.getStack(stack).getItem() instanceof BladeItem) {
            float speedSqr = (float) attacker.getDeltaMovement().add(target.getDeltaMovement().reverse()).lengthSqr();
            target.hurt(SoyDamageSources.slice(attacker.level(), attacker), 6 + 10 * speedSqr / (speedSqr + 8));
            if (Math.random() < 0.02) {
                this.setStack(stack, Items.IRON_NUGGET.getDefaultInstance());
                attacker.broadcastBreakEvent(attacker.getItemBySlot(EquipmentSlot.MAINHAND).equals(stack) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return true;
        }
        return false;
    }

    @Override
    public void playInsertSound(Player player, float pitch) {
        player.playSound(this.insertSound,0.6f, pitch / 2);
    }
}
