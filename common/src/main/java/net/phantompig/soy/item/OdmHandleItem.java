package net.phantompig.soy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.phantompig.soy.odm.OdmLevelHelper;
import net.phantompig.soy.odm.physics.OdmHookNode;
import net.phantompig.soy.odm.physics.OdmPlayerNode;

import java.util.UUID;

public class OdmHandleItem extends Item {
    public OdmHandleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        boolean rightHand = (player.getMainArm() == HumanoidArm.RIGHT) == (usedHand == InteractionHand.MAIN_HAND);
        if (leggings.getItem() instanceof OdmAttachableAddonArmorItem odm) {
            UUID hook;
            if ((hook = rightHand ? odm.getRightHook(leggings) : odm.getLeftHook(leggings)) != null) {
                OdmLevelHelper.removeNode(level, hook);
                if (rightHand) odm.removeRightHook(leggings);
                else odm.removeLeftHook(leggings);
            } else {
                OdmHookNode node = OdmLevelHelper.shootHook(level, player);
                if (rightHand) odm.setRightHook(leggings, node.uuid);
                else odm.setLeftHook(leggings, node.uuid);
                OdmPlayerNode playerNode = OdmLevelHelper.createPlayerNode(level, player);
                node.lastNode = playerNode;
                playerNode.nextNode = node;
            }
        }
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }
}
