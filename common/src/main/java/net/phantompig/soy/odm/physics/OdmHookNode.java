package net.phantompig.soy.odm.physics;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.odm.OdmServerLevel;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OdmHookNode extends OdmNode {
    public OdmHookNode(OdmServerLevel odmLevel, UUID uuid, @Nullable Player owner, Vec3 position, Vec3 velocity) {
        super(odmLevel, uuid, owner, position, velocity);
        this.type = "hook";
    }
    public OdmHookNode(OdmServerLevel odmLevel, @Nullable Player owner, Vec3 position, Vec3 velocity) {
        this(odmLevel, UUID.randomUUID(), owner, position, velocity);
    }

    public void tick() {
        super.tick();

        PlayerUtil.spawnParticleForAll(
                this.odmLevel.level,
                32,
                ParticleTypes.HEART,
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

    @Override
    public void applySpringPhysics() {
        if (!this.stuck) super.applySpringPhysics();
    }

    public static OdmHookNode fromTag(OdmServerLevel level, CompoundTag tag) {
        // i do hate to repeat this code but whatever. i can't really think of another way to do this type system
        // because this method needs to
        CompoundTag position = tag.getCompound("Position");
        CompoundTag velocity = tag.getCompound("Velocity");
        OdmHookNode node = new OdmHookNode(
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
