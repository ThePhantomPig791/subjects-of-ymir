package net.phantompig.soy.power.ability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.ResourceLocationProperty;

public class StatHealingAbility extends Ability {
    public static final PalladiumProperty<Integer> FREQUENCY = (new IntegerProperty("frequency")).configurable("Sets the frequency of healing (in ticks)");
    public static final PalladiumProperty<Integer> AMOUNT = (new IntegerProperty("amount")).configurable("Sets the amount of hearts for each healing");
    public static final PalladiumProperty<ResourceLocation> STAT = (new ResourceLocationProperty("stat")).configurable("The statistic to award");

    public StatHealingAbility() {
        this.withProperty(FREQUENCY, 20);
        this.withProperty(AMOUNT, 3);
        this.withProperty(STAT, new ResourceLocation("eat_cake_slice"));
    }

    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && !entity.level().isClientSide) {
            int frequency = entry.getProperty(FREQUENCY);
            if (frequency != 0 && entity.tickCount % frequency == 0) {
                entity.heal(entry.getProperty(AMOUNT));
                if (entity instanceof Player player) {
                    ResourceLocation stat = entry.getProperty(STAT);
                    if (!BuiltInRegistries.CUSTOM_STAT.containsKey(stat)) {
                        SubjectsOfYmir.LOGGER.error("Could not find stat {}", stat);
                        return;
                    }
                    player.awardStat(Stats.CUSTOM.get(BuiltInRegistries.CUSTOM_STAT.get(stat)), entry.getProperty(AMOUNT));
                }
            }
        }

    }

    public String getDocumentationDescription() {
        return "Heals the entity and awards a statistic with the amount healed.";
    }
}
