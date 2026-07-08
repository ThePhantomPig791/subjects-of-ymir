package net.phantompig.soy.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.entity.SoyDamageSources;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.entity.ThrownBladeEntity;
import net.phantompig.soy.player.SoyServerPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.util.PlayerUtil;

public class BladeHandleItem extends ItemStackHoldingItem implements OdmHandleItem {
    public static Vec3 UP = new Vec3(0, 1, 0);
    public static Vec3 DOWN = new Vec3(0, -1, 0);

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (SoyProperties.ODM_HOLD_ATTACK_INCREASING.get(player)) {
            {
                Vec3 rightOffset = player.getEyePosition().add(player.getLookAngle().multiply(1, 0, 1).cross(UP).scale(0.6));
                ItemStack rightStack = player.getItemBySlot(player.getMainArm() == HumanoidArm.RIGHT ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                if (rightStack.getItem() instanceof BladeHandleItem handle && handle.getStack(rightStack).getItem() instanceof BladeItem) {
                    ThrownBladeEntity rightBlade = new ThrownBladeEntity(SoyEntities.THROWN_BLADE.get(), level);
                    rightBlade.setPos(rightOffset);
                    rightBlade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 3, 0.1f);
                    level.addFreshEntity(rightBlade);

                    handle.setStack(rightStack, Items.AIR.getDefaultInstance());
                    PlayerUtil.playSoundToAll(level, player.getX(), player.getY(), player.getZ(), 32, SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5f, 0.6f + 0.1f * (float) Math.random());
                    PlayerUtil.playSoundToAll(level, player.getX(), player.getY(), player.getZ(), 32, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 1, 1.5f + 0.1f * (float) Math.random());
                }
            }

            {
                Vec3 leftOffset = player.getEyePosition().add(player.getLookAngle().multiply(1, 0, 1).cross(DOWN).scale(0.6));
                ItemStack leftStack = player.getItemBySlot(player.getMainArm() == HumanoidArm.RIGHT ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
                if (leftStack.getItem() instanceof BladeHandleItem handle && handle.getStack(leftStack).getItem() instanceof BladeItem) {
                    ThrownBladeEntity leftBlade = new ThrownBladeEntity(SoyEntities.THROWN_BLADE.get(), level);
                    leftBlade.setPos(leftOffset);
                    leftBlade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 3, 0.1f);
                    level.addFreshEntity(leftBlade);

                    handle.setStack(leftStack, Items.AIR.getDefaultInstance());
                    PlayerUtil.playSoundToAll(level, player.getX(), player.getY(), player.getZ(), 32, SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5f, 0.6f + 0.1f * (float) Math.random());
                    PlayerUtil.playSoundToAll(level, player.getX(), player.getY(), player.getZ(), 32, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 1, 1.5f + 0.1f * (float) Math.random());
                }
            }

            if (player instanceof SoyServerPlayerExtension ext) {
                ext.soy$getCombatSystem().stopHolding();
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void playInsertSound(Player player, float pitch) {
        player.playSound(this.insertSound,0.6f, pitch / 2);
    }
}
