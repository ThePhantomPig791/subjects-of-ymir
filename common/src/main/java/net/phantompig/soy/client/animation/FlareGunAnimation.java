package net.phantompig.soy.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.item.FlareGunItem;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;

public class FlareGunAnimation extends PalladiumAnimation {
    public static final FlareGunAnimation INSTANCE = new FlareGunAnimation();
    public FlareGunAnimation() {
        super(50);
    }

    @Override
    public void animate(Builder builder, AbstractClientPlayer player, HumanoidModel<?> model, FirstPersonContext firstPersonContext, float partialTicks) {
        ItemStack stack = player.getUseItem();
        if (stack.getItem() instanceof FlareGunItem) {
            boolean rightHand = player.getMainArm() == HumanoidArm.RIGHT ^ player.getUsedItemHand() == InteractionHand.OFF_HAND;
            if (player.isCrouching()) {
                builder.get(rightHand ? PlayerModelPart.RIGHT_ARM : PlayerModelPart.LEFT_ARM)
                        .setXRotShortest((float)((double)model.head.xRot - 1.5707963267948966)).setYRotShortest(model.head.yRot).setZRotShortest(model.head.zRot);
                builder.get(rightHand ? PlayerModelPart.LEFT_ARM : PlayerModelPart.RIGHT_ARM)
                        .setYRotDegrees(180)
                        .setZRotDegrees(180)
                        .moveY(2);
            } else {
                builder.get(rightHand ? PlayerModelPart.RIGHT_ARM : PlayerModelPart.LEFT_ARM)
                        .setXRotDegrees(10 * (rightHand ? 1 : -1))
                        .setYRotDegrees(180)
                        .setZRotDegrees(180)
                        .moveY(-4);
                builder.get(rightHand ? PlayerModelPart.LEFT_ARM : PlayerModelPart.RIGHT_ARM)
                        .setYRotDegrees(180)
                        .setZRotDegrees(180)
                        .moveY(2);
            }
        }
    }
}
