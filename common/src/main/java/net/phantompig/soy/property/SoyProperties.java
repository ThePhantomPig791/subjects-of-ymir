package net.phantompig.soy.property;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.titan.TitanInstance;
import net.threetag.palladium.event.PalladiumEvents;
import net.threetag.palladium.util.property.*;

import java.awt.*;

public class SoyProperties {
    public static final PalladiumProperty<Integer> PROGRESS = new IntegerProperty("subjects_of_ymir/progress").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<Integer> CHARGE = new IntegerProperty("subjects_of_ymir/charge").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<ResourceLocation> TITAN = new ResourceLocationProperty("subjects_of_ymir/titan").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<String> VARIANT = new StringProperty("subjects_of_ymir/variant").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<Integer> DECAY = new IntegerProperty("subjects_of_ymir/decay").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<Color> EYE_COLOR = new ColorProperty("subjects_of_ymir/eye_color").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<Integer> PATH_POINTS = new IntegerProperty("subjects_of_ymir/path_points").sync(SyncType.NONE);
    public static final PalladiumProperty<Integer> ATTACK_SPEED = new IntegerProperty("subjects_of_ymir/attack_speed").sync(SyncType.EVERYONE);

    public static void init() {
        PalladiumEvents.REGISTER_PROPERTY.register(handler -> {
            if (handler.getEntity() instanceof LivingEntity) {
                handler.register(PROGRESS, 0);
                handler.register(CHARGE, 0);
                handler.register(TITAN, SubjectsOfYmir.rsrc("null"));
                handler.register(VARIANT, "default");
                handler.register(DECAY, TitanInstance.START_CORPSE_DECAY);
                handler.register(EYE_COLOR, Color.WHITE);
                handler.register(PATH_POINTS, 0);
                handler.register(ATTACK_SPEED, 20);
            }
        });
    }
}
