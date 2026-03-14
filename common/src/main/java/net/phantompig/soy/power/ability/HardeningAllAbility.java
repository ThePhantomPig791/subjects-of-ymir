package net.phantompig.soy.power.ability;

import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.hardening.HardeningSystem;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.property.*;

public class HardeningAllAbility extends Ability {
    public static final PalladiumProperty<Integer> TIME = new IntegerProperty("time").configurable("The time it takes to fully crystal harden. After this amount of ticks, the crystal will start to grow outwards while the ability is still enabled. When the ability is disabled, the entity will be ejected from titan form");
    public static final PalladiumProperty<String> INFINITE_RANGE_ABILITY = new StringProperty("infinite_range_ability").configurable("Points to an ability in the same power. If that ability is unlocked and enabled, then the crystal will have infinite range (the range does not increase linearly)");

    public static final PalladiumProperty<Integer> TIMER = new IntegerProperty("timer").sync(SyncType.NONE);

    public HardeningAllAbility() {
        this.withProperty(TIME, 100);
        this.withProperty(INFINITE_RANGE_ABILITY, "null");
    }

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(TIMER, 0);
    }

    @Override
    public void firstTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled) {
            entry.setUniqueProperty(TIMER, 0);
        }
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && entity instanceof HardeningSystemHolder hardh) {
            HardeningSystem hardening = hardh.soy$getHardeningSystem();
            float percentage = entry.getProperty(TIMER) / (float) entry.getProperty(TIME);
            String infRangeName = entry.getProperty(INFINITE_RANGE_ABILITY);
            boolean infRange = holder.getAbilities().containsKey(infRangeName) && holder.getAbilities().get(infRangeName).isEnabled();
            if (percentage < 1) {
                hardening.setAllHardening(percentage);
            }
            for (int i = 0; i < 4; i++) {
                hardening.placePhysicalHardening(percentage, infRange);
            }
            entry.setUniqueProperty(TIMER, entry.getProperty(TIMER) + 1);
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled) {
            if (!(entity instanceof SoyPlayerExtension ext) || ext.getTitanInstance().titan == null) return;
            entry.setUniqueProperty(TIMER, 0);
            ext.getTitanInstance().titan.unshift(entity, true, false);
        }
    }
}
