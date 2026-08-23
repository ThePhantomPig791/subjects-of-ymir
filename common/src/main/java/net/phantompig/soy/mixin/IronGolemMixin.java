package net.phantompig.soy.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.entity.goal.TargetTitanGoal;
import net.phantompig.soy.player.SoyPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends AbstractGolem {
    private IronGolemMixin(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void soy$registerGoals(CallbackInfo ci) {
        if (SoyConfig.Server.shouldIronGolemsAttackOnTitans()) {
            if (this.targetSelector == null) return;
            this.targetSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof TargetTitanGoal);
            this.targetSelector.addGoal(2, new TargetTitanGoal(this, LivingEntity.class, 5, false, false, (livingEntity) ->
                livingEntity instanceof SoyPlayerExtension ext && ext.soy$getTitanInstance().getProgress() > 0
            ));
        }
    }
}
