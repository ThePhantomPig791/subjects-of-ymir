package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.json.GsonUtil;

import java.util.UUID;

public class IsSprintChargingCondition extends Condition {
    public final UUID uuid;

    public IsSprintChargingCondition(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean active(DataContext context) {
        if (!(context.getEntity() instanceof LivingEntity living)) return false;
        if (living.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid) == null) return false;
        return living.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(uuid).getAmount() > 0.02;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.IS_SPRINT_CHARGING.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new IsSprintChargingCondition(GsonUtil.getAsUUID(json, "uuid", UUID.fromString("73c2510b-6bdd-4378-9a29-c23fcb4d8d95")));
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity is sprint charging using an attribute";
        }
    }
}
