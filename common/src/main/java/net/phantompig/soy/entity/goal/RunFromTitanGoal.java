package net.phantompig.soy.entity.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.property.SoyProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class RunFromTitanGoal extends Goal {
    public final PathfinderMob mob;
    @Nullable
    public Entity runningFrom;
    @Nullable
    public Vec3 runningTo;

    public RunFromTitanGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        this.runningFrom = getClosestTitan();
        if (this.runningFrom != null) {
            newPosition();
            start();
            return runningTo != null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.runningFrom == null || this.runningFrom.isRemoved()) return false;
        double range = 32;
        if (this.mob.getAttribute(Attributes.FOLLOW_RANGE) != null) range = this.mob.getAttribute(Attributes.FOLLOW_RANGE).getValue();
        if (this.mob.distanceTo(this.runningFrom) < range * 2) {
            start();
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        if (runningTo == null || this.mob.getNavigation().isDone() || this.mob.position().distanceToSqr(runningTo) < 25) {
            this.runningFrom = getClosestTitan();
            if (runningFrom == null) return;
            newPosition();
        }
        if (runningTo == null) return;
        this.mob.getNavigation().moveTo(runningTo.x, runningTo.y, runningTo.z, 1);
    }

    @Override
    public void stop() {
        this.runningFrom = null;
        this.runningTo = null;
    }

    public void newPosition(double range) {
        if (runningFrom == null) return;
        runningTo = DefaultRandomPos.getPosAway(this.mob, (int) (2 * range), 4, this.runningFrom.position());
    }
    public void newPosition() {
        newPosition(getRange());
    }

    public double getRange() {
        return this.mob.getAttribute(Attributes.FOLLOW_RANGE) == null ? 32 : this.mob.getAttribute(Attributes.FOLLOW_RANGE).getValue();
    }

    @Nullable
    public Entity getClosestTitan() {
        double range = 32;
        if (this.mob.getAttribute(Attributes.FOLLOW_RANGE) != null) range = this.mob.getAttribute(Attributes.FOLLOW_RANGE).getValue();
        return this.mob.level()
                .getEntities(this.mob, this.mob.getBoundingBox().inflate(range))
                .stream()
                .filter(en -> SoyProperties.PROGRESS.get(en) != null && SoyProperties.PROGRESS.get(en) > 0)
                .min(Comparator.comparing(e -> e.distanceTo(this.mob)))
                .orElse(null);
    }
}
