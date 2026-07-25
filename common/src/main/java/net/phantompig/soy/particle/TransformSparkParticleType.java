package net.phantompig.soy.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class TransformSparkParticleType extends RollingParticle {
    TransformSparkParticleType(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, 1.005f, 0.85f, sprites);
        this.friction = 0.94f;
        this.gravity = -0.1f;
        this.lifetime = 10;
        this.fadeOut = false;
        this.setSpriteFromAge(sprites);
        final int direction = (this.random.nextBoolean() ? 1 : -1);
        this.deltaRoll = (float) (0.01 + this.random.nextFloat() * 0.05) * direction;
        this.roll = 0.025f * direction;
        this.scale(4 + (float) (3 * Math.random()));
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new TransformSparkParticleType(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
