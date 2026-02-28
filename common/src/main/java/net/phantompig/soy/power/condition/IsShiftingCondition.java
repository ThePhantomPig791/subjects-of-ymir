package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class IsShiftingCondition extends Condition {
    public IsShiftingCondition() {}

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        if (!(entity instanceof SoyPlayerExtension ext) || ext.getTitanInstance().titan == null) return false;
        int pro = SoyProperties.PROGRESS.get(entity);
        return pro > 0 && pro < ext.getTitanInstance().titan.maxProgress;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.IS_SHIFTING.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new IsShiftingCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity is in the process of titan shifting.";
        }
    }
}
