package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.entity.TitanCorpseEntity;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class IsCorpseCondition extends Condition {
    public IsCorpseCondition() {}

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        return entity instanceof TitanCorpseEntity;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.IS_CORPSE.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new IsCorpseCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity is a titan corpse.";
        }
    }
}
