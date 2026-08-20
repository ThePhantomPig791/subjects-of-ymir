package net.phantompig.soy.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.power.ability.GrabbingAbility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderPlayerArm", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;)V"))
    public void soy$renderRightHand(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, float equippedProgress, float swingProgress, HumanoidArm side, CallbackInfo ci) {
        if (GrabbingAbility.isGrabbingSomething(this.minecraft.player)) {
            var player = this.minecraft.player;
            LivingEntity grabbed = null;
            for (Entity e : player.level().getEntities(player, player.getBoundingBox().inflate(40))) {
                if (e instanceof LivingEntity living && GrabbingAbility.getGrabbing(player).equals(e.getUUID())) {
                    grabbed = living;
                    break;
                }
            }
            if (grabbed == null) return;

            poseStack.pushPose();
            poseStack.translate(-0.4, 0.7, 0);
            float scaleFactor = 2 / player.getBbHeight();
            poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
            poseStack.mulPose(Direction.NORTH.getRotation());

            EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(grabbed);
            renderer.render(grabbed, grabbed.getYRot(), Minecraft.getInstance().getDeltaFrameTime(), poseStack, buffer, 15728641);

            poseStack.popPose();
        }
    }
}
