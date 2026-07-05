package net.phantompig.soy.stat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;

public class SoyStats {
    public static final DeferredRegister<ResourceLocation> CUSTOM_STATS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.CUSTOM_STAT);

    public static final ResourceLocation TIMES_SHIFTED = register("times_shifted_into_titan");
    public static final ResourceLocation TIME_AS_TITAN = register("time_as_titan");
    public static final ResourceLocation PATH_POINTS_GAINED = register("path_points_gained");
    public static final ResourceLocation AMOUNT_STEAM_HEALED = register("amount_steam_healed");

    private static ResourceLocation register(String name) {
        final ResourceLocation id = SubjectsOfYmir.rsrc(name);
        CUSTOM_STATS.register(name, () -> id);
        return id;
    }

    public static void init() {
        CUSTOM_STATS.register();
    }

    public static void setup() {
        Stats.CUSTOM.get(TIMES_SHIFTED, StatFormatter.DEFAULT);
        Stats.CUSTOM.get(TIME_AS_TITAN, StatFormatter.TIME);
        Stats.CUSTOM.get(PATH_POINTS_GAINED, StatFormatter.DEFAULT);
        Stats.CUSTOM.get(AMOUNT_STEAM_HEALED, StatFormatter.DEFAULT);
    }
}
