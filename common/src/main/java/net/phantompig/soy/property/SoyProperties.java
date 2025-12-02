package net.phantompig.soy.property;

import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.event.PalladiumEvents;
import net.threetag.palladium.util.property.*;

public class SoyProperties {
    public static final PalladiumProperty<Integer> PROGRESS = new IntegerProperty("subjects_of_ymir/progress").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<Float> SCALE = new FloatProperty("subjects_of_ymir/scale").sync(SyncType.EVERYONE);

    public static void init() {
        PalladiumEvents.REGISTER_PROPERTY.register(handler -> {
            if (handler.getEntity() instanceof LivingEntity) {
                handler.register(PROGRESS, 0);
                handler.register(SCALE, 1f);
            }
        });
    }
}
