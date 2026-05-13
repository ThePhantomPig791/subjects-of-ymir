package net.phantompig.soy.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.item.OdmHandleItem;
import net.threetag.palladiumcore.network.MessageC2S;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

public class OdmHandlePressMessage extends MessageC2S {
    final boolean right;

    public OdmHandlePressMessage(boolean right) {
        this.right = right;
    }

    public OdmHandlePressMessage(FriendlyByteBuf buf) {
        this.right = buf.readBoolean();
    }

    @Override
    public @NotNull MessageType getType() {
        return SoyNetwork.ODM_HANDLE_PRESS;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(right);
    }

    @Override
    public void handle(MessageContext context) {
        ServerPlayer player = context.getPlayer();
        boolean mainhand = player.getMainArm() == HumanoidArm.RIGHT == right;
        ItemStack stack = mainhand ? player.getMainHandItem() : player.getOffhandItem();
        if (stack.getItem() instanceof OdmHandleItem odm) {
            odm.handlePress(player, stack, right);
        }
    }
}
