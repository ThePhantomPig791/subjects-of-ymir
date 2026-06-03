package net.phantompig.soy.particle;

import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class LargeSteamParticleType extends RollingParticle {
    LargeSteamParticleType(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, 1.002f, 0.9f, sprites);
        this.friction = 0.99f;
        this.gravity = -0.4f;
        this.lifetime = 36;
        this.setSpriteFromAge(sprites);
        final int direction = (this.random.nextBoolean() ? 1 : -1);
        this.deltaRoll = (float) (0.01 + this.random.nextFloat() * 0.01) * direction;
        this.roll = 0.02f * direction;
        this.quadSize = 0.2f;
        this.scale(10f);
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LargeSteamParticleType(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
