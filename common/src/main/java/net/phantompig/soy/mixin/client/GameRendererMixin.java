package net.phantompig.soy.mixin.client;

import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.client.renderer.ScreenShakeManager;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import net.phantompig.soy.util.ShapeUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final Minecraft minecraft;

    @Unique
    private final AtomicDouble soy$cameraPitch = new AtomicDouble(0), soy$cameraRoll = new AtomicDouble(0);

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    public void renderLevel(float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci, @Local(ordinal = 1) PoseStack poseStack2) {
        if (this.minecraft.player != null && !this.minecraft.player.isPassenger()) {
            soy$adjustCameraAngle(ShapeUtil.UP, soy$cameraPitch, 0.4);
            soy$adjustCameraAngle(this.minecraft.player.getLookAngle().cross(ShapeUtil.UP), soy$cameraRoll, 0.715);
        }
        if (this.soy$cameraPitch.get() != 0) {
            poseStack2.mulPose(Axis.XN.rotationDegrees(this.soy$cameraPitch.floatValue()));
        }
        if (this.soy$cameraRoll.get() != 0) {
            poseStack2.mulPose(Axis.ZP.rotationDegrees(this.soy$cameraRoll.floatValue()));
        }

        ScreenShakeManager.update(poseStack2);
    }

    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    public double soy$getFov(double original, @Local(argsOnly = true) float partialTicks) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof OdmAttachableAddonArmorItem) {
            double spd = Minecraft.getInstance().player.getDeltaMovementLerped(partialTicks).multiply(1, 0.5, 1).lengthSqr();
            spd = spd / (spd + 20);
            original += 30 * spd * this.minecraft.options.fovEffectScale().get();
        }
        return original;
    }

    @Unique
    private void soy$adjustCameraAngle(Vec3 compareTo, AtomicDouble ref, double scale) {
        if (this.minecraft.player != null && !this.minecraft.player.onGround() && this.minecraft.player.getDeltaMovement().lengthSqr() > 0.01 && this.minecraft.player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof OdmAttachableAddonArmorItem) {
            ref.set(SoyConfig.Client.getOdmScreenTiltStrength() * scale * ref.addAndGet(this.minecraft.player.getDeltaMovement().dot(compareTo)));
        } else if (ref.get() != 0) {
            ref.set(ref.get() * 0.99);
            if (ref.get() < 0.001) ref.set(0);
        }
    }
}
