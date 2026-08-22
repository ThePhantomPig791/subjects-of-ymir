package net.phantompig.soy.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class OdmNodeEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_RIGHT = SynchedEntityData.defineId(OdmNodeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_STUCK_ENTITY_ID = SynchedEntityData.defineId(OdmNodeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_STUCK = SynchedEntityData.defineId(OdmNodeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_OFFSET = SynchedEntityData.defineId(OdmNodeEntity.class, EntityDataSerializers.FLOAT);

    public UUID stuckEntityUuid;
    public int stuckTicks;

    public OdmNodeEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getStuck()) {
            // pull player
            Entity owner = this.getOwner();
            if (owner == null || SoyProperties.PROGRESS.get(owner) > 0 || owner instanceof LivingEntity living && !belongsTo(living)) {
                this.discard();
                return;
            }
            double distanceScale = this.position().distanceTo(owner.position());
            distanceScale = distanceScale / (distanceScale + 10);
            Optional<Entity> stuckEntity = this.getStuckEntity();
            float reelVolume;
            if (stuckEntity.isPresent() && owner.isCrouching() && owner.onGround() && !stuckEntity.get().isCrouching()) {
                stuckEntity.get().stopRiding();
                stuckEntity.get().addDeltaMovement(owner.position().subtract(stuckEntity.get().position()).normalize().scale(distanceScale / stuckEntity.get().getBoundingBox().getYsize()));
                sendMotionPacket(stuckEntity.get());
                reelVolume = 2 * (float) stuckEntity.get().getDeltaMovement().lengthSqr();
            } else {
                owner.stopRiding();
                owner.addDeltaMovement(this.position().subtract(owner.position()).normalize().scale(0.7 * distanceScale));
                sendMotionPacket(owner);
                reelVolume = 2 * (float) owner.getDeltaMovement().lengthSqr();
            }

            if (this.level().isClientSide() && this.stuckTicks % 17 == 0) {
                if (reelVolume > 0.35) {
                    float pitch = (float) (0.95 + 0.1 * Math.random());
                    if (owner.equals(Minecraft.getInstance().player)) owner.playSound(SoySounds.REEL_LOCAL.get(), reelVolume, pitch);
                    else this.level().playLocalSound(owner.getX(), owner.getY(), owner.getZ(), SoySounds.REEL.get(), SoundSource.PLAYERS, reelVolume, pitch, false);
                }
            }

            // check still stuck
            boolean newStuck = false;
            // to block
            if (!this.level().isClientSide()) {
                for (VoxelShape shape : level().getBlockCollisions(this, this.getBoundingBox().inflate(1))) {
                    if (!shape.isEmpty()) {
                        newStuck = true;
                        break;
                    }
                }
            }
            // to entity
            for (Entity e : this.getStuckEntity().stream().toList()) {
                if (e.isRemoved() || (e instanceof LivingEntity l && l.isDeadOrDying())) {
                    this.setStuckEntity(null);
                    break;
                } else {
                    this.setPos(e.getEyePosition().add(0, this.getOffset(), 0));
                    newStuck = true;
                    break;
                }
            }
            this.setStuck(newStuck);

            stuckTicks++;
        } else {
            if (!this.level().isClientSide() && !this.isNoGravity()) {
                this.addDeltaMovement(new Vec3(0, -this.getGravity(), 0));

                // aim assist
                if (this.tickCount > 10) {
                    final int assistRange = 2;
                    for (int dx = -assistRange; dx <= assistRange; dx++) {
                        for (int dy = assistRange; dy >= -assistRange; dy--) {
                            for (int dz = -assistRange; dz <= assistRange; dz++) {
                                if (this.level().getBlockState(BlockPos.containing(this.position().add(dx, dy, dz))).blocksMotion()) {
                                    this.setDeltaMovement(new Vec3(dx, dy, dz));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendMotionPacket(Entity entity) {
        if (entity instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
        }
    }

    private boolean belongsTo(LivingEntity entity) {
        ItemStack leggings = entity.getItemBySlot(EquipmentSlot.LEGS);
        return (leggings.getItem() instanceof OdmAttachableAddonArmorItem odm) && (this.uuid.equals(odm.getLeftHook(leggings)) || this.uuid.equals(odm.getRightHook(leggings)));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (amount < 4) return false;
        Entity direct = source.getDirectEntity();
        if (direct == null) return false;
        Entity owner = this.getOwner();
        if (direct.equals(owner)) return false;
        this.discard();
        if (owner instanceof LivingEntity living) {
            ItemStack leggings = living.getItemBySlot(EquipmentSlot.LEGS);
            if (leggings.getItem() instanceof OdmAttachableAddonArmorItem odm) {
                odm.removeLeftHook(leggings);
                odm.removeRightHook(leggings);
            }
        }
        PlayerUtil.spawnParticleForAll(this.level(), 32, ParticleTypes.CRIT, false, this.getX(), this.getY(), this.getZ(), 0.2f, 0.2f, 0.2f, 0, 8);
        return true;
    }

    @Override
    protected void onHit(HitResult result) {
        if (result instanceof EntityHitResult eHit && this.ownedBy(eHit.getEntity())) return;

        final float v = (float) (0.5 * this.getDeltaMovement().lengthSqr()), p = (float) (0.9 + 0.2 * Math.random());
        final boolean hitHardenedCrystal = result instanceof BlockHitResult bHit && this.level().getBlockState(bHit.getBlockPos()).is(SoyBlocks.HARDENING_BLOCK.get());
        PlayerUtil.playSoundToAll(this.level(), this.getX(), this.getY(), this.getZ(), 64, SoySounds.HOOK_LAND.get(), SoundSource.PLAYERS, v, p);
        if (hitHardenedCrystal) {
            PlayerUtil.playSoundToAll(this.level(), result.getLocation().x, result.getLocation().y, result.getLocation().z, 32, SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.6f, p + 0.7f);
            PlayerUtil.playSoundToAll(this.level(), result.getLocation().x, result.getLocation().y, result.getLocation().z, 32, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.6f, p + 0.5f);
            PlayerUtil.playSoundToAll(this.level(), result.getLocation().x, result.getLocation().y, result.getLocation().z, 32, SoundEvents.METAL_PLACE, SoundSource.PLAYERS, 0.6f, p + 0.3f);
        }
        if (this.getOwner() instanceof Player pl) {
            PlayerUtil.playSound(pl, this.getX(), this.getY(), this.getZ(), SoySounds.HOOK_LAND.get(), SoundSource.PLAYERS, v, p);
        }

        this.setStuck(true);
        this.setDeltaMovement(0, 0, 0);
        if (result.getType() == HitResult.Type.BLOCK) {
            this.setPos(result.getLocation());
            this.setOffset(0);
        }
        super.onHit(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity hitEntity = result.getEntity();
        float offset = (float) (this.getY() - hitEntity.getEyeY());
        offset = Mth.clamp(offset, -hitEntity.getEyeHeight(), hitEntity.getBbHeight() - hitEntity.getEyeHeight());
        this.setOffset(offset);

        if (this.level().isClientSide()) return;
        if (hitEntity.equals(this.getOwner())) return;
        if (this.getStuckEntity().isPresent() && this.getStuckEntity().get().equals(hitEntity)) return;
        this.setStuckEntity(hitEntity);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !(target instanceof OdmNodeEntity);
    }

    public float getGravity() {
        return this.getStuck() ? 0 : 0.04f;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ASH;
    }

    public void setRight(boolean rightHanded) {
        this.entityData.set(DATA_RIGHT, rightHanded);
    }
    public boolean getRight() {
        return this.entityData.get(DATA_RIGHT);
    }

    public void setStuckEntity(@Nullable Entity entity) {
        if (entity == null) {
            this.entityData.set(DATA_STUCK_ENTITY_ID, -1);
            this.setStuck(false);
            this.stuckEntityUuid = null;
            return;
        }
        this.entityData.set(DATA_STUCK_ENTITY_ID, entity.getId());
        this.setStuck(true);
        this.stuckEntityUuid = entity.getUUID();
    }

    public Optional<Entity> getStuckEntity() { // will cache the entity ID if it's not set, so this should be called on the server
        if (!this.getStuck()) return Optional.empty();
        int id = this.entityData.get(DATA_STUCK_ENTITY_ID);
        if (this.level() instanceof ServerLevel level && id == -1 && this.stuckEntityUuid != null) {
            final Entity e = level.getEntity(this.stuckEntityUuid);
            if (e != null) {
                id = e.getId();
                this.entityData.set(DATA_STUCK_ENTITY_ID, id);
            }
            return Optional.ofNullable(e);
        }
        return Optional.ofNullable(this.level().getEntity(id));
    }

    public boolean getStuck() {
        return this.entityData.get(DATA_STUCK);
    }
    public void setStuck(boolean stuck) {
        this.entityData.set(DATA_STUCK, stuck);
    }

    public float getOffset() {
        return this.entityData.get(DATA_OFFSET);
    }
    public void setOffset(float offset) {
        this.entityData.set(DATA_OFFSET, offset);
    }

    public Vec3 getOwnerPosition(float partialTick) {
        var owner = this.getOwner();
        if (owner == null) return Vec3.ZERO;
        float yRot = owner instanceof LivingEntity living ? living.yBodyRot : owner.getYRot();
        return owner.getPosition(partialTick).add(getRightOrLeftOffset(yRot, this.getRight()).subtract(0, owner.isCrouching() ? 0.2 : 0, 0));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RIGHT, false);
        this.entityData.define(DATA_STUCK_ENTITY_ID, -1);
        this.entityData.define(DATA_STUCK, false);
        this.entityData.define(DATA_OFFSET, 0f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("StuckEntityUuid")) this.stuckEntityUuid = compound.getUUID("StuckEntityUuid");
        this.setRight(compound.getBoolean("RightHanded"));
        this.setOffset(compound.getFloat("OffsetY"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.stuckEntityUuid != null) compound.putUUID("StuckEntityUuid", this.stuckEntityUuid);
        compound.putBoolean("RightHanded", this.getRight());
        compound.putFloat("OffsetY", this.getOffset());
    }

    public Vec3 getRightOrLeftOffset(float yRot, boolean rightHand) {
        Vec3 offset = this.calculateViewVector(0, yRot); // even though it's this.calculateViewVector, the method is basically static and doesn't have anything to do with this node entity
        offset = new Vec3(offset.z * (rightHand ? -1 : 1), 0, offset.x * (rightHand ? 1 : -1)).normalize();
        return offset.scale(0.15).add(0, 0.8, 0);
    }
}
