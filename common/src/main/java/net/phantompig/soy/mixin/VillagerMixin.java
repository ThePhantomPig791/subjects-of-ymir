package net.phantompig.soy.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.entity.goal.RunFromTitanGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    private VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerBrainGoals", at = @At("TAIL"))
    public void soy$registerBrainGoals(Brain<Villager> villagerBrain, CallbackInfo ci) {
        SubjectsOfYmir.LOGGER.info("applied goal to {}", this);
        GoalSelector goals = this.goalSelector;
        if (this.goalSelector == null) return;
        goals.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof RunFromTitanGoal);
        goals.addGoal(0, new RunFromTitanGoal(this));
        SubjectsOfYmir.LOGGER.info("   goals:");
        goals.getAvailableGoals().forEach(goal -> {
            SubjectsOfYmir.LOGGER.info("   | {}", goal.getGoal());
        });
    }
}
