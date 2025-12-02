package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;

public class ShiftProgressCondition extends Condition {
    private final int min, max;

    public ShiftProgressCondition(int minHealth, int maxHealth) {
        this.min = minHealth;
        this.max = maxHealth;
    }

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        if (entity == null) return false;

        int pro = SoyProperties.PROGRESS.get(entity);
        return pro >= min && pro <= max;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.SHIFT_PROGRESS.get();
    }

    public static class Serializer extends ConditionSerializer {
        public static final PalladiumProperty<Integer> MIN = new IntegerProperty("min").configurable("Minimum required shifting progress");
        public static final PalladiumProperty<Integer> MAX = new IntegerProperty("max").configurable("Maximum required shifting progress");

        public Serializer() {
            this.withProperty(MIN, 0);
            this.withProperty(MAX, 15);
        }

        @Override
        public Condition make(JsonObject json) {
            return new ShiftProgressCondition(getProperty(json, MIN), getProperty(json, MAX));
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity's titan shifting progress is within the given range.";
        }
    }
}
