package net.phantompig.soy.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class DirtCloudParticleType extends RollingParticle {
    DirtCloudParticleType(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, 1.005f, 0.9f, sprites);
        this.friction = 0.7f;
        this.gravity = -0.8f;
        this.lifetime = 120;
        this.setSpriteFromAge(sprites);
        final int direction = (this.random.nextBoolean() ? 1 : -1);
        this.deltaRoll = (float) (0.01 + this.random.nextFloat() * 0.05) * direction;
        this.roll = 0.02f * direction;
        this.scale(12f);
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DirtCloudParticleType(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
