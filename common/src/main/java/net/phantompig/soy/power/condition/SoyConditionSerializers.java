package net.phantompig.soy.power.condition;

import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyConditionSerializers {
    public static final DeferredRegister<ConditionSerializer> CONDITION_SERIALIZERS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, ConditionSerializer.REGISTRY);

    public static final RegistrySupplier<ConditionSerializer> SHIFT_PROGRESS = CONDITION_SERIALIZERS.register("shift_progress", ShiftProgressCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> IS_SHIFTING = CONDITION_SERIALIZERS.register("is_shifting", IsShiftingCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> IS_CORPSE = CONDITION_SERIALIZERS.register("is_corpse", IsCorpseCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> CAN_SHIFT = CONDITION_SERIALIZERS.register("can_shift", CanShiftCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> GAMEMODE = CONDITION_SERIALIZERS.register("gamemode", GamemodeCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> AT_FULL_HEALTH = CONDITION_SERIALIZERS.register("at_full_health", AtFullHealthCondition.Serializer::new);
}
