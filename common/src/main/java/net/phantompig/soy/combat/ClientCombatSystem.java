package net.phantompig.soy.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.network.OdmStartAttackMessage;
import net.phantompig.soy.network.OdmStopAttackMessage;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.network.TitanAttackMessage;

@Environment(EnvType.CLIENT) // duh
public class ClientCombatSystem {
    public final LocalPlayer player;

    public int nextStageTimer;

    public boolean heldLeftClick;

    public ClientCombatSystem(LocalPlayer player) {
        this.player = player;
        this.nextStageTimer = 0;
    }

    public void attack() {
        SoyNetwork.NETWORK.sendToServer(new TitanAttackMessage());
    }

    public void startHeldAttack() {
        if (!this.player.getMainHandItem().is(SoyItems.BLADE_HANDLE.get()) || !this.player.getOffhandItem().is(SoyItems.BLADE_HANDLE.get())) return;
        this.heldLeftClick = true;
        SoyNetwork.NETWORK.sendToServer(new OdmStartAttackMessage());
    }

    public void stopHeldAttack() {
        this.heldLeftClick = false;
        SoyNetwork.NETWORK.sendToServer(new OdmStopAttackMessage());
    }

    public void tick() {
        if (this.nextStageTimer > 0) this.nextStageTimer--;
    }
}