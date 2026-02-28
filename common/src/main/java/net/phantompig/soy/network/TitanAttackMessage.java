package net.phantompig.soy.network;

import net.minecraft.network.FriendlyByteBuf;
import net.phantompig.soy.player.SoyServerPlayerExtension;
import net.threetag.palladiumcore.network.MessageC2S;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

public class TitanAttackMessage extends MessageC2S {
    public TitanAttackMessage() {
    }

    public TitanAttackMessage(FriendlyByteBuf buf) {
    }

    @Override
    public @NotNull MessageType getType() {
        return SoyNetwork.TITAN_ATTACK;
    }

    @Override
    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (messageContext.getPlayer().level().isClientSide()) return;
        handleServer(messageContext);
    }


    public void handleServer(MessageContext messageContext) {
        if (!(messageContext.getPlayer() instanceof SoyServerPlayerExtension ext)) return;
        ext.soy$getCombatSystem().attack();
    }
}
