package net.phantompig.soy.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.odm.OdmLevelHelper;
import net.phantompig.soy.odm.physics.OdmHookNode;
import net.phantompig.soy.odm.physics.OdmPlayerNode;

import java.util.UUID;

public class OdmHandleItem extends Item {
    public OdmHandleItem(Properties properties) {
        super(properties);
    }

    public void handlePress(Player player, ItemStack item, boolean rightHand) {
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.getItem() instanceof OdmAttachableAddonArmorItem odm) {
            UUID hook;
            if ((hook = rightHand ? odm.getRightHook(leggings) : odm.getLeftHook(leggings)) != null) {
                OdmLevelHelper.removeNode(player.level(), hook);

                if (rightHand) odm.removeRightHook(leggings);
                else odm.removeLeftHook(leggings);
            } else if (odm.consumeGas(leggings, 1)) {
                OdmHookNode node = OdmLevelHelper.shootHook(player.level(), player);

                if (rightHand) odm.setRightHook(leggings, node.uuid);
                else odm.setLeftHook(leggings, node.uuid);

                OdmPlayerNode playerNode = OdmLevelHelper.createPlayerNode(player.level(), player);
                node.lastNode = playerNode;
                playerNode.nextNode = node;
            }
        }
    }
}
