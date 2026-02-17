package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class AtFullHealthCondition extends Condition {
    public AtFullHealthCondition() {}

    @Override
    public boolean active(DataContext context) {
        if (!(context.getEntity() instanceof LivingEntity living)) return false;
        return living.getHealth() >= living.getMaxHealth();
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.IS_SHIFTING.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new AtFullHealthCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity is at full health.";
        }
    }
}
