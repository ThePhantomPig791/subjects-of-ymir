package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class IsLeftHandedCondition extends Condition {
    public IsLeftHandedCondition() {}

    @Override
    public boolean active(DataContext context) {
        return context.getEntity() instanceof Player player && player.getMainArm() == HumanoidArm.LEFT;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.IS_LEFT_HANDED.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new IsLeftHandedCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity's main arm is the left arm.'";
        }
    }
}
