package net.phantompig.soy.titan;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Tuple;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.saveddata.SavedData;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.util.ListUtil;
import net.threetag.palladium.addonpack.log.AddonPackLog;
import net.threetag.palladiumcore.registry.ReloadListenerRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TitanRegistry extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final TitanRegistry INSTANCE = new TitanRegistry(GSON, "titans");

    private final Map<ResourceLocation, Titan> titans = new HashMap<>();
    private final Map<ResourceLocation, Double> titanWeights = new HashMap<>();

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, SubjectsOfYmir.rsrc("titans"), TitanRegistry.INSTANCE);
    }

    public TitanRegistry(Gson gson, String directory) {
        super(gson, directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        titans.clear();
        titanWeights.clear();
        object.forEach((id, json) -> {
            try {
                register(id, Titan.fromJson(id, json.getAsJsonObject()));
            } catch (Exception exception) {
                AddonPackLog.error("Parsing error loading titan {}", id, exception);
            }
        });
    }

    public void register(ResourceLocation id, Titan titan) {
        titans.put(id, titan);
        titanWeights.put(id, titan.weight);
    }

    @Nullable
    public static Titan getTitan(ResourceLocation id) {
        return INSTANCE.titans.get(id);
    }

    public static boolean titanExists(ResourceLocation id) {
        return INSTANCE.titans.containsKey(id);
    }

    public static int indexOf(ResourceLocation titan) {
        return new ArrayList<>(INSTANCE.titans.keySet()).indexOf(titan);
    }

    public static ImmutableMap<ResourceLocation, Titan> getTitans() {
        return ImmutableMap.copyOf(INSTANCE.titans);
    }

    public static Tuple<Titan, String> getRandomTitan() {
        Tuple<Titan, String> tuple = new Tuple<>(ListUtil.getRandom(TitanRegistry.getTitans().values().asList()), null);
        tuple.setB(ListUtil.getRandom(tuple.getA().variants));
        return tuple;
    }

    // when this method is called, it can't return a titan that has already been returned, with the list keeping track of that resetting when there are no more titans to give. i.e. it ensures that players get unique titans, unless we run out of titans to give in which case it starts over
    public static Tuple<Titan, String> getSequentialRandomTitan(MinecraftServer server) {
        ArrayList<ResourceLocation> available = new ArrayList<>(getTitans().keySet().asList());
        var data = SequentialTitanSavedData.get(server);
        List<ResourceLocation> used = data.used;
        if (used.size() == available.size()) {
            data.clear();
        } else {
            available.removeIf(used::contains);
        }

        List<Tuple<ResourceLocation, Double>> weighted = available.stream().map(rsrc -> new Tuple<>(rsrc, INSTANCE.titanWeights.get(rsrc))).toList();
        ResourceLocation titanRsrc = ListUtil.getRandomWeighted(weighted);
        Titan titan = TitanRegistry.getTitan(titanRsrc);
        data.use(titanRsrc);
        return new Tuple<>(titan, ListUtil.getRandom(titan.variants));
    }


    public static class SequentialTitanSavedData extends SavedData {
        public final List<ResourceLocation> used;

        public SequentialTitanSavedData() {
            used = new ArrayList<>();
        }

        public SequentialTitanSavedData(List<ResourceLocation> used) {
            this.used = used;
        }

        public void use(ResourceLocation loc) {
            used.add(loc);
            setDirty();
        }

        public void clear() {
            used.clear();
            setDirty();
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            ListTag list = new ListTag();
            for (ResourceLocation titan : used) {
                list.add(StringTag.valueOf(titan.toString()));
            }
            tag.put("UsedTitans", list);
            return tag;
        }

        public static SequentialTitanSavedData load(CompoundTag tag) {
            List<ResourceLocation> usedList = new ArrayList<>();
            ListTag nbtList = tag.getList("UsedTitans", CompoundTag.TAG_STRING);
            for (int i = 0; i < nbtList.size(); i++) {
                usedList.add(new ResourceLocation(nbtList.getString(i)));
            }
            return new SequentialTitanSavedData(usedList);
        }

        public static SequentialTitanSavedData get(MinecraftServer server) {
            ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);
            if (level == null) {
                return new SequentialTitanSavedData();
            }
            return level.getDataStorage().computeIfAbsent(SequentialTitanSavedData::load, SequentialTitanSavedData::new, "sequential_titan_data");
        }
    }
}
