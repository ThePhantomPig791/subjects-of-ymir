package net.phantompig.soy.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.EntityAttributeRegistry;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.ENTITY_TYPE);

    public static RegistrySupplier<EntityType<TitanCorpseEntity>> TITAN_CORPSE = ENTITY_TYPES.register(
            "titan_corpse",
            () -> EntityType.Builder.of(TitanCorpseEntity::new, MobCategory.MISC).sized(0.6f, 1.8f).build("titan_corpse")
    );

    public static RegistrySupplier<EntityType<OdmNodeEntity>> ODM_NODE = ENTITY_TYPES.register(
            "odm_node",
            () -> EntityType.Builder.of(OdmNodeEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).updateInterval(1).clientTrackingRange(64).build("odm_node")
    );

    public static RegistrySupplier<EntityType<FlareEntity>> FLARE = ENTITY_TYPES.register(
            "flare",
            () -> EntityType.Builder.of(FlareEntity::new, MobCategory.MISC).sized(1, 1).updateInterval(10).clientTrackingRange(512).build("flare")
    );

    public static void init() {
        ENTITY_TYPES.register();

        EntityAttributeRegistry.register(TITAN_CORPSE, TitanCorpseEntity::createLivingAttributes);
    }
}
