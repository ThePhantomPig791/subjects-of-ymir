package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class CanShiftCondition extends Condition {
    public CanShiftCondition() {}

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        if (!(entity instanceof SoyPlayerExtension ext) || ext.getTitanInstance().getProgress() > 0) return false;
        return ext.getTitanInstance().canShift();
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.CAN_SHIFT.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new CanShiftCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity is able to shift into a titan";
        }
    }
}
