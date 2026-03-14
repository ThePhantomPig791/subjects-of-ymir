package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.phantompig.soy.titan.hardening.HardeningSystemHolder;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.property.FloatProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.StringProperty;

public class HardeningCondition extends Condition {
    private final String type;
    private final float min, max;

    public HardeningCondition(String type, float min, float max) {
        this.type = type;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean active(DataContext context) {
        if (!(context.getEntity() instanceof HardeningSystemHolder ext)) return false;
        float value = switch (type) {
            case "all" -> ext.soy$getHardeningSystem().getAllHardening();
            case "knuckles" -> ext.soy$getHardeningSystem().getKnuckles();
            case "hands" -> ext.soy$getHardeningSystem().getHands();
            default -> 0;
        };
        return this.min <= value && value <= this.max;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.HARDENING.get();
    }

    public static class Serializer extends ConditionSerializer {
        public static final PalladiumProperty<String> TYPE = new StringProperty("hardening_type").configurable("Accepts 'all', 'knuckles', or 'hands'");
        public static final PalladiumProperty<Float> MIN = new FloatProperty("min").configurable("The hardening must be greater than or equal to this value");
        public static final PalladiumProperty<Float> MAX = new FloatProperty("max").configurable("The hardening must be less than or equal to this value");


        public Serializer() {
            this.withProperty(TYPE, "hardening_type");
            this.withProperty(MIN, 0f);
            this.withProperty(MAX, 1f);
        }

        @Override
        public Condition make(JsonObject json) {
            return new HardeningCondition(getProperty(json, TYPE), getProperty(json, MIN), getProperty(json, MAX));
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks the value of a specific hardening level on a scale of 0-1";
        }
    }
}
