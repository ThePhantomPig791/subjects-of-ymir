package net.phantompig.soy.titan;

import com.google.gson.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladium.addonpack.log.AddonPackLog;
import net.threetag.palladiumcore.registry.ReloadListenerRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TitanRegistry extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final TitanRegistry INSTANCE = new TitanRegistry(GSON, "titans");

    private final Map<ResourceLocation, Titan> titans = new HashMap<>();

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, SubjectsOfYmir.rsrc("titans"), TitanRegistry.INSTANCE);
    }

    public TitanRegistry(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        object.forEach((id, json) -> {
            try {
                titans.put(id, Titan.fromJson(id, GsonHelper.convertToJsonObject(json, "$")));
            } catch (Exception exception) {
                AddonPackLog.error("Parsing error loading titan {}", id, exception);
            }
        });
    }

    @Nullable
    public static Titan getTitan(ResourceLocation id) {
        return INSTANCE.titans.get(id);
    }
}
