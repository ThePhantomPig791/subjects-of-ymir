package net.phantompig.soy.odm;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.phantompig.soy.entity.OdmNodeEntity;
import net.phantompig.soy.entity.SoyEntities;

import java.util.UUID;

public class OdmLevelHelper {
    public static OdmNodeEntity shootHook(Level level, Player player, double strength) {
        if (level instanceof ServerLevel) {
            OdmNodeEntity node = new OdmNodeEntity(SoyEntities.ODM_NODE.get(), level);
            node.setPos(player.position());
            node.setDeltaMovement(player.getLookAngle().scale(strength).add(player.getDeltaMovement()));
            node.setOwner(player);
            level.addFreshEntity(node);
            return node;
        }
        return null;
    }
    public static OdmNodeEntity shootHook(Level level, Player player) {
        return shootHook(level, player, 8);
    }
    public static void removeNode(Level level, UUID uuid) {
        if (level instanceof ServerLevel sl) {
            var entity = sl.getEntity(uuid);
            if (entity != null) entity.discard();
        }
    }
}
