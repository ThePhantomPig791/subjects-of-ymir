package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;position(F)F")
    )
    public float soy$getWalkAnimationPosition(float original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof SoyPlayerExtension extension && extension.getTitanInstance().getProgress() > 0) {
            return original * 0.6f;
        }
        return original;
    }
}
