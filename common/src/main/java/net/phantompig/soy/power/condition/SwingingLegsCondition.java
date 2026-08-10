package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class SwingingLegsCondition extends Condition {
    public SwingingLegsCondition() {}

    @Override
    public boolean active(DataContext context) {
        return SoyProperties.SWINGING_LEGS.get(context.getEntity());
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.SWINGING_LEGS.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new SwingingLegsCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns true if the Titan is swinging their legs";
        }
    }
}
