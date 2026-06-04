package net.phantompig.soy.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.joml.Vector3f;

public class FlareParticleType extends RollingParticle {
    FlareParticleType(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Vector3f color, float scale, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, 1.0005f, 0.92f, sprites);
        this.friction = 0.99f;
        this.gravity = 0;
        this.lifetime = 3600;
        this.setSpriteFromAge(sprites);
        final int direction = (this.random.nextBoolean() ? 1 : -1);
        this.deltaRoll = (float) (0.01 + this.random.nextFloat() * 0.02) * direction;
        this.roll = 0.02f * direction;
        this.scale(20 * scale);
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 240;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<FlareParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(FlareParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FlareParticleType(level, x, y, z, xSpeed, ySpeed, zSpeed, options.getColor(), options.getScale(), this.sprites);
        }
    }
}
