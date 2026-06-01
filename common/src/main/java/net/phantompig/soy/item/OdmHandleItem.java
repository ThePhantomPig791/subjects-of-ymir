package net.phantompig.soy.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.entity.OdmNodeEntity;
import net.phantompig.soy.odm.OdmLevelHelper;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.util.PlayerUtil;

import java.util.UUID;

public class OdmHandleItem extends Item {
    public OdmHandleItem(Properties properties) {
        super(properties);
    }

    public void handlePress(Player player, ItemStack itemStack, boolean rightHand) {
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.getItem() instanceof OdmAttachableAddonArmorItem odm) {
            UUID hook;
            if ((hook = rightHand ? odm.getRightHook(leggings) : odm.getLeftHook(leggings)) != null && OdmLevelHelper.getNode(player.level(), hook).isPresent()) {
                OdmLevelHelper.removeNode(player.level(), hook);

                if (rightHand) odm.removeRightHook(leggings);
                else odm.removeLeftHook(leggings);
            } else if (!odm.getTurbine(leggings).isEmpty() && odm.consumeGas(leggings, 1)) {
                OdmNodeEntity node = OdmLevelHelper.shootHook(player.level(), player);

                node.setRight(rightHand);
                node.setPos(node.position().add(node.getRightOrLeftOffset(player.getXRot(), player.yBodyRot, rightHand)));

                if (rightHand) odm.setRightHook(leggings, node.getUUID());
                else odm.setLeftHook(leggings, node.getUUID());

                PlayerUtil.playSoundToAll(player.level(), player.getX(), player.getY(), player.getZ(), 64, SoySounds.HOOK_LAUNCH.get(), SoundSource.PLAYERS, 1, (float) (0.95 + 0.1 * Math.random()));
            }
        }
        PlayerUtil.playSoundToAll(player.level(), player.getX(), player.getY(), player.getZ(), 16, SoySounds.CLICK.get(), SoundSource.PLAYERS, 0.6f, (float) (0.9 + 0.2 * Math.random()));
    }
}
