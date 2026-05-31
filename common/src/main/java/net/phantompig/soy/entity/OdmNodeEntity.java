package net.phantompig.soy.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OdmNodeEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_RIGHT = SynchedEntityData.defineId(OdmNodeEntity.class, EntityDataSerializers.BOOLEAN);

    public boolean stuck;

    public OdmNodeEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (stuck) {
            Entity owner = this.getOwner();
            if (owner != null) {
                double distanceScale = this.position().distanceTo(owner.position());
                distanceScale = distanceScale / (distanceScale + 10);
                owner.addDeltaMovement(this.position().subtract(owner.position()).normalize().scale(0.7 * distanceScale));
            }

            boolean newStuck = false;
            for (VoxelShape shape : level().getBlockCollisions(this, this.getBoundingBox().inflate(1))) {
                if (!shape.isEmpty()) newStuck = true;
            }
            this.stuck = newStuck;
        } else {
            if (!this.isNoGravity()) {
                this.addDeltaMovement(new Vec3(0, -this.getGravity(), 0));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.stuck = true;
        this.setDeltaMovement(0, 0, 0);
        this.setPos(result.getLocation());
    }

    public float getGravity() {
        return 0.04f;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public void setRight(boolean rightHanded) {
        this.entityData.set(DATA_RIGHT, rightHanded);
    }
    public boolean getRight() {
        return this.entityData.get(DATA_RIGHT);
    }

    public Vec3 getOwnerPosition(float partialTick) {
        var owner = this.getOwner();
        if (owner == null) return Vec3.ZERO;
        float xRot = owner.getXRot();
        float yRot = owner instanceof LivingEntity living ? living.yBodyRot : owner.getYRot();
        return owner.getPosition(partialTick).add(getRightOrLeftOffset(xRot, yRot, this.getRight()));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RIGHT, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.stuck = compound.getBoolean("stuck");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("stuck", this.stuck);
    }

    public Vec3 getRightOrLeftOffset(float xRot, float yRot, boolean rightHand) {
        Vec3 offset = this.calculateViewVector(xRot, yRot); // only using this#calculateViewVector because it's private
        offset = new Vec3(offset.z * (rightHand ? -1 : 1), 0, offset.x * (rightHand ? 1 : -1));
        return offset.scale(0.25).add(0, 0.65, 0);
    }
}
