package net.phantompig.soy.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.power.ability.BlockAbility;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.Easing;

public class NapeCoverAnimation extends PalladiumAnimation {
    public static final NapeCoverAnimation INSTANCE = new NapeCoverAnimation();

    public NapeCoverAnimation() {
        super(20);
    }

    public float getProgress(LivingEntity entity, float partialTicks) {
        float max = 0;
        var instances = AbilityUtil.getInstances(entity, SoyAbilities.NAPE_COVER.get());

        if (instances.isEmpty()) {
            return 0F;
        }

        for (AbilityInstance entry : instances) {
            float timer = ((BlockAbility) SoyAbilities.NAPE_COVER.get()).getAnimationValue(entry, partialTicks);

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
            if (firstPersonContext.firstPerson()) {
                builder.get(PlayerModelPart.LEFT_ARM)
                        .setZ(10)
                        .scaleX(2)
                        .scaleY(2)
                        .scaleZ(2)
                        .rotateXDegrees(-45)
                        .rotateYDegrees(-180)
                        .rotateZDegrees(70)
                        .animate(Easing.INOUTBACK, progress);
            } else {
                builder.get(PlayerModelPart.LEFT_ARM)
                        .setXRotDegrees(70)
                        .setYRotDegrees(-60)
                        .setZRotDegrees(15)
                        .animate(Easing.INOUTBACK, progress);
            }
        }
    }
}
