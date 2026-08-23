package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class IsUnshiftingCondition extends Condition {
    public IsUnshiftingCondition() {}

    @Override
    public boolean active(DataContext context) {
        return context.getEntity() instanceof SoyPlayerExtension ext && ext.soy$getTitanInstance().forceUnshift;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.IS_UNSHIFTING.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new IsUnshiftingCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Returns true for the one tick that a titan is unshifting for.";
        }
    }
}
