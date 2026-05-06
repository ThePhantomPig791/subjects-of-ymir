package net.phantompig.soy.odm.physics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.phantompig.soy.odm.OdmLevelHelper;
import net.phantompig.soy.odm.OdmServerLevel;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OdmNode {
    public final UUID uuid;

    public Vec3 position, velocity;

    @Nullable
    public Player owner;

    public OdmServerLevel odmLevel;

    public float size = 0.2f;

    public boolean stuck = false;

    public OdmNode nextNode, lastNode;

    public String type = "node";

    public OdmNode(OdmServerLevel odmLevel, UUID uuid, @Nullable Player owner, Vec3 position, Vec3 velocity) {
        this.odmLevel = odmLevel;
        this.owner = owner;
        this.uuid = uuid;
        this.position = position;
        this.velocity = velocity;
    }
    public OdmNode(OdmServerLevel odmLevel, @Nullable Player owner, Vec3 position, Vec3 velocity) {
        this(odmLevel, UUID.randomUUID(), owner, position, velocity);
    }

    public OdmNode(OdmServerLevel odmLevel, Vec3 position) {
        this(odmLevel, null, position, Vec3.ZERO);
    }

    public void tick() {
        applySpringPhysics();

        this.velocity = this.velocity.scale(0.9);
        final double vsqr = this.velocity.lengthSqr();
        double steps = vsqr == 0 ? 1 : Math.sqrt(vsqr) / this.size;
        Vec3 newPosition = this.position;
        VoxelShape hitBlockShape = null;
        raymarch: for (int i = 0; i <= steps; i++) {
            newPosition = newPosition.add(this.velocity.scale(1 / steps));
            for (BlockCollisions<Tuple<BlockPos.MutableBlockPos, VoxelShape>> it = new BlockCollisions<>(this.odmLevel.level, owner, this.getCollisionBox(newPosition), false, Tuple::new); it.hasNext(); ) {
                Tuple<BlockPos.MutableBlockPos, VoxelShape> tuple = it.next();
                hitBlockShape = tuple.getB();
                break raymarch;
            }
        }
        if (hitBlockShape != null) {
            if (!this.stuck) this.stuck = true;
            this.position = hitBlockShape.closestPointTo(this.position).orElseGet(() -> this.position);
            this.velocity = new Vec3(0, 0, 0);
        } else {
            if (this.stuck) this.stuck = false;
            if (odmLevel.level.isFluidAtPosition(this.getBlockPos(), (fluidState) -> fluidState.is(FluidTags.LAVA))) {
                this.remove();
            } else if (odmLevel.level.isFluidAtPosition(this.getBlockPos(), (fluidState) -> fluidState.is(FluidTags.WATER))) {
                this.velocity = this.velocity.scale(0.75f);
            }
            this.position = newPosition;
            this.velocity = this.velocity.add(OdmServerLevel.GRAVITY);
        }

        if (this.lastNode != null) {
            final double distance = this.position.distanceTo(this.lastNode.position);
            steps = distance / this.size;
            final Vec3 delta = this.position.subtract(this.lastNode.position).normalize().scale(-this.size);
            newPosition = this.position;
            for (int i = 0; i <= steps; i++) {
                newPosition = newPosition.add(delta);

                PlayerUtil.spawnParticleForAll(
                        this.odmLevel.level,
                        32,
                        ParticleTypes.FLAME,
                        true,
                        newPosition.x,
                        newPosition.y,
                        newPosition.z,
                        0,
                        0,
                        0,
                        0,
                        1
                );

                if (new BlockCollisions<>(this.odmLevel.level, owner, this.getCollisionBox(newPosition), false, Tuple::new).hasNext()) {
                    if (newPosition.closerThan(this.position, 1.5) || newPosition.closerThan(this.lastNode.position, 1.5)) continue;
                    OdmNode newNode = OdmLevelHelper.addNodeAt(this.odmLevel.level, this.owner, newPosition);
                    OdmNode oldLast = this.lastNode;
                    oldLast.nextNode = newNode;
                    this.lastNode = newNode;
                    newNode.nextNode = this;
                    newNode.lastNode = oldLast;
                }
            }
        }

        PlayerUtil.spawnParticleForAll(
                this.odmLevel.level,
                32,
                ParticleTypes.ELECTRIC_SPARK,
                true,
                this.position.x,
                this.position.y,
                this.position.z,
                0,
                0,
                0,
                0,
                1
        );
    }

    public void applySpringPhysics() {
        if (this.nextNode != null) {
            this.velocity = this.velocity.add(this.nextNode.position.subtract(this.position).scale(0.06));
        }
        if (this.lastNode != null) {
            this.velocity = this.velocity.add(this.lastNode.position.subtract(this.position).scale(0.06));
        }
    }

    public AABB getCollisionBox() {
        return getCollisionBox(this.position);
    }
    public AABB getCollisionBox(Vec3 position) {
        return AABB.ofSize(position, size, size, size);
    }

    public BlockPos getBlockPos() {
        return BlockPos.containing(this.position.x, this.position.y, this.position.z);
    }

    public final void remove() {
        this.odmLevel.removeNode(this.uuid);
    }

    public void onAdd() { // it will already exist within the level node list when this method is called

    }
    public void onRemove() { // it will not exist within the level node list when this method is called
        if (this.nextNode != null && this.lastNode != null) {
            this.nextNode.lastNode = this.lastNode;
            this.lastNode.nextNode = this.nextNode;
        } else {
            if (this.nextNode != null) {
                this.nextNode.lastNode = null;
            }
            if (this.lastNode != null) {
                this.lastNode.nextNode = null;
            }
        }
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putString("Type", this.type);

        CompoundTag vec = new CompoundTag();
        vec.putDouble("x", this.position.x);
        vec.putDouble("y", this.position.y);
        vec.putDouble("z", this.position.z);
        tag.put("Position", vec);

        vec = new CompoundTag();
        vec.putDouble("x", this.velocity.x);
        vec.putDouble("y", this.velocity.y);
        vec.putDouble("z", this.velocity.z);
        tag.put("Velocity", vec);

        tag.putUUID("UUID", this.uuid);
        if (this.owner != null) tag.putUUID("OwnerUUID", this.owner.getUUID());

        if (this.nextNode != null) tag.putUUID("NextNode", this.nextNode.uuid);
        if (this.lastNode != null) tag.putUUID("LastNode", this.lastNode.uuid);

        return tag;
    }
    public static OdmNode fromTag(OdmServerLevel level, CompoundTag tag) {
        switch (tag.getString("Type")) {
            case "hook" -> {
                return OdmHookNode.fromTag(level, tag);
            }
            case "player" -> {
                return OdmPlayerNode.fromTag(level, tag);
            }
        }

        CompoundTag position = tag.getCompound("Position");
        CompoundTag velocity = tag.getCompound("Velocity");
        OdmNode node = new OdmNode(
                level,
                tag.getUUID("UUID"),
                tag.contains("OwnerUUID") ? level.level.getPlayerByUUID(tag.getUUID("OwnerUUID")) : null,
                new Vec3(position.getFloat("x"), position.getFloat("y"), position.getFloat("z")),
                new Vec3(velocity.getFloat("x"), velocity.getFloat("y"), velocity.getFloat("z"))
        );
        if (tag.contains("NextNode")) {
            node.nextNode = level.getNode(tag.getUUID("NextNode"));
        }
        if (tag.contains("LastNode")) {
            node.lastNode = level.getNode(tag.getUUID("LastNode"));
        }
        return node;
    }
}
