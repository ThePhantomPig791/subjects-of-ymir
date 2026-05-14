package net.phantompig.soy.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.odm.OdmLevelHelper;
import net.phantompig.soy.odm.physics.OdmHookNode;
import net.phantompig.soy.odm.physics.OdmPlayerNode;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.util.PlayerUtil;

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

                Vec3 offset = player.getLookAngle();
                offset = new Vec3(offset.z * (rightHand ? -1 : 1), 0, offset.x * (rightHand ? 1 : -1));
                offset.scale(0.5);
                node.position = node.position.add(offset);

                if (rightHand) odm.setRightHook(leggings, node.uuid);
                else odm.setLeftHook(leggings, node.uuid);

                OdmPlayerNode playerNode = OdmLevelHelper.createPlayerNode(player.level(), player);
                node.lastNode = playerNode;
                node.distanceToLastNode = node.position.distanceTo(player.position());
                playerNode.nextNode = node;
                playerNode.distanceToNextNode = player.position().distanceTo(node.position);
            }
        }
        PlayerUtil.playSoundToAll(player.level(), player.getX(), player.getY(), player.getZ(), 16, SoySounds.CLICK.get(), SoundSource.PLAYERS, 0.6f, (float) (0.9 + 0.2 * Math.random()));
    }
}
