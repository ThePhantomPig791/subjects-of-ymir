package net.phantompig.soy.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.combat.ClientCombatHolder;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

public class SetNextAttackStageTimerMessage extends MessageS2C {
    public final int ticks;

    public SetNextAttackStageTimerMessage(int ticks) {
        this.ticks = ticks;
    }

    public SetNextAttackStageTimerMessage(FriendlyByteBuf buf) {
        ticks = buf.readInt();
    }

    @Override
    public @NotNull MessageType getType() {
        return SoyNetwork.SET_NEXT_ATTACK_STAGE_TIMER;
    }

    @Override
    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(ticks);
    }

    @Override
    public void handle(MessageContext messageContext) {
        SubjectsOfYmir.LOGGER.info("recierved packed");
        if (Minecraft.getInstance().level == null) return;
        SubjectsOfYmir.LOGGER.info("recierved packed 2");
        handleClient(messageContext);
    }

    public void handleClient(MessageContext messageContext) {
        SubjectsOfYmir.LOGGER.info("check: {}", Minecraft.getInstance() instanceof ClientCombatHolder com);
        if (Minecraft.getInstance().player == null || !(Minecraft.getInstance() instanceof ClientCombatHolder com)) return;
        com.soy$getCombatSystem().nextStageTimer = ticks;
        SubjectsOfYmir.LOGGER.info("set timer to {}", com.soy$getCombatSystem().nextStageTimer);
    }
}
