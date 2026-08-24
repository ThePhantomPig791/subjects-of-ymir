package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.power.ability.GrabbingAbility;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class IsGrabbingSomethingCondition extends Condition {
    public IsGrabbingSomethingCondition() {}

    @Override
    public boolean active(DataContext context) {
        if (context.getEntity() instanceof LivingEntity living) {
            return GrabbingAbility.isGrabbingSomething(living);
        }
        return false;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.IS_GRABBING_SOMETHING.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new IsGrabbingSomethingCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity is grabbing something";
        }
    }
}
