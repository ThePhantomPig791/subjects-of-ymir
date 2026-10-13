package net.phantompig.soy.fluid;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.phantompig.soy.SubjectsOfYmir;

public class SoyFluidTags {
    public static final TagKey<Fluid> SPINAL_FLUID = create("spinal_fluid");

    private static TagKey<Fluid> create(String name) {
        return TagKey.create(Registries.FLUID, SubjectsOfYmir.rsrc(name));
    }

    public static void init() {}
}
