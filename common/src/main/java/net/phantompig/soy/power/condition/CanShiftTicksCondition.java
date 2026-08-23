package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;

public class CanShiftTicksCondition extends Condition {
    private final int min, max;

    public CanShiftTicksCondition(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        if (entity == null) return false;

        int x;
        if (entity instanceof SoyPlayerExtension ext) {
            x = ext.soy$getTitanInstance().canShiftTicks;
        } else {
            return false;
        }
        return x >= min && (max == -1 || x <= max);
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.CAN_SHIFT_TICKS.get();
    }

    public static class Serializer extends ConditionSerializer {
        public static final PalladiumProperty<Integer> MIN = new IntegerProperty("min").configurable("Minimum required can shift ticks");
        public static final PalladiumProperty<Integer> MAX = new IntegerProperty("max").configurable("Maximum required can shift ticks. Set to -1 for no upper bound");

        public Serializer() {
            this.withProperty(MIN, 0);
            this.withProperty(MAX, -1);
        }

        @Override
        public Condition make(JsonObject json) {
            return new CanShiftTicksCondition(getProperty(json, MIN), getProperty(json, MAX));
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks the entity's can shift ticks timer. The timer increases when the entity takes damage. The timer always ticks down, unless it is at 1 and the entity is not at full health.";
        }
    }
}
