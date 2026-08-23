package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class CanShiftFromStaminaCondition extends Condition {
    public CanShiftFromStaminaCondition() {}

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        if (!(entity instanceof SoyPlayerExtension ext) || ext.soy$getTitanInstance().getProgress() > 0) return false;
        return ext.soy$getTitanInstance().canShiftFromStamina();
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.CAN_SHIFT_FROM_STAMINA.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new CanShiftFromStaminaCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity has enough stamina to shift into a titan";
        }
    }
}
