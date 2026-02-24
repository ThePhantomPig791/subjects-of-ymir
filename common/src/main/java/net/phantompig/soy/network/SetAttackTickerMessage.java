package net.phantompig.soy.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

public class SetAttackTickerMessage extends MessageS2C {
    public final int ticks;

    public SetAttackTickerMessage(int ticks) {
        this.ticks = ticks;
    }

    public SetAttackTickerMessage(FriendlyByteBuf buf) {
        ticks = buf.readInt();
    }

    @Override
    public @NotNull MessageType getType() {
        return SoyNetwork.SET_ATTACK_TICKER;
    }

    @Override
    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(ticks);
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (Minecraft.getInstance().level == null) return;
        handleClient(messageContext);
    }

    public void handleClient(MessageContext messageContext) {
        if (Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.attackStrengthTicker = ticks;

    }
}
