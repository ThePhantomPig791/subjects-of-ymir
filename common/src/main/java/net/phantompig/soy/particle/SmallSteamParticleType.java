package net.phantompig.soy.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SmallSteamParticleType extends RollingParticle {
    SmallSteamParticleType(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, 1.02f, 0.8f, sprites);
        this.friction = 0.8f;
        this.gravity = -0.6f;
        this.lifetime = 24;
        this.setSpriteFromAge(sprites);
        final int direction = (this.random.nextBoolean() ? 1 : -1);
        this.deltaRoll = (float) (0.05 + this.random.nextFloat() * 0.02) * direction;
        this.roll = 0.03f * direction;
        this.quadSize = 0.2f;
        this.scale(1.5f);
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
