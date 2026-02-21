package net.phantompig.soy.particle;

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

    public static void init() {
        PARTICLE_TYPES.register();
    }
}
