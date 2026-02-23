package net.phantompig.soy.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoySounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> SHIFT = make("shift", 128);
    public static final RegistrySupplier<SoundEvent> SHIFT_LOCAL = make("shift_local", 16);
    public static final RegistrySupplier<SoundEvent> ELECTRICITY = make("electricity", 16);
    public static final RegistrySupplier<SoundEvent> ELECTRICITY_FINAL = make("electricity_final", 16);
    public static final RegistrySupplier<SoundEvent> STEAM = make("steam", 32);
    public static final RegistrySupplier<SoundEvent> GAS_BURST = make("gas_burst", 16);

    public static RegistrySupplier<SoundEvent> make(String name, float range) {
        return SOUNDS.register(name, () -> new SoundEvent(SubjectsOfYmir.rsrc(name), range, false));
    }

    public static void init() {
        SOUNDS.register();
    }
}
