package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.ResourceLocationProperty;

public class StatisticCondition extends Condition {
    private final ResourceLocation stat;
    private final int min, max;

    public StatisticCondition(ResourceLocation stat, int min, int max) {
        this.stat = stat;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean active(DataContext context) {
        if (!(context.getEntity() instanceof ServerPlayer player)) return false;
        if (!BuiltInRegistries.CUSTOM_STAT.containsKey(this.stat)) {
            SubjectsOfYmir.LOGGER.error("Could not find stat {}", this.stat);
            return false;
        }
        Stat<ResourceLocation> stat = Stats.CUSTOM.get(BuiltInRegistries.CUSTOM_STAT.get(this.stat));
        int value = player.getStats().getValue(stat);
        if (this.max == -1) return this.min <= value;
        return this.min <= value && value <= this.max;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.STATISTIC.get();
    }

    public static class Serializer extends ConditionSerializer {
        public static final PalladiumProperty<ResourceLocation> STAT = new ResourceLocationProperty("stat").configurable("Statistic to check");
        public static final PalladiumProperty<Integer> MIN = new IntegerProperty("min").configurable("The statistic must be greater than or equal to this value");
        public static final PalladiumProperty<Integer> MAX = new IntegerProperty("max").configurable("The statistic must be less than or equal to this value. If set to -1, the max will not be checked (only min will matter)");

        public Serializer() {
            this.withProperty(STAT, new ResourceLocation("sneak_time"));
            this.withProperty(MIN, 0);
            this.withProperty(MAX, -1);
        }

        @Override
        public Condition make(JsonObject json) {
            return new StatisticCondition(getProperty(json, STAT), getProperty(json, MIN), getProperty(json, MAX));
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity's value for the given statistic is in the given range.";
        }
    }
}
