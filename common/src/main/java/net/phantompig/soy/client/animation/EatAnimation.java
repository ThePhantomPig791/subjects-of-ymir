package net.phantompig.soy.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.power.ability.*;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.Easing;

public class EatAnimation extends PalladiumAnimation {
    public static final EatAnimation INSTANCE = new EatAnimation();

    public EatAnimation() {
        super(20);
    }

    public float getProgress(LivingEntity entity, float partialTicks) {
        float max = 0;
        var instances = AbilityUtil.getInstances(entity, SoyAbilities.EAT.get());

        if (instances.isEmpty()) {
            return 0F;
        }

        for (AbilityInstance entry : instances) {
            float timer = Mth.lerp(partialTicks, entry.getProperty(EatAbility.TIMER_PREV), entry.getProperty(EatAbility.TIMER));
            timer /= entry.getProperty(EatAbility.TIME);

            if (timer > max) {
                max = timer;
            }
        }

        return Mth.clamp(max, 0F, 1F);
    }

    @Override
    public void animate(Builder builder, AbstractClientPlayer player, HumanoidModel<?> model, FirstPersonContext firstPersonContext, float partialTicks) {
        var progress = this.getProgress(player, partialTicks);

        if (progress > 0F) {
            float eatProgress = Easing.OUTSINE.apply(EatAbility.getEatProgress(player, partialTicks));
            if (firstPersonContext.firstPerson()) {
                builder.get(PlayerModelPart.RIGHT_ARM)
                        .setX(-12)
                        .rotateXDegrees(-30 - 60 * eatProgress)
                        .rotateYDegrees(-30)
                        .rotateZDegrees(-80)
                        .animate(Easing.INOUTBACK, progress);
            } else {
                float headRot = 0.5f * (float) Math.toDegrees(model.head.yRot) + 20;
                builder.get(PlayerModelPart.RIGHT_ARM)
                        .setXRotDegrees(-90 - 40 * eatProgress)
                        .setYRotDegrees(-45 - 30 * eatProgress + headRot)
                        .setZRotDegrees(25)
                        .animate(Easing.INOUTBACK, progress);
                builder.get(PlayerModelPart.HEAD)
                        .setYRot(0.5f * model.head.yRot - 0.3f)
                        .animate(Easing.INOUTBACK, progress);
            }
        }
    }
}
