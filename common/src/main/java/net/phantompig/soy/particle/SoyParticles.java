package net.phantompig.soy.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<ParticleType<SimpleParticleType>> DIRT_CLOUD = PARTICLE_TYPES.register("dirt_cloud", () -> new SimpleParticleType(false));

    public static void init() {
        PARTICLE_TYPES.register();
    }
}
