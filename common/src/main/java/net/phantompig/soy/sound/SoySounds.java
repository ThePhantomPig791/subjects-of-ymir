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
    public static final RegistrySupplier<SoundEvent> SHORT_HARDEN = make("short_harden", 48);
    public static final RegistrySupplier<SoundEvent> QUICK_HARDEN = make("quick_harden", 48);
    public static final RegistrySupplier<SoundEvent> HEARTBEAT = make("heartbeat", 16);
    public static final RegistrySupplier<SoundEvent> CLICK = make("click", 16);
    public static final RegistrySupplier<SoundEvent> HOOK_LAUNCH = make("hook_launch", 64);
    public static final RegistrySupplier<SoundEvent> HOOK_LAND = make("hook_land", 256);
    public static final RegistrySupplier<SoundEvent> FLARE_SHOOT = make("flare_shoot", 512);
    public static final RegistrySupplier<SoundEvent> FLARE_SCREECH = make("flare_screech", 512);
    public static final RegistrySupplier<SoundEvent> REEL = make("reel", 16);
    public static final RegistrySupplier<SoundEvent> REEL_LOCAL = make("reel_local", 16);

    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_YOUSEEBIGGIRL = make("music_disc.youseebiggirl", 256);
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_SPLINTER_WOLF = make("music_disc.splinter_wolf", 256);
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_TKT = make("music_disc.tkt", 256);
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_COUNTER_ATTACK_MANKIND = make("music_disc.counter_attack_mankind", 256);
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_CALL_OF_SILENCE = make("music_disc.call_of_silence", 256);
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_BAUKLOTZE = make("music_disc.bauklotze", 256);

    public static RegistrySupplier<SoundEvent> make(String name, float range) {
        return SOUNDS.register(name, () -> new SoundEvent(SubjectsOfYmir.rsrc(name), range, false));
    }

    public static void init() {
        SOUNDS.register();
    }
}
