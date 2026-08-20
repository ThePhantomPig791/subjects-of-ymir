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
    public static final RegistrySupplier<ConditionSerializer> CAN_SHIFT_FROM_DAMAGE = CONDITION_SERIALIZERS.register("can_shift_from_damage", CanShiftFromDamageCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> CAN_SHIFT_FROM_STAMINA = CONDITION_SERIALIZERS.register("can_shift_from_stamina", CanShiftFromStaminaCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> GAMEMODE = CONDITION_SERIALIZERS.register("gamemode", GamemodeCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> AT_FULL_HEALTH = CONDITION_SERIALIZERS.register("at_full_health", AtFullHealthCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> STATISTIC = CONDITION_SERIALIZERS.register("statistic", StatisticCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> IS_UNSHIFTING = CONDITION_SERIALIZERS.register("is_unshifting", IsUnshiftingCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> HARDENING = CONDITION_SERIALIZERS.register("hardening", HardeningCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> WEARING_RING = CONDITION_SERIALIZERS.register("wearing_ring", WearingRingCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> CAN_SHIFT_TICKS = CONDITION_SERIALIZERS.register("can_shift_ticks", CanShiftTicksCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> SWINGING_FISTS = CONDITION_SERIALIZERS.register("swinging_fists", SwingingFistsCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> SWINGING_LEGS = CONDITION_SERIALIZERS.register("swinging_legs", SwingingLegsCondition.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> GRABBED_ENTITY_NEARBY = CONDITION_SERIALIZERS.register("grabbed_entity_nearby", GrabbedEntityNearby.Serializer::new);
    public static final RegistrySupplier<ConditionSerializer> IS_LEFT_HANDED = CONDITION_SERIALIZERS.register("is_left_handed", IsLeftHandedCondition.Serializer::new);
}
