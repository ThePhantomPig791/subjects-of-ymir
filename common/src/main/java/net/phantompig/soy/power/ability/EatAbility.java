package net.phantompig.soy.power.ability;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.entity.SoyDamageSources;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.property.*;
import org.joml.Vector3f;

public class EatAbility extends Ability {
    public static final PalladiumProperty<Float> DAMAGE = new FloatProperty("damage").configurable("The damage to deal on each munch");
    public static final PalladiumProperty<Integer> TIME = new IntegerProperty("time").configurable("The time in ticks between munches");

    public static final PalladiumProperty<Integer> TIMER = new IntegerProperty("timer").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<Integer> TIMER_PREV = new IntegerProperty("timer_prev").sync(SyncType.EVERYONE);

    public EatAbility() {
        this.withProperty(DAMAGE, 2f);
        this.withProperty(TIME, 15);
    }

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(TIMER, 0);
        manager.register(TIMER_PREV, 0);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        int timer = entry.getProperty(TIMER);
        if (timer != entry.getProperty(TIMER_PREV)) entry.setUniqueProperty(TIMER_PREV, timer);
        if (enabled) {
            entry.setUniqueProperty(TIMER, ++timer);
            if (entity.level() instanceof ServerLevel level
                    && timer >= entry.getProperty(TIME)
                    && timer % entry.getProperty(TIME) == 0
                    && GrabbingAbility.isGrabbingSomething(entity)
                    && level.getEntity(GrabbingAbility.getGrabbing(entity)) instanceof LivingEntity grabbed
            ) {
                grabbed.hurt(SoyDamageSources.eat(level, entity), entry.getProperty(DAMAGE));
                PlayerUtil.playSoundToAll(level, entity.getX(), entity.getEyeY(), entity.getZ(), 32, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1, 0.7f);
                PlayerUtil.playSoundToAll(level, entity.getX(), entity.getEyeY(), entity.getZ(), 32, SoySounds.TITAN_KILL.get(), SoundSource.PLAYERS, 0.25f, 1.6f + (float) (0.1 * Math.random()));
                PlayerUtil.spawnParticleForAll(level,32,
                        new DustParticleOptions(new Vector3f(0.6f, 0.05f, 0), 1),
                        false,
                        grabbed.getX(), grabbed.getY(), grabbed.getZ(),
                        0.2f, 0.4f, 0.2f,
                        0, 10
                );
            }
        } else if (timer > 0) {
            timer = timer % entry.getProperty(TIME);
            entry.setUniqueProperty(TIMER, --timer);
        }
    }

    public static float getEatProgress(LivingEntity entity, float partialTick) {
        int max = 0;
        AbilityInstance maxEntry = null;
        for (AbilityInstance entry : AbilityUtil.getEnabledInstances(entity, SoyAbilities.EAT.get())) {
            if (entry.getProperty(TIMER) > max) {
                maxEntry = entry;
                max = entry.getProperty(TIMER);
            }
        }
        if (maxEntry != null) {
            float val = Mth.lerp(partialTick, maxEntry.getProperty(TIMER_PREV), max);
            float pro = (val % maxEntry.getProperty(TIME)) / (float) (maxEntry.getProperty(TIME));
            if (pro >= 0.5) return (pro);
            return 1 - pro;
        }
        return 0;
    }

    public static boolean isEating(LivingEntity entity) {
        return AbilityUtil.isTypeEnabled(entity, SoyAbilities.EAT.get());
    }

    @Override
    public String getDocumentationDescription() {
        return "Deals damage to the grabbed entity on a specified frequency. Also plays an animation.";
    }
}
