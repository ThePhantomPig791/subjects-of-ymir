package net.phantompig.soy.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public abstract class RollingParticle extends TextureSheetParticle {
    public final SpriteSet sprites;
    public final float scaleDelta, rollFriction;
    public float deltaRoll;
    public boolean fadeOut = true;

    RollingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float scaleDelta, float rollFriction, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.hasPhysics = true;
        this.scaleDelta = scaleDelta;
        this.rollFriction = rollFriction;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSpriteFromAge(sprites);

            float t = (float) this.age / this.lifetime;
            this.scale(scaleDelta);
            if (this.fadeOut) this.setAlpha(1 - t);
            this.oRoll = this.roll;
            this.roll += deltaRoll;
            deltaRoll *= rollFriction;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        super.render(buffer, renderInfo, partialTicks);
    }
}
