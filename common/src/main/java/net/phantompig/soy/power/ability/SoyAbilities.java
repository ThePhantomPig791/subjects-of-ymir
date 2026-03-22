package net.phantompig.soy.power.ability;

import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoyAbilities {
    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Ability.REGISTRY);

    public static final RegistrySupplier<Ability> TITAN_SHIFT = ABILITIES.register("titan_shift", TitanShiftAbility::new);
    public static final RegistrySupplier<Ability> TITAN_UNSHIFT = ABILITIES.register("titan_unshift", TitanUnshiftAbility::new);
    public static final RegistrySupplier<Ability> BLOCK = ABILITIES.register("block", BlockAbility::new);
    public static final RegistrySupplier<Ability> BITE = ABILITIES.register("bite", BiteAbility::new);
    public static final RegistrySupplier<Ability> DAGGER = ABILITIES.register("dagger", DaggerAbility::new);
    public static final RegistrySupplier<Ability> REPEAT_SOUND = ABILITIES.register("repeat_sound", RepeatSoundAbility::new);
    public static final RegistrySupplier<Ability> STAT_HEALING = ABILITIES.register("stat_healing", StatHealingAbility::new);
    public static final RegistrySupplier<Ability> HARDENING_ALL = ABILITIES.register("hardening_all", HardeningAllAbility::new);
    public static final RegistrySupplier<Ability> HARDENING_KNUCKLES = ABILITIES.register("hardening_knuckles", HardeningKnucklesAbility::new);
    public static final RegistrySupplier<Ability> HARDENING_HANDS = ABILITIES.register("hardening_hands", HardeningHandsAbility::new);
    public static final RegistrySupplier<Ability> BERSERK = ABILITIES.register("berserk", BerserkAbility::new);
}
