package net.phantompig.soy.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.item.SoyItemTags;
import net.phantompig.soy.power.ability.BiteAbility;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.Easing;

public class DaggerAnimation extends PalladiumAnimation {
    public static final DaggerAnimation INSTANCE = new DaggerAnimation();

    public DaggerAnimation() {
        super(30);
    }

    public float getProgress(LivingEntity entity, float partialTicks) {
        float max = 0;
        var instances = AbilityUtil.getInstances(entity, SoyAbilities.DAGGER.get());

        if (instances.isEmpty()) {
            return 0F;
        }

        for (AbilityInstance entry : instances) {
            float timer = ((BiteAbility) SoyAbilities.DAGGER.get()).getAnimationValue(entry, partialTicks);

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
            var armType = player.getItemInHand(InteractionHand.MAIN_HAND).is(SoyItemTags.CAN_BE_USED_TO_SELF_STAB) ? player.getMainArm() : player.getMainArm().getOpposite();
            if (armType == HumanoidArm.LEFT) {
                if (!firstPersonContext.firstPerson()) {
                    builder.get(PlayerModelPart.LEFT_ARM)
                            .setXRotDegrees(-85)
                            .setYRotDegrees(65)
                            .setZRotDegrees(-60)
                            .animate(Easing.INCUBIC, progress);
                    builder.get(PlayerModelPart.RIGHT_ARM)
                            .setXRotDegrees(-30)
                            .animate(Easing.INCUBIC, progress);
                }
            } else {
                if (!firstPersonContext.firstPerson()) {
                    builder.get(PlayerModelPart.RIGHT_ARM)
                            .setXRotDegrees(-85)
                            .setYRotDegrees(-65)
                            .setZRotDegrees(60)
                            .animate(Easing.INCUBIC, progress);
                    builder.get(PlayerModelPart.LEFT_ARM)
                            .setXRotDegrees(-30)
                            .animate(Easing.INCUBIC, progress);
                }
            }
        }
    }
}
