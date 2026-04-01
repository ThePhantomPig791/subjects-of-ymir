package net.phantompig.soy.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.phantompig.soy.item.SoyItems;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;

public class BazookaAnimation extends PalladiumAnimation {
    public static final BazookaAnimation INSTANCE = new BazookaAnimation();

    public BazookaAnimation() {
        super(10);
    }

    @Override
    public void animate(Builder builder, AbstractClientPlayer player, HumanoidModel<?> model, FirstPersonContext firstPersonContext, float partialTicks) {
        if (firstPersonContext.firstPerson()) return;

        boolean activeMain = player.getItemInHand(InteractionHand.MAIN_HAND).is(SoyItems.BAZOOKA.get());
        boolean activeOff = player.getItemInHand(InteractionHand.OFF_HAND).is(SoyItems.BAZOOKA.get());

        if (activeMain) {
            var armType = player.getMainArm();
            builder.get(armType == HumanoidArm.RIGHT ? PlayerModelPart.RIGHT_ARM : PlayerModelPart.LEFT_ARM)
                    .rotateXDegrees(-75);
        }
        if (activeOff) {
            var armType = player.getMainArm();
            builder.get(armType != HumanoidArm.RIGHT ? PlayerModelPart.RIGHT_ARM : PlayerModelPart.LEFT_ARM)
                    .rotateXDegrees(-75);
        }
    }
}
