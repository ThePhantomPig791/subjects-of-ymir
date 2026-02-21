package net.phantompig.soy.stat;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.phantompig.soy.SubjectsOfYmir;

public class SoyStats {
    public static final ResourceLocation TIMES_SHIFTED = register("times_shifted_into_titan");
    public static final ResourceLocation TIME_AS_TITAN = register("time_as_titan", StatFormatter.TIME);

    private static ResourceLocation register(String name) {
        return register(name, StatFormatter.DEFAULT);
    }
    private static ResourceLocation register(String name, StatFormatter formatter) {
        var id = SubjectsOfYmir.rsrc(name);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, name, id); // sorry to all the DeferredRegister lovers out there
        Stats.CUSTOM.get(id, formatter);
        return id;
    }

    public static void init() {}
}
