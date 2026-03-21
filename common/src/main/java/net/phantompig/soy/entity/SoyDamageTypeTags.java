package net.phantompig.soy.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.phantompig.soy.SubjectsOfYmir;

public class SoyDamageTypeTags {
    public static final TagKey<DamageType> CANNOT_CAUSE_SHIFT = create("cannot_cause_shift");

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, SubjectsOfYmir.rsrc(name));
    }

    public static void init() {}
}
