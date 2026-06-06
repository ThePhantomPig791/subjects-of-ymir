package net.phantompig.soy.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SubjectsOfYmir;

public class SoyDamageSources {
    public static final ResourceKey<DamageType> SELF_BITE = ResourceKey.create(Registries.DAMAGE_TYPE, SubjectsOfYmir.rsrc("self_bite"));
    public static final ResourceKey<DamageType> SELF_STAB = ResourceKey.create(Registries.DAMAGE_TYPE, SubjectsOfYmir.rsrc("self_stab"));
    public static final ResourceKey<DamageType> SLICE = ResourceKey.create(Registries.DAMAGE_TYPE, SubjectsOfYmir.rsrc("slice"));

    public static DamageSource selfBite(Level level, Entity direct, Entity causing, Vec3 damageSourcePosition) {
        return new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .get(SELF_BITE).get(),
                direct,
                causing,
                damageSourcePosition
        );
    }

    public static DamageSource selfStab(Level level, Entity direct, Entity causing, Vec3 damageSourcePosition) {
        return new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .get(SELF_STAB).get(),
                direct,
                causing,
                damageSourcePosition
        );
    }

    public static DamageSource slice(Level level, Entity attacker) {
        return new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .get(SLICE).get(),
                attacker
        );
    }

    public static void init() {
        SoyDamageTypeTags.init();
    }
}
