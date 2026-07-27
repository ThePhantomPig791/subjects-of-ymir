package net.phantompig.soy.odm.component;

import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.threetag.palladiumcore.registry.CreativeModeTabRegistry;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class OdmComponents {
    public static final LinkedHashMap<String, OdmComponent> ODM_COMPONENTS = new LinkedHashMap<>();
    public static final LinkedHashMap<String, RegistrySupplier<OdmComponentItem>> ODM_ITEMS = new LinkedHashMap<>();

    static {
        register("prototype_turbine", OdmSlot.TURBINE, 500, 0, new StrafeStrengthBuilder().forward(0.1f).up(0.025f).build());
        register("prototype_turbine_v2", OdmSlot.TURBINE, 0, 0, new StrafeStrengthBuilder(0.04f).forward(0.06f).backward(0).up(0.04f).build());
        register("prototype_turbine_v3", OdmSlot.TURBINE, 0, 0, new StrafeStrengthBuilder(0.07f).forward(0.1f).up(0.12f).build());
        register("turbine_v1", OdmSlot.TURBINE, 0, 0, new StrafeStrengthBuilder(0.05f).forward(0.075f).backward(0).up(0.07f).build());
        register("turbine_v2", OdmSlot.TURBINE, 0, 0, new StrafeStrengthBuilder(0.125f).forward(0.1f).backward(0.08f).up(0.11f).build());
        register("blade_sheath", OdmSlot.SHEATH, 0, 8);
        register("blade_sheath_with_canister", OdmSlot.SHEATH, 1500, 6);
        register("canister_sheath", OdmSlot.SHEATH, 3000, 0);
    }

    public static void register(String name, OdmSlot slot, int gasCapacity, int bladeCapacity) {
        ODM_COMPONENTS.put(name, new OdmComponent(name, slot, gasCapacity, bladeCapacity, new HashMap<>()));
    }
    public static void register(String name, OdmSlot slot, int gasCapacity, int bladeCapacity, HashMap<Direction, Float> strafes) {
        ODM_COMPONENTS.put(name, new OdmComponent(name, slot, gasCapacity, bladeCapacity, strafes));
    }

    public static void init(DeferredRegister<Item> itemsRegister) {
        ODM_COMPONENTS.forEach((str, odm) -> {
            var item = itemsRegister.register(str, () -> new OdmComponentItem(new Item.Properties().stacksTo(1), odm));
            ODM_ITEMS.put(str, item);
        });
    }

    public static void initCreativeMenu(CreativeModeTabRegistry.ItemGroupEntries entries) {
        ODM_ITEMS.forEach((str, item) -> {
            entries.add(item.get());
            if (item.get().max > 0) {
                entries.add(item.get().getDefaultInstance());
            }
        });
    }

    public static class StrafeStrengthBuilder {
        private float north, east, south, west, up, down;

        public StrafeStrengthBuilder(float def) {
            north = east = south = west = up = down = def;
        }
        public StrafeStrengthBuilder() {
            this(0);
        }

        public StrafeStrengthBuilder up(float s) {
            up = s;
            return this;
        }
        public StrafeStrengthBuilder down(float s) {
            down = s;
            return this;
        }
        public StrafeStrengthBuilder forward(float s) {
            north = s;
            return this;
        }
        public StrafeStrengthBuilder backward(float s) {
            south = s;
            return this;
        }
        public StrafeStrengthBuilder left(float s) {
            west = s;
            return this;
        }
        public StrafeStrengthBuilder right(float s) {
            east = s;
            return this;
        }

        public HashMap<Direction, Float> build() {
            HashMap<Direction, Float> map = new HashMap<>();
            if (up != 0) map.put(Direction.UP, up);
            if (down != 0) map.put(Direction.DOWN, down);
            if (north != 0) map.put(Direction.NORTH, north);
            if (south != 0) map.put(Direction.SOUTH, south);
            if (east != 0) map.put(Direction.EAST, east);
            if (west != 0) map.put(Direction.WEST, west);
            return map;
        }
    }
}
