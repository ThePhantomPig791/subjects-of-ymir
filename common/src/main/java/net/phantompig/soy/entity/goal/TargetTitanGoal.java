package net.phantompig.soy.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TargetTitanGoal extends NearestAttackableTargetGoal {
    public TargetTitanGoal(Mob mob, Class targetType, int randomInterval, boolean mustSee, boolean mustReach, @NotNull Predicate<LivingEntity> targetPredicate) {
        super(mob, targetType,
                randomInterval,
                mustSee,
                mustReach,
                targetPredicate
        );
    }
}
