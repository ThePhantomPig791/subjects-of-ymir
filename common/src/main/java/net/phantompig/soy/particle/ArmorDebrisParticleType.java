package net.phantompig.soy.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class ArmorDebrisParticleType extends RollingParticle {
    ArmorDebrisParticleType(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, 1f, 0.95f, sprites);
        this.friction = 0.9f;
        this.gravity = 0.8f;
        this.lifetime = 128;
        this.setSprite(sprites.get((int) (4 * Math.random()), 4));
        final int direction = (this.random.nextBoolean() ? 1 : -1);
        this.deltaRoll = (float) (0.05 + this.random.nextFloat() * 0.02) * direction;
        this.roll = 0.03f * direction;
        this.quadSize = 1;
        this.scale(1.5f);
        this.setSpritesOverTime = false;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ArmorDebrisParticleType(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
