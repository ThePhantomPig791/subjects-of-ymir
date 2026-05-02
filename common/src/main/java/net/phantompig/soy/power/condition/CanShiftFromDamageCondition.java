package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class CanShiftFromDamageCondition extends Condition {
    public CanShiftFromDamageCondition() {}

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        if (!(entity instanceof SoyPlayerExtension ext) || ext.getTitanInstance().getProgress() > 0) return false;
        return ext.getTitanInstance().canShiftFromDamage();
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.CAN_SHIFT_FROM_DAMAGE.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new CanShiftFromDamageCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity has taken enough damage recently to shift into a titan";
        }
    }
}
