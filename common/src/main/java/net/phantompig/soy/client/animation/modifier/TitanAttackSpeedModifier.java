package net.phantompig.soy.client.animation.modifier;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.minecraft.client.player.AbstractClientPlayer;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.property.SoyProperties;
import org.jetbrains.annotations.NotNull;

public class TitanAttackSpeedModifier extends AbstractModifier {
    public final AbstractClientPlayer player;

    private float speed;
    private float delta, shiftedDelta;

    public TitanAttackSpeedModifier(AbstractClientPlayer player) {
        this.player = player;
    }

    @Override
    public void tick() {
        super.tick();
        int attackSpeed = SoyProperties.ATTACK_SPEED.get(player);
        speed = 20f / attackSpeed;
        SubjectsOfYmir.LOGGER.info("atk speed: {}, s: {}", attackSpeed, speed);
        float delta = 1f - this.delta;
        this.delta = 0;
        step(delta);
    }

    @Override
    public void setupAnim(float tickDelta) {
        float delta = tickDelta - this.delta;
        this.delta = tickDelta;
        step(delta);
    }

    protected void step(float delta) {
        delta *= speed;
        delta += shiftedDelta;
        while (delta > 1) {
            delta -= 1;
            super.tick();
        }
        super.setupAnim(delta);
        this.shiftedDelta = delta;
    }


    @Override
    public @NotNull Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        return super.get3DTransform(modelName, type, shiftedDelta, value0);
    }
}
