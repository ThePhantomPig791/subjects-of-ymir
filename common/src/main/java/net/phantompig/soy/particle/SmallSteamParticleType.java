package net.phantompig.soy.particle;

import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SmallSteamParticleType extends TextureSheetParticle {
    private final SpriteSet sprites;
    private float deltaRoll;

    SmallSteamParticleType(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.hasPhysics = true;
        this.friction = 0.8f;
        this.gravity = -0.6f;
        this.lifetime = 24;
        this.setSpriteFromAge(sprites);
        this.rCol = this.gCol = this.bCol = 1;
        this.deltaRoll = (float) (0.2 + this.random.nextFloat() * 0.1) * (this.random.nextBoolean() ? 1 : -1);
        this.roll = 0.5f;
        this.quadSize = 0.2f;
        this.scale(0.25f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSpriteFromAge(sprites);

            float t = (float) this.age / this.lifetime;
            this.scale(1.05f);
            this.setAlpha(1 - t);
            this.oRoll = this.roll;
            this.roll += deltaRoll;
            deltaRoll *= 0.95f;
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

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SmallSteamParticleType(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
