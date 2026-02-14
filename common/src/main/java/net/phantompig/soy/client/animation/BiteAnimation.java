package net.phantompig.soy.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.power.ability.BiteAbility;
import net.phantompig.soy.power.ability.BlockAbility;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.Easing;

public class BiteAnimation extends PalladiumAnimation {
    public static final BiteAnimation INSTANCE = new BiteAnimation();

    public BiteAnimation() {
        super(30);
    }

    public float getProgress(LivingEntity entity, float partialTicks) {
        float max = 0;
        var instances = AbilityUtil.getInstances(entity, SoyAbilities.BITE.get());

        if (instances.isEmpty()) {
            return 0F;
        }

        for (AbilityInstance entry : instances) {
            float timer = ((BiteAbility) SoyAbilities.BITE.get()).getAnimationValue(entry, partialTicks);

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
            var armType = player.getMainArm();
            if (armType == HumanoidArm.LEFT) {
                builder.get(PlayerModelPart.RIGHT_ARM)
                        .setXRotDegrees(-94)
                        .setYRotDegrees(-58)
                        .setZRotDegrees(-2)
                        .animate(Easing.INOUTBACK, progress);

                if (firstPersonContext.firstPerson()) {
                    builder.get(PlayerModelPart.RIGHT_ARM)
                            .setZ(30)
                            .scaleX(4)
                            .scaleY(4)
                            .scaleZ(4)
                            .animate(Easing.INOUTBACK, progress);

                    builder.get(PlayerModelPart.LEFT_ARM)
                            .setZRotDegrees(180)
                            .animate(Easing.INOUTBACK, progress);
                }
            } else {
                builder.get(PlayerModelPart.LEFT_ARM)
                        .setXRotDegrees(-77)
                        .setYRotDegrees(54)
                        .setZRotDegrees(24)
                        .animate(Easing.INOUTBACK, progress);

                if (firstPersonContext.firstPerson()) {
                    builder.get(PlayerModelPart.LEFT_ARM)
                            .setZ(40)
                            .scaleX(4)
                            .scaleY(4)
                            .scaleZ(4)
                            .animate(Easing.INOUTBACK, progress);

                    builder.get(PlayerModelPart.RIGHT_ARM)
                            .setZRotDegrees(-180)
                            .animate(Easing.INOUTBACK, progress);
                }
            }
        }
    }
}
