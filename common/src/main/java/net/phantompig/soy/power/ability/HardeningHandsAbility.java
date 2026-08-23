package net.phantompig.soy.power.ability;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.titan.hardening.HardeningSystem;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;
import net.threetag.palladium.util.property.SyncType;

public class HardeningHandsAbility extends Ability {
    public static final PalladiumProperty<Integer> TIME = new IntegerProperty("time").configurable("The time it takes to fully harden the hands");

    public static final PalladiumProperty<Integer> TIMER = new IntegerProperty("timer").sync(SyncType.NONE);

    public HardeningHandsAbility() {
        this.withProperty(TIME, 100);
    }

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(TIMER, 0);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && entity instanceof HardeningSystemHolder hardh && entity instanceof SoyPlayerExtension ext) {
            HardeningSystem hardening = hardh.soy$getHardeningSystem();
            float percentage = entry.getProperty(TIMER) / (float) entry.getProperty(TIME);
            if (percentage <= 1) {
                if (!entity.level().isClientSide()) hardening.setHands(percentage);
                PlayerUtil.playSoundToAll(entity.level(),
                        entity.getX(),
                        entity.getY() + entity.getBoundingBox().getYsize() / 2,
                        entity.getZ(),
                        48,
                        SoySounds.QUICK_HARDEN.get(),
                        SoundSource.PLAYERS,
                        0.7f,
                        (float) (0.1 * Math.random() + 0.8)
                );
                ext.soy$getTitanInstance().exhaust(2);
            }
            entry.setUniqueProperty(TIMER, entry.getProperty(TIMER) + 1);
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (!enabled && entity instanceof SoyPlayerExtension ext && ext.soy$getTitanInstance().getProgress() == 0) {
            entry.setUniqueProperty(TIMER, 0);
        }
    }
}
