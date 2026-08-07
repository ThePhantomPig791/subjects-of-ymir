package net.phantompig.soy.util;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class ShapeUtil {
    public static Vec3 UP = new Vec3(0, 1, 0);
    public static Vec3 DOWN = new Vec3(0, -1, 0);

    public static boolean inOval(Vec3i pos, float r, int rx, int ry, int rz) {
        return (Math.pow(pos.getX(), 2) / rx + Math.pow(pos.getY(), 2) / ry + Math.pow(pos.getZ(), 2) / rz) <= r * r;
    }

    public static boolean withinOvals(Vec3i pos, float r, float outerRadius, int rx, int ry, int rz) {
        return (Math.pow(pos.getX(), 2) / rx + Math.pow(pos.getY(), 2) / ry + Math.pow(pos.getZ(), 2) / rz) <= r * r && !inOval(pos, outerRadius, rx, ry, rz);
    }
}
