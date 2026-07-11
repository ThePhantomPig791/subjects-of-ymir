package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.property.SoyProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @Unique
    private static final Vec3 YELLOW = new Vec3(255, 255, 0);

    @Shadow public abstract Iterable<Entity> entitiesForRendering();

    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    public Vec3 soy$getSkyColor(Vec3 original, @Local(ordinal = 1) float partial) {
        if (SoyConfig.Client.shouldSkyTintOnShift()) {
            int max = 0;
            for (Entity e : this.entitiesForRendering()) {
                if (SoyProperties.PROGRESS.isRegistered(e)) {
                    int progress = SoyProperties.PROGRESS.get(e);
                    if (progress < 15 && progress > max) {
                        max = progress;
                    }
                }
            }
            if (max > 0) {
                return original.lerp(YELLOW, ease(max + partial));
            }
        }
        return original;
    }

    @Unique
    private static double ease(float x) {
        return Math.max(-0.003125 * x * x * x + 0.0549107 * x * x - 0.120536 * x, 0);
    }
}
