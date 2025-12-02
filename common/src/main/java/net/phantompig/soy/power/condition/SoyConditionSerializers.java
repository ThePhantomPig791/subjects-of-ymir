package net.phantompig.soy.power.condition;

import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyConditionSerializers {
    public static final DeferredRegister<ConditionSerializer> CONDITION_SERIALIZERS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, ConditionSerializer.REGISTRY);

    public static final RegistrySupplier<ConditionSerializer> SHIFT_PROGRESS = CONDITION_SERIALIZERS.register("shift_progress", ShiftProgressCondition.Serializer::new);
}
