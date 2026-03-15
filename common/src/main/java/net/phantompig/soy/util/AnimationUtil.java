package net.phantompig.soy.util;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.phantompig.soy.SubjectsOfYmir;

public class AnimationUtil {
    public static void playTitanAnimation(AbstractClientPlayer player, String id) {
        KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(SubjectsOfYmir.rsrc(id));
        ModifierLayer<IAnimation>  animationContainer = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(SubjectsOfYmir.rsrc("soy_animation"));

        if (anim == null || animationContainer == null) return;

        animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(20, Ease.INOUTSINE),
                new KeyframeAnimationPlayer(anim)
                        .setFirstPersonMode(FirstPersonMode.NONE)
                        .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(true).setShowLeftArm(true))
        );
    }
}
