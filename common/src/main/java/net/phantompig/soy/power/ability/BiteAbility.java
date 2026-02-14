package net.phantompig.soy.power.ability;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.entity.SoyDamageSources;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AnimationTimer;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;
import net.threetag.palladium.util.property.SyncType;

public class BiteAbility extends Ability implements AnimationTimer {
    private static final int MAX_TIMER = 10;

    public static final PalladiumProperty<Integer> TIMER = new IntegerProperty("timer").sync(SyncType.NONE);
    public static final PalladiumProperty<Integer> PREV_TIMER = new IntegerProperty("prev_timer").sync(SyncType.NONE);

    public BiteAbility() {}

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(TIMER, 0);
        manager.register(PREV_TIMER, 0);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance instance, IPowerHolder holder, boolean enabled) {
        int timer = instance.getProperty(TIMER);
        instance.setUniqueProperty(PREV_TIMER, timer);

        if (enabled && timer < MAX_TIMER) {
            instance.setUniqueProperty(TIMER, timer + 1);
        } else if (!enabled && timer > 0) {
            instance.setUniqueProperty(TIMER, timer - 1);
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled) {
            if (entry.getProperty(TIMER) == MAX_TIMER && !entity.isCrouching()) {
                entity.hurt(SoyDamageSources.selfBite(entity.level(), entity), 1);
                if (entity instanceof SoyPlayerExtension ext) ext.getTitanInstance().canShiftTicks += 60;
            }
        }
    }

    @Override
    public float getAnimationValue(AbilityInstance instance, float partialTick) {
        return Mth.lerp(partialTick, instance.getProperty(PREV_TIMER), instance.getProperty(TIMER)) / (float) MAX_TIMER;
    }

    @Override
    public float getAnimationTimer(AbilityInstance instance, float partialTick, boolean maxedOut) {
        if (maxedOut) {
            return MAX_TIMER;
        }
        return instance.getProperty(TIMER);
    }
}
