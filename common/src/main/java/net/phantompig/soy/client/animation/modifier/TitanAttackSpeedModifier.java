package net.phantompig.soy.client.animation.modifier;

import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import net.minecraft.client.player.AbstractClientPlayer;
import net.phantompig.soy.property.SoyProperties;

public class TitanAttackSpeedModifier extends SpeedModifier {
    public final AbstractClientPlayer player;

    public TitanAttackSpeedModifier(AbstractClientPlayer player) {
        this.player = player;
    }

    @Override
    public void tick() {
        int attackSpeed = SoyProperties.ATTACK_TIME.get(player) + SoyProperties.ATTACK_TIME_INCREASE.get(player);
        this.speed = 20f / attackSpeed;
        super.tick();
    }
}
