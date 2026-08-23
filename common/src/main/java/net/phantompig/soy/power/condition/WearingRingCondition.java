package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

public class WearingRingCondition extends Condition {
    public WearingRingCondition() {}

    @Override
    public boolean active(DataContext context) {
        Entity entity = context.getEntity();
        if (!(entity instanceof SoyPlayerExtension ext) || ext.soy$getTitanInstance().getProgress() > 0) return false;
        return ext.soy$getTitanInstance().wearingRing();
    }

    @Override
    public ConditionSerializer getSerializer() {
        return SoyConditionSerializers.WEARING_RING.get();
    }

    public static class Serializer extends ConditionSerializer {
        public Serializer() {}

        @Override
        public Condition make(JsonObject json) {
            return new WearingRingCondition();
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity is wearing an item from the #subjects_of_ymir:rings tag in a Trinkets or Curios ring slot";
        }
    }
}
