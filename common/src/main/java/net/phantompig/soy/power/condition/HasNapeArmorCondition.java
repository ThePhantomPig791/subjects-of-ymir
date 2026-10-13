package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.phantompig.soy.titan.armor.ArmorSystemHolder;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class HasNapeArmorCondition extends Condition {
    public HasNapeArmorCondition() {}

    @Override
    public boolean active(DataContext context) {
        if (!(context.getEntity() instanceof ArmorSystemHolder arh)) return false;
        return arh.soy$getArmorSystem().hasNapeArmor();
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.HAS_NAPE_ARMOR.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new HasNapeArmorCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the Titan has nape armor";
        }
    }
}
