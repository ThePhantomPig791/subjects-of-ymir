package net.phantompig.soy.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.entity.TitanCorpseEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow @Final private double x;

    @Shadow @Final private double y;

    @Shadow @Final private double z;

    @ModifyExpressionValue(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D", opcode = Opcodes.GETFIELD))
    public double soy$fixExplosions(double original, @Local Entity entity) {
        if ((entity instanceof SoyPlayerExtension ext && ext.getTitanInstance().getProgress() > 0) || entity instanceof TitanCorpseEntity) {
            return entity.getEyePosition().distanceToSqr(new Vec3(this.x, this.y, this.z)) * 0.7 + original * 0.3;
        }
        return original;
    }
}
