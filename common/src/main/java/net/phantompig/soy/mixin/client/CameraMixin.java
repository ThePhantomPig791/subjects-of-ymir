package net.phantompig.soy.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.power.ability.GrabbingAbility;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.entity.BodyPart;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPosition(Vec3 pos);

    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @Shadow private float xRot;

    @Shadow private float yRot;

    @Inject(method = "setup", at = @At("TAIL"))
    public void soy$setup(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (SoyProperties.GRABBED.isRegistered(entity) && SoyProperties.GRABBED.get(entity)) {
            LivingEntity grabber = null;
            for (Entity e : entity.level().getEntities(entity, entity.getBoundingBox().inflate(40))) {
                if (e instanceof LivingEntity living && GrabbingAbility.getGrabbing(living).equals(entity.getUUID())) {
                    grabber = living;
                    break;
                }
            }
            if (grabber == null) return;
            if (grabber instanceof AbstractClientPlayer player) {
                Matrix4f mat = BodyPart.getTransformationMatrix(BodyPart.RIGHT_ARM, new Vector3f(0, 0.6f, 0), player, partialTick).normalize3x3();
                this.setPosition(BodyPart.getInWorldPosition(
                        BodyPart.RIGHT_ARM,
                        new Vector3f(0, 0.6f, Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON ? -0.25f : -0.6f),
                        player,
                        partialTick
                ));
                this.setRotation(
                        (float) Math.toDegrees(Math.asin(-mat.get(0, 2))) + this.yRot,
                        (float) Math.toDegrees(Math.atan2(-mat.get(1, 2), mat.get(2, 2))) + this.xRot + 180
                );
            }
        }
    }
}
