package net.phantompig.soy.util;

import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladiumcore.util.Platform;
import org.joml.Vector3f;

public class ShapeUtil {
    public static Vec3 UP = new Vec3(0, 1, 0);
    public static Vec3 DOWN = new Vec3(0, -1, 0);

    public static boolean inOval(Vec3i pos, float r, int rx, int ry, int rz) {
        return (Math.pow(pos.getX(), 2) / rx + Math.pow(pos.getY(), 2) / ry + Math.pow(pos.getZ(), 2) / rz) <= r * r;
    }

    public static boolean withinOvals(Vec3i pos, float r, float outerRadius, int rx, int ry, int rz) {
        return (Math.pow(pos.getX(), 2) / rx + Math.pow(pos.getY(), 2) / ry + Math.pow(pos.getZ(), 2) / rz) <= r * r && !inOval(pos, outerRadius, rx, ry, rz);
    }

    public static void outlineBox(Level level, AABB box, Vector3f color) {
        if (Platform.isProduction()) return;
        for (float x = 0; x <= 1; x += 0.1f) {
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.minX + box.getXsize() * x,
                    box.minY,
                    box.minZ,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.minX + box.getXsize() * x,
                    box.maxY,
                    box.minZ,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.minX + box.getXsize() * x,
                    box.maxY,
                    box.maxZ,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.minX + box.getXsize() * x,
                    box.minY,
                    box.maxZ,
                    0, 0, 0,
                    0, 1
            );
        }

        for (float y = 0; y <= 1; y += 0.1f) {
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.minX,
                    box.minY + box.getYsize() * y,
                    box.minZ,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.maxX,
                    box.minY + box.getYsize() * y,
                    box.minZ,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.maxX,
                    box.minY + box.getYsize() * y,
                    box.maxZ,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.minX,
                    box.minY + box.getYsize() * y,
                    box.maxZ,
                    0, 0, 0,
                    0, 1
            );
        }

        for (float z = 0; z <= 1; z += 0.1f) {
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.minX,
                    box.minY,
                    box.minZ + box.getZsize() * z,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.maxX,
                    box.minY,
                    box.minZ + box.getZsize() * z,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.maxX,
                    box.maxY,
                    box.minZ + box.getZsize() * z,
                    0, 0, 0,
                    0, 1
            );
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(color, 1),
                    false,
                    box.minX,
                    box.maxY,
                    box.minZ + box.getZsize() * z,
                    0, 0, 0,
                    0, 1
            );
        }
    }

    public static void highlightVector(Level level, Vec3 start, Vec3 delta) {
        if (Platform.isProduction()) return;
        Vec3 end = start.add(delta);
        for (float i = 0; i <= 1; i += 0.05f) {
            Vec3 pos = start.lerp(end, i);
            PlayerUtil.spawnParticleForAll(level,
                    32,
                    new DustParticleOptions(new Vector3f(i, i, i), 0.5f + 2f * i),
                    false,
                    pos.x, pos.y, pos.z,
                    0, 0, 0,
                    0, 1
            );
        }
    }

    public static Vec3 vectorProjection(Vec3 a, Vec3 b) {
        return b.scale(a.dot(b) / b.lengthSqr());
    }
}
