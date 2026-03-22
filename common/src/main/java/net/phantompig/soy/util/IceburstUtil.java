package net.phantompig.soy.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.util.PlayerUtil;

public class IceburstUtil {
    public static void explode(Vec3 pos, Level level, float strength, AABB box) {
        level.getEntities(null, box).forEach(e -> {
            e.addDeltaMovement(pos
                    .subtract(e.getEyePosition())
                    .normalize()
                    .reverse()
                    .scale(Math.min(2, strength / pos.distanceToSqr(e.getEyePosition()) / Math.pow(e.getBoundingBox().getYsize() / 2, 2)))
            );
            if (e instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        });
        PlayerUtil.playSoundToAll(level, pos.x, pos.y, pos.z, 24, SoySounds.GAS_BURST.get(), SoundSource.BLOCKS, strength / 6f, 1 - (0.1f * strength / 8));
        PlayerUtil.spawnParticleForAll(
                level,
                64,
                ParticleTypes.CLOUD,
                false,
                pos.x(),
                pos.y(),
                pos.z(),
                0,
                0,
                0,
                strength / 10,
                (int) strength
        );
    }
}
