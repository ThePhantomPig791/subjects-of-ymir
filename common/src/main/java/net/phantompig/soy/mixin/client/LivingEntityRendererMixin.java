package net.phantompig.soy.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.util.RenderingUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;position(F)F")
    )
    public float soy$getWalkAnimationPosition(float original, @Local(argsOnly = true) LivingEntity entity) {
        if (entity instanceof SoyPlayerExtension extension && extension.soy$getTitanInstance().getProgress() > 0) {
            return original * 0.6f;
        }
        return original;
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void soy$renderHead(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        poseStack.pushPose();
        if (RenderingUtil.transformPosestackForGrabbedEntity(entity, poseStack, partialTicks) && packedLight != 15728641) {
            // the packed light is being used as a "marker".
            // if it's specifically 15728641, we know this is being rendered from ItemInHandRendererMixin and we shouldn't cancel the rendering. god this took me so long to figure out
            // i should note. this is so scuffed and there *must* be a better way
            ci.cancel();
            poseStack.popPose();
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL")
    )
    public void soy$renderTail(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        poseStack.popPose();
    }
}