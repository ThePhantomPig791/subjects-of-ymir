package net.phantompig.soy.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<ParticleType<SimpleParticleType>> SMALL_STEAM = PARTICLE_TYPES.register("small_steam", () -> new SimpleParticleType(false));
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> LARGE_STEAM = PARTICLE_TYPES.register("large_steam", () -> new SimpleParticleType(false));
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> EMBER = PARTICLE_TYPES.register("ember", () -> new SimpleParticleType(false));
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> DIRT_CLOUD = PARTICLE_TYPES.register("dirt_cloud", () -> new SimpleParticleType(false));
    public static final RegistrySupplier<ParticleType<FlareParticleOptions>> FLARE = PARTICLE_TYPES.register("flare", () -> new ParticleType<FlareParticleOptions>(true, FlareParticleOptions.DESERIALIZER) {
        @Override
        public Codec<FlareParticleOptions> codec() {
            return FlareParticleOptions.CODEC;
        }
    });
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> TRANSFORM_SPARK = PARTICLE_TYPES.register("transform_spark", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> TRANSFORM_ARC = PARTICLE_TYPES.register("transform_arc", () -> new SimpleParticleType(false));
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> TRANSFORM_ZAP = PARTICLE_TYPES.register("transform_zap", () -> new SimpleParticleType(true));
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> TRANSFORM_ZOP = PARTICLE_TYPES.register("transform_zop", () -> new SimpleParticleType(false));
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> TRANSFORM_CRACK = PARTICLE_TYPES.register("transform_crack", () -> new SimpleParticleType(false));
    public static final RegistrySupplier<ParticleType<SimpleParticleType>> TRANSFORM_RAY = PARTICLE_TYPES.register("transform_ray", () -> new SimpleParticleType(true));

    public static void init() {
        PARTICLE_TYPES.register();
    }
}
