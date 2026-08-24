package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.power.ability.GrabbingAbility;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class GrabbedEntityNearbyCondition extends Condition {
    public GrabbedEntityNearbyCondition() {}

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        if (entity instanceof LivingEntity living) {
            for (Entity e : entity.level().getEntities(entity, entity.getBoundingBox().inflate(30))) {
                if (GrabbingAbility.getGrabbing(living).equals(e.getUUID())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.GRABBED_ENTITY_NEARBY.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new GrabbedEntityNearbyCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity being grabbed by this entity is nearby.";
        }
    }
}
