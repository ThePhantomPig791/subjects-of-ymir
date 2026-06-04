package net.phantompig.soy.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class FlareParticleOptions extends DustParticleOptionsBase {
    // ripped from dustparticleoptions. there's probably a better way to do this
    public static Codec<FlareParticleOptions> CODEC = RecordCodecBuilder.create((instance) -> instance.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((dustParticleOptions) -> dustParticleOptions.color), Codec.FLOAT.fieldOf("scale").forGetter((dustParticleOptions) -> dustParticleOptions.scale)).apply(instance, FlareParticleOptions::new));
    public static ParticleOptions.Deserializer<FlareParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<FlareParticleOptions>() {
        public FlareParticleOptions fromCommand(ParticleType<FlareParticleOptions> particleType, StringReader
        stringReader) throws CommandSyntaxException {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(stringReader);
            stringReader.expect(' ');
            float f = stringReader.readFloat();
            return new FlareParticleOptions(vector3f, f);
        }

        public FlareParticleOptions fromNetwork(ParticleType<FlareParticleOptions> particleType, FriendlyByteBuf
        friendlyByteBuf) {
            return new FlareParticleOptions(DustParticleOptionsBase.readVector3f(friendlyByteBuf), friendlyByteBuf.readFloat());
        }
    };

    public FlareParticleOptions(Vector3f vector3f, float f) {
        super(vector3f, f);
    }

    @Override
    public ParticleType<FlareParticleOptions> getType() {
        return SoyParticles.FLARE.get();
    }
}
