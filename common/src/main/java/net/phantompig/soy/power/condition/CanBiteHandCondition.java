package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.phantompig.soy.compat.SoyCompatLayers;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class CanBiteHandCondition extends Condition {
    public CanBiteHandCondition() {}

    @Override
    public boolean active(DataContext context) {
        return !SoyCompatLayers.shouldDisableBite(context.getLivingEntity());
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.CAN_BITE_HAND.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new CanBiteHandCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity can bite their hand. Usually only false for mod compat reasons (handcuffs from Dampened).";
        }
    }
}
