package net.phantompig.soy.odm;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.entity.OdmNodeEntity;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.item.BladeHandleItem;

import java.util.Optional;
import java.util.UUID;

public class OdmLevelHelper {
    public static OdmNodeEntity shootHook(Level level, Player player, Vec3 along, double strength) {
        if (level instanceof ServerLevel) {
            OdmNodeEntity node = new OdmNodeEntity(SoyEntities.ODM_NODE.get(), level);
            node.setPos(player.position());
            node.setDeltaMovement(along.scale(strength).add(vectorProjection(player.getDeltaMovement(), along)));
            node.setOwner(player);
            level.addFreshEntity(node);
            return node;
        }
        return null;
    }
    public static OdmNodeEntity shootHook(Level level, Player player, boolean right) {
        Vec3 along = player.getLookAngle();
        if (player.isCrouching()) {
            along = along.lerp(along.multiply(1, 0, 1).cross(right ? BladeHandleItem.UP : BladeHandleItem.DOWN), 0.25);
        }
        return shootHook(level, player, along, 8);
    }
    public static Optional<OdmNodeEntity> getNode(Level level, UUID uuid) {
        if (level instanceof ServerLevel sl && sl.getEntity(uuid) instanceof OdmNodeEntity e) {
            return Optional.of(e);
        }
        return Optional.empty();
    }
    public static void removeNode(Level level, UUID uuid) {
        if (uuid == null) return;
        if (level instanceof ServerLevel sl) {
            var entity = sl.getEntity(uuid);
            if (entity != null) entity.discard();
        }
    }

    private static Vec3 vectorProjection(Vec3 a, Vec3 b) {
        return b.scale(a.dot(b) / b.lengthSqr());
    }
}
