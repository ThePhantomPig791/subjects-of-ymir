package net.phantompig.soy.odm.physics;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
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
    public OdmPlayerNode(OdmNode node) {
        this(node.odmLevel, node.uuid, node.owner, node.position, node.velocity);
    }

    @Override
    public void tick() { // TODO see if i can reduce the player's air resistance and also unlock the camera ? so you can go upside down idk it could feel cool. maybe these would take effect when you wear the uniform leggings
        if (this.owner != null) {
            this.position = this.owner.position().add(0, 1, 0);

            if (hookStuck()) {
                this.owner.addDeltaMovement(this.nextNode.position.subtract(this.position).scale(OdmServerLevel.SPRING_CONSTANT * (this.position.distanceTo(this.nextNode.position) - this.distanceToNextNode)));
                this.owner.setDeltaMovement(this.owner.getDeltaMovement().scale(OdmServerLevel.AIR_RESISTANCE));
                if (this.owner instanceof ServerPlayer sp) {
                    sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
                }
            }
        } else {
            this.remove();
        }

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

    private boolean hookStuck() {
        OdmNode next = this.nextNode;
        while (next != null) {
            if (next.stuck) return true;
            next = next.nextNode;
        }
        return false;
    }

    public static OdmPlayerNode fromTag(OdmServerLevel level, CompoundTag tag) {
        return new OdmPlayerNode(OdmNode.fromTag(level, tag));
    }
}
