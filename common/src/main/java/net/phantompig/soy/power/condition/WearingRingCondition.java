package net.phantompig.soy.power.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.item.SoyItemTags;
import net.threetag.palladium.compat.curiostinkets.CuriosTrinketsUtil;
import net.threetag.palladium.condition.Condition;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.util.context.DataContext;

import java.util.List;

public class WearingRingCondition extends Condition {
    public WearingRingCondition() {}

    @Override
    public boolean active(DataContext context) {
        List<ItemStack> rings = CuriosTrinketsUtil.getInstance().getItemsInSlot(context.getLivingEntity(), "ring");
        rings.addAll(CuriosTrinketsUtil.getInstance().getItemsInSlot(context.getLivingEntity(), "hand/ring"));
        rings.addAll(CuriosTrinketsUtil.getInstance().getItemsInSlot(context.getLivingEntity(), "offhand/ring"));
        for (ItemStack stack : rings) {
            if (stack.is(SoyItemTags.RINGS)) return true;
        }
        return false;
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
            return "Checks if the entity is wearing an item in the #subjects_of_ymir:rings tag in either Curios or Trinkets ring slot";
        }
    }
}
