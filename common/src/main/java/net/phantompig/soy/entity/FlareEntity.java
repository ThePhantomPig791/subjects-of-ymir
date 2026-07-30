package net.phantompig.soy.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.particle.FlareParticleOptions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.*;

public class FlareEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(FlareEntity.class, EntityDataSerializers.INT);

    public FlareEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_COLOR, 0);
    }

    public Color getColor() {
        return new Color(this.entityData.get(DATA_COLOR));
    }
    public void setColor(Color color) {
        setColor(color.getRGB());
    }
    public void setColor(int color) {
        this.entityData.set(DATA_COLOR, color);
    }

    @NotNull
    @Override
    protected ParticleOptions getTrailParticle() {
        var color = this.getColor();
        return new FlareParticleOptions(new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f), Math.max(0.01f, getScale(this.tickCount)));
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0, true, false));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 70, 0, true, false));
        }
        result.getEntity().setRemainingFireTicks(160);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.addDeltaMovement(new Vec3(0, -this.getGravity(), 0));
        }
    }

    public float getGravity() {
        return 0.08f;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 0.99f;
    }

    private static float getScale(int x) {
       return (-0.00424333f * x * x) + (0.422433f * x) + 0.2f;
    }
}
