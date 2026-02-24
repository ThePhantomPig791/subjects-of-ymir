package net.phantompig.soy.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.Titan;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

public class UpdateTitanMessage extends MessageS2C {
    public final Titan titan;

    public UpdateTitanMessage(Titan instance) {
        this.titan = instance;
    }

    public UpdateTitanMessage(FriendlyByteBuf buf) {
        this.titan = Titan.fromNetwork(buf);
    }

    @Override
    public @NotNull MessageType getType() {
        return SoyNetwork.UPDATE_TITAN_INSTANCE;
    }

    @Override
    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        Titan.toNetwork(titan, friendlyByteBuf);
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (Minecraft.getInstance().level == null) return;
        handleClient(messageContext);
    }

    public void handleClient(MessageContext messageContext) {
        if (!(Minecraft.getInstance().player instanceof SoyPlayerExtension ext)) return;
        ext.getTitanInstance().titan = this.titan;
    }
}
