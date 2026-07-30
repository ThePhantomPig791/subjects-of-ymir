package net.phantompig.soy.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.entity.SoyDamageSources;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.entity.ThrownBladeEntity;
import net.phantompig.soy.odm.component.OdmComponentItem;
import net.phantompig.soy.player.SoyServerPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.util.PlayerUtil;

public class BladeHandleItem extends ItemStackHoldingItem implements OdmHandleItem {
    public static Vec3 UP = new Vec3(0, 1, 0);
    public static Vec3 DOWN = new Vec3(0, -1, 0);

    public BladeHandleItem(Properties properties) {
        super(stack -> stack.getItem() instanceof BladeItem || stack.is(Items.IRON_NUGGET), SoundEvents.ARMOR_EQUIP_CHAIN, properties);
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
            throwBlade(level, player, UP, player.getMainArm() == HumanoidArm.RIGHT ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            throwBlade(level, player, DOWN, player.getMainArm() == HumanoidArm.RIGHT ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);

            if (player instanceof SoyServerPlayerExtension ext) {
                ext.soy$getCombatSystem().stopHolding();
            }
            return InteractionResultHolder.consume(player.getItemInHand(usedHand));
        } else {
            if (
                    player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BladeHandleItem bladeM && bladeM.getStack(player.getItemInHand(InteractionHand.MAIN_HAND)).getItem() instanceof BladeItem
                    && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof BladeHandleItem bladeO && bladeO.getStack(player.getItemInHand(InteractionHand.OFF_HAND)).getItem() instanceof BladeItem
            ) {
                return InteractionResultHolder.pass(player.getItemInHand(usedHand));
            }
            ItemStack stack = player.getItemInHand(usedHand);
            ItemStack containedStack = this.getStack(stack);
            if (containedStack.isEmpty()) {
                ItemStack odmStack = player.getItemBySlot(EquipmentSlot.LEGS);
                if (odmStack.getItem() instanceof OdmAttachableAddonArmorItem odm) {
                    boolean rightHand = (usedHand == InteractionHand.MAIN_HAND) == (player.getMainArm() == HumanoidArm.RIGHT);
                    ItemStack[] sheaths = new ItemStack[2]; // prioritize opposite-sided sheath
                    sheaths[rightHand ? 0 : 1] = odm.getLeftSheath(odmStack);
                    sheaths[rightHand ? 1 : 0] = odm.getRightSheath(odmStack);
                    for (ItemStack sheath : sheaths) {
                        if (sheath.getItem() instanceof OdmComponentItem component) {
                            ItemStack blade = component.takeBlade(sheath);
                            if (!blade.isEmpty()) {
                                this.setStack(stack, blade);
                                PlayerUtil.playSoundToAll(level, player.getX(), player.getY(), player.getZ(), 32, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 0.6f, 0.75f + 0.1f * (float) Math.random());
                                break;
                            }
                        }
                    }
                }
            } else {
                if (
                        (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BladeHandleItem bladeM && bladeM.getStack(player.getItemInHand(InteractionHand.MAIN_HAND)).is(Items.IRON_NUGGET))
                        || (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof BladeHandleItem bladeO && bladeO.getStack(player.getItemInHand(InteractionHand.OFF_HAND)).is(Items.IRON_NUGGET))
                ) {
                    dropBrokenBlade(level, player, DOWN, player.getMainArm() == HumanoidArm.RIGHT ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
                    dropBrokenBlade(level, player, UP, player.getMainArm() == HumanoidArm.RIGHT ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    return InteractionResultHolder.consume(player.getItemInHand(usedHand));
                }
            }
        }
        return super.use(level, player, usedHand);
    }

    public static void throwBlade(Level level, Player player, Vec3 normal, EquipmentSlot slot) {
        Vec3 offset = player.getEyePosition().add(player.getLookAngle().multiply(1, 0, 1).cross(normal).scale(0.3)).add(0, -1, 0);
        ItemStack stack = player.getItemBySlot(slot);
        if (stack.getItem() instanceof BladeHandleItem handle && handle.getStack(stack).getItem() instanceof BladeItem) {
            ThrownBladeEntity blade = new ThrownBladeEntity(SoyEntities.THROWN_BLADE.get(), level);
            blade.setOwner(player);
            blade.setPos(offset);
            blade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 3, 0.1f);
            level.addFreshEntity(blade);

            handle.setStack(stack, Items.AIR.getDefaultInstance());
            PlayerUtil.playSoundToAll(level, player.getX(), player.getY(), player.getZ(), 32, SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5f, 0.6f + 0.1f * (float) Math.random());
            PlayerUtil.playSoundToAll(level, player.getX(), player.getY(), player.getZ(), 32, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 1, 1.5f + 0.1f * (float) Math.random());
        }
    }

    public static void dropBrokenBlade(Level level, Player player, Vec3 normal, EquipmentSlot slot) {
        Vec3 offset = getOffset(player, normal);
        ItemStack stack = player.getItemBySlot(slot);
        if (stack.getItem() instanceof BladeHandleItem handle && handle.getStack(stack).is(Items.IRON_NUGGET)) {
            ItemEntity entity = new ItemEntity(level, offset.x, offset.y, offset.z, handle.getStack(stack));
            entity.addDeltaMovement(player.getLookAngle().scale(0.5));
            level.addFreshEntity(entity);

            handle.setStack(stack, Items.AIR.getDefaultInstance());
            PlayerUtil.playSoundToAll(level, player.getX(), player.getY(), player.getZ(), 32, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 0.7f, 0.8f + 0.1f * (float) Math.random());
        }
    }

    private static Vec3 getOffset(Player player, Vec3 normal) {
        return player.getEyePosition().add(player.getLookAngle().multiply(1, 0, 1).cross(normal).scale(0.3)).add(0, -1, 0);
    }

    @Override
    public void playInsertSound(Player player, float pitch) {
        player.playSound(this.insertSound,0.6f, pitch / 2);
    }
}
