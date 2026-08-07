package net.phantompig.soy.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.util.ShapeUtil;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;
import net.threetag.palladium.util.Easing;

public class OdmAnimation extends PalladiumAnimation {
    private static final Vec3 RIGHT_ARM_HOLD = new Vec3(-140, -20, 20);
    private static final Vec3 LEFT_ARM_HOLD = new Vec3(-160, 5, 10);
    private static final Vec3 RIGHT_ARM_AIR = new Vec3(10, 15, 45);
    private static final Vec3 LEFT_ARM_AIR = new Vec3(10, -15, -45);
    private static final Vec3 BODY_FALL = new Vec3(-75, 0, 0);
    private static final Vec3 BODY_TILT = new Vec3(0, 0, 45);

    public static final OdmAnimation INSTANCE = new OdmAnimation();
    public OdmAnimation() {
        super(0);
    }

    @Override
    public void animate(Builder builder, AbstractClientPlayer player, HumanoidModel<?> model, FirstPersonContext firstPersonContext, float partialTicks) {
        if (firstPersonContext.firstPerson()) return;
        if (player.isSpectator()) return;
        if (!SoyConfig.Client.shouldAnimateOdm()) return;
        if (player.isPassenger()) return;

        Vec3 rightArm = Vec3.ZERO;
        Vec3 leftArm = Vec3.ZERO;
        Vec3 body = Vec3.ZERO;

        double spd = player.getDeltaMovementLerped(partialTicks).lengthSqr();
        if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof OdmAttachableAddonArmorItem) {
            if (!player.isVisuallySwimming()) {
                Vec3 vert = player.getDeltaMovementLerped(partialTicks).multiply(0, 1, 0);
                if (Math.abs(vert.y) > 0.08) {
                    float progress = (float) Math.cbrt(vert.lengthSqr());
                    progress = 3 * progress / (progress + 5);
                    if (progress > 0.1f) {
                        body = body.lerp(BODY_FALL.scale(vert.y > 0 ? -0.5f : 1), Easing.INCIRC.apply(progress));
                    }
                }

                double tilt = player.getDeltaMovement().dot(player.getLookAngle().cross(ShapeUtil.UP));
                if (Math.abs(tilt) > 0.11) {
                    boolean tiltLeft = tilt < 0;
                    tilt = Math.abs(tilt);
                    tilt = 0.75 * tilt / (tilt + 1);
                    body = body.lerp(BODY_TILT.scale(tiltLeft ? -1 : 1), Easing.INCIRC.apply((float) tilt));
                }


                if (!player.onGround()) {
                    float progress = (float) Math.cbrt(spd);
                    progress = 1.5f * progress / (progress + 0.5f);
                    if (progress > 0f) {
                        rightArm = rightArm.lerp(RIGHT_ARM_AIR, Easing.INCUBIC.apply(progress));
                        leftArm = leftArm.lerp(LEFT_ARM_AIR, Easing.INCUBIC.apply(progress));
                    }
                }
            }
        }

        int rawProgress = SoyProperties.ODM_HOLD_ATTACK.get(player);
        boolean increasing = SoyProperties.ODM_HOLD_ATTACK_INCREASING.get(player);
        if (rawProgress > 0 || increasing) {
            float progress = SoyProperties.ODM_HOLD_ATTACK.get(player) + partialTicks * (increasing ? 1 : -1);
            progress /= 5;
            progress = Math.min(progress, 1);
            if (progress > 0) {
                rightArm = rightArm.lerp(RIGHT_ARM_HOLD, Easing.INEXPO.apply(progress));
                leftArm = leftArm.lerp(LEFT_ARM_HOLD, Easing.INEXPO.apply(progress));
            }
        }

        spd = Math.cbrt(spd);
        if (!body.equals(Vec3.ZERO)) {
            builder.get(PlayerModelPart.BODY)
                    .setXRotDegrees((float) body.x)
                    .setYRotDegrees((float) body.y + 2 * (float) spd * (float) Math.sin(builder.getAgeInTicks()))
                    .setZRotDegrees((float) body.z);
        }
        if (!rightArm.equals(Vec3.ZERO)) {
            rightArm = rightArm.add(2 * spd * Math.sin(builder.getAgeInTicks()), spd * Math.sin(builder.getAgeInTicks() - 10), 0);
            builder.get(PlayerModelPart.RIGHT_ARM)
                    .setXRotDegrees((float) rightArm.x)
                    .setYRotDegrees((float) rightArm.y)
                    .setZRotDegrees((float) rightArm.z);
            if (aboveGround(player) && !player.isVisuallySwimming()) builder.get(PlayerModelPart.LEFT_LEG)
                    .setXRotDegrees((float) rightArm.x * -0.1f + (float) Math.sin(builder.getAgeInTicks()))
                    .setYRotDegrees((float) rightArm.y * 0.1f)
                    .setZRotDegrees((float) rightArm.z * -0.1f);
        }
        if (!leftArm.equals(Vec3.ZERO)) {
            leftArm = leftArm.add(2 * spd * Math.cos(builder.getAgeInTicks()), spd * Math.cos(builder.getAgeInTicks() - 10), 0);
            builder.get(PlayerModelPart.LEFT_ARM)
                    .setXRotDegrees((float) leftArm.x)
                    .setYRotDegrees((float) leftArm.y)
                    .setZRotDegrees((float) leftArm.z);
            if (aboveGround(player) && !player.isVisuallySwimming()) builder.get(PlayerModelPart.RIGHT_LEG)
                    .setXRotDegrees((float) leftArm.x * -0.1f + (float) Math.cos(builder.getAgeInTicks()))
                    .setYRotDegrees((float) leftArm.y * 0.1f)
                    .setZRotDegrees((float) leftArm.z * -0.1f);
        }
    }

    private static boolean aboveGround(AbstractClientPlayer player) {
        return !player.onGround() && !player.level().getBlockState(player.blockPosition().below(2)).blocksMotion() && !player.level().getBlockState(player.blockPosition().below(3)).blocksMotion();
    }
}
