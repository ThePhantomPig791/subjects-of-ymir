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
    public static final PalladiumProperty<Integer> ATTACK_TIME = new IntegerProperty("subjects_of_ymir/attack_time").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<Integer> ATTACK_TIME_INCREASE = new IntegerProperty("subjects_of_ymir/attack_time_increase").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<Integer> HARDENING_ALL = new IntegerProperty("subjects_of_ymir/hardening/all").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<Integer> HARDENING_KNUCKLES = new IntegerProperty("subjects_of_ymir/hardening/knuckles").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<Integer> HARDENING_HANDS = new IntegerProperty("subjects_of_ymir/hardening/hands").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<Integer> STAMINA = new IntegerProperty("subjects_of_ymir/stamina").sync(SyncType.SELF);
    public static final PalladiumProperty<Integer> MAX_STAMINA = new IntegerProperty("subjects_of_ymir/max_stamina").sync(SyncType.SELF);

    public static final PalladiumProperty<Integer> MARKS_TIMER = new IntegerProperty("subjects_of_ymir/marks_timer").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<Integer> ODM_HOLD_ATTACK = new IntegerProperty("subjects_of_ymir/odm_hold_attack").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<Boolean> ODM_HOLD_ATTACK_INCREASING = new BooleanProperty("subjects_of_ymir/odm_hold_attack_increasing").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<Boolean> SWINGING_FISTS = new BooleanProperty("subjects_of_ymir/swinging_fists").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<Boolean> SWINGING_LEGS = new BooleanProperty("subjects_of_ymir/swinging_legs").sync(SyncType.EVERYONE);

    public static final PalladiumProperty<Boolean> GRABBED = new BooleanProperty("subjects_of_ymir/grabbed").sync(SyncType.EVERYONE);
    public static final PalladiumProperty<String> GRABBING = new StringProperty("subjects_of_ymir/grabbing").sync(SyncType.EVERYONE);

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
                handler.register(ATTACK_TIME, 20);
                handler.register(ATTACK_TIME_INCREASE, 0);
                handler.register(HARDENING_ALL, 0);
                handler.register(HARDENING_KNUCKLES, 0);
                handler.register(HARDENING_HANDS, 0);
                handler.register(STAMINA, 0);
                handler.register(MAX_STAMINA, 0);
                handler.register(MARKS_TIMER, 0);
                handler.register(ODM_HOLD_ATTACK, 0);
                handler.register(ODM_HOLD_ATTACK_INCREASING, false);
                handler.register(SWINGING_FISTS, false);
                handler.register(SWINGING_LEGS, false);
                handler.register(GRABBED, false);
                handler.register(GRABBING, "");
            }
        });
    }
}
