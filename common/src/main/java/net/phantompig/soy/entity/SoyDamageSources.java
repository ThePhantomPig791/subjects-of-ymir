package net.phantompig.soy.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.phantompig.soy.SubjectsOfYmir;

public class SoyDamageSources {
    public static final ResourceKey<DamageType> SELF_BITE = ResourceKey.create(Registries.DAMAGE_TYPE, SubjectsOfYmir.rsrc("self_bite"));

    public static DamageSource selfBite(Level level, Entity entity) {
        return new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .get(SELF_BITE).get(),
                entity
        );
    }
}
