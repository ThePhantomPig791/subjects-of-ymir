package net.phantompig.soy.odm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.odm.physics.OdmNode;

import java.util.*;

public class OdmServerLevel {
    public static final Vec3 GRAVITY = new Vec3(0, -0.1, 0);

    public ServerLevel level;

    private OdmSavedData odmData;

    private final HashSet<UUID> forRemoval = new HashSet<>(2);

    public OdmServerLevel(ServerLevel level) {
        this.level = level;
        this.odmData = getData();
    }

    public void tick() {
        this.getData().nodes.values().forEach(OdmNode::tick);
        forRemoval.forEach(this.odmData::removeNode);
        forRemoval.clear();
    }

    public void addNode(OdmNode hook) {
        this.getData().addNode(hook);
        hook.onAdd();
    }
    public OdmNode getNode(UUID hook) {
        return this.getData().nodes.get(hook);
    }
    public void removeNode(UUID uuid) {
        this.forRemoval.add(uuid);
    }
    public void clearNodes() {
        this.forRemoval.addAll(this.odmData.nodes.keySet());
    }

    public OdmSavedData getData() {
        if (odmData == null) {
            this.odmData = OdmSavedData.get(this);
            this.odmData.odmLevel = this;
            this.odmData.load(); // weird workaround but who cares
            return this.odmData;
        }
        return this.odmData;
    }

    public static class OdmSavedData extends SavedData {
        private final HashMap<UUID, OdmNode> nodes;
        private OdmServerLevel odmLevel;

        private CompoundTag nbt;

        public OdmSavedData(HashMap<UUID, OdmNode> nodes, OdmServerLevel odmLevel, CompoundTag nbt) {
            this.nodes = nodes;
            this.odmLevel = odmLevel;
            this.nbt = nbt;
        }
        public OdmSavedData(HashMap<UUID, OdmNode> nodes, OdmServerLevel odmLevel) {
            this(nodes, odmLevel, new CompoundTag());
        }
        public OdmSavedData(OdmServerLevel odmLevel) {
            this(new HashMap<>(), odmLevel);
        }
        public OdmSavedData() {
            this(new HashMap<>(), null);
        }
        public OdmSavedData(CompoundTag tag) {
            this();
            this.nbt = tag;
        }

        public void addNode(OdmNode node) {
            nodes.put(node.uuid, node);
            setDirty();
        }

        public void removeNode(UUID uuid) {
            OdmNode node = nodes.remove(uuid);
            if (node != null) {
                node.onRemove();
                setDirty();
            }
        }

        public void clearNodes() {
            nodes.clear();
            setDirty();
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            CompoundTag hooksTag = new CompoundTag();
            this.nodes.forEach((uuid, node) -> {
                hooksTag.put(uuid.toString(), node.toTag());
            });
            tag.put("Nodes", hooksTag);
            return tag;
        }

        private void load() {
            CompoundTag nodesTag = this.nbt.getCompound("Nodes");
            Set<String> keys = nodesTag.getAllKeys();
            keys.iterator().forEachRemaining(string -> {
                nodes.put(UUID.fromString(string), OdmNode.fromTag(this.odmLevel, nodesTag.getCompound(string)));
            });
        }

        public static OdmSavedData load(CompoundTag tag) {
            return new OdmSavedData(tag);
        }

        public static OdmSavedData get(OdmServerLevel level) {
            if (level == null) {
                return new OdmSavedData();
            }
            return level.level.getDataStorage().computeIfAbsent(OdmSavedData::load, OdmSavedData::new, "odm_data");
        }
    }
}
