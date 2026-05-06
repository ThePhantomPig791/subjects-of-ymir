package net.phantompig.soy.odm;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.phantompig.soy.odm.physics.OdmHookNode;
import net.phantompig.soy.odm.physics.OdmNode;

import java.util.UUID;

public class OdmLevelHelper {
    public static void addNode(Level level, OdmNode hook) {
        if (level instanceof OdmServerLevelHolder holder) {
            holder.soy$getOdmServerLevel().addNode(hook);
        }
    }
    public static OdmNode shootHook(Level level, Player player, double strength) {
        if (level instanceof OdmServerLevelHolder holder) {
            OdmHookNode hook = new OdmHookNode(holder.soy$getOdmServerLevel(), player, player.getEyePosition(), player.getLookAngle().scale(strength).add(player.getDeltaMovement()));
            addNode(level, hook);
            return hook;
        }
        return null;
    }
    public static OdmNode shootHook(Level level, Player player) {
        return shootHook(level, player, 10);
    }

    public static OdmNode addNodeAt(Level level, Player player) {
        if (level instanceof OdmServerLevelHolder holder) {
            OdmNode node = new OdmNode(holder.soy$getOdmServerLevel(), player, player.position().add(0, 1, 0), player.getDeltaMovement());
            addNode(level, node);
            return node;
        }
        return null;
    }

    public static OdmNode getNode(Level level, UUID uuid) {
        if (level instanceof OdmServerLevelHolder holder) {
            return holder.soy$getOdmServerLevel().getNode(uuid);
        }
        return null;
    }
    public static void removeNode(Level level, UUID uuid) {
        if (level instanceof OdmServerLevelHolder holder) {
            holder.soy$getOdmServerLevel().removeNode(uuid);
        }
    }

    public static void clearNodes(Level level) {
        if (level instanceof OdmServerLevelHolder holder) {
            holder.soy$getOdmServerLevel().clearNodes();
        }
    }
}
