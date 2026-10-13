package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.phantompig.soy.titan.armor.ArmorSystemHolder;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class HasLowerArmorCondition extends Condition {
    public HasLowerArmorCondition() {}

    @Override
    public boolean active(DataContext context) {
        if (!(context.getEntity() instanceof ArmorSystemHolder arh)) return false;
        return arh.soy$getArmorSystem().hasLowerArmor();
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.HAS_LOWER_ARMOR.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new HasLowerArmorCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the Titan has lower armor";
        }
    }
}
