package net.phantompig.soy.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class SoySounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(SubjectsOfYmir.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> SHIFT = make("shift");
    public static final RegistrySupplier<SoundEvent> SHIFT_LOCAL = make("shift_local");
    public static final RegistrySupplier<SoundEvent> ELECTRICITY = make("electricity");
    public static final RegistrySupplier<SoundEvent> ELECTRICITY_FINAL = make("electricity_final");

    public static RegistrySupplier<SoundEvent> make(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(SubjectsOfYmir.rsrc(name)));
    }
}
