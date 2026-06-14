package net.phantompig.soy.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.phantompig.soy.client.renderer.ScreenShakeManager;
import net.threetag.palladium.util.Easing;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ScreenShakeMessage extends MessageS2C {
    public int durationMillis;
    public Vector3f strength;
    @Nullable
    public Easing easing;

    public ScreenShakeMessage(int durationMillis, Vector3f strength, @Nullable Easing easing) {
        this.durationMillis = durationMillis;
        this.strength = strength;
        this.easing = easing;
    }

    public ScreenShakeMessage(FriendlyByteBuf buf) {
        this.durationMillis = buf.readInt();
        this.strength = new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        String easing = buf.readUtf();
        if (!easing.equals("null")) this.easing = Easing.fromString(easing);
    }

    @Override
    public @NotNull MessageType getType() {
        return SoyNetwork.SCREEN_SHAKE;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.durationMillis);
        buf.writeFloat(this.strength.x);
        buf.writeFloat(this.strength.y);
        buf.writeFloat(this.strength.z);
        buf.writeUtf(this.easing == null ? "null" : this.easing.toString());
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (Minecraft.getInstance().level == null) return;
        handleClient(messageContext);
    }

    public void handleClient(MessageContext messageContext) {
        ScreenShakeManager.addScreenShake(this.durationMillis, this.strength, this.easing);
    }
}
