package net.phantompig.soy.power.ability;

import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyAbilities {
    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Ability.REGISTRY);

    public static final RegistrySupplier<Ability> TITAN_SHIFT = ABILITIES.register("titan_shift", TitanShiftAbility::new);
    public static final RegistrySupplier<Ability> TITAN_UNSHIFT = ABILITIES.register("titan_unshift", TitanUnshiftAbility::new);

    public static void init() {}
}
