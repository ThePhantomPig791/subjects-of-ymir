package net.phantompig.soy.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.util.AnimationUtil;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TitanAttackAnimationMessage extends MessageS2C {
    public final UUID playerUuid;
    public final String id;

    public TitanAttackAnimationMessage(Player player, String id) {
        this.playerUuid = player.getUUID();
        this.id = id;
    }

    public TitanAttackAnimationMessage(FriendlyByteBuf buf) {
        playerUuid = buf.readUUID();
        id = buf.readUtf();
    }

    @Override
    public @NotNull MessageType getType() {
        return SoyNetwork.TITAN_ATTACK_ANIMATION;
    }

    @Override
    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.playerUuid);
        friendlyByteBuf.writeUtf(this.id);
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (Minecraft.getInstance().level == null) return;
        handleClient(messageContext);
    }

    public void handleClient(MessageContext messageContext) {
        var player = Minecraft.getInstance().player;
        var entities = Minecraft.getInstance().level.getEntities((Player) null, player.getBoundingBox().inflate(150), pl -> pl.getUUID().equals(this.playerUuid));
        if (entities.isEmpty()) return;
        if (entities.get(0) instanceof AbstractClientPlayer pl) AnimationUtil.playTitanAnimation(pl, id);
        // TIL there are two different types of client players: LocalPlayer and RemotePlayer
    }
}
