package net.phantompig.soy.odm.physics;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.odm.OdmServerLevel;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OdmPlayerNode extends OdmNode {
    public OdmPlayerNode(OdmServerLevel odmLevel, UUID uuid, @Nullable Player owner, Vec3 position, Vec3 velocity) {
        super(odmLevel, uuid, owner, position, velocity);
        this.type = "player";
    }
    public OdmPlayerNode(OdmServerLevel odmLevel, @Nullable Player owner, Vec3 position, Vec3 velocity) {
        this(odmLevel, UUID.randomUUID(), owner, position, velocity);
    }

    @Override
    public void tick() {
        if (this.owner != null) this.position = this.owner.position().add(0, 1, 0);

        PlayerUtil.spawnParticleForAll(
                this.odmLevel.level,
                32,
                ParticleTypes.GLOW,
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

    public static OdmPlayerNode fromTag(OdmServerLevel level, CompoundTag tag) {
        CompoundTag position = tag.getCompound("Position");
        CompoundTag velocity = tag.getCompound("Velocity");
        OdmPlayerNode node = new OdmPlayerNode(
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
