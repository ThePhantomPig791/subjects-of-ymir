package net.phantompig.soy.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.network.TitanAttackMessage;

@Environment(EnvType.CLIENT) // duh
public class ClientCombatSystem {
    public final LocalPlayer player;

    public int nextStageTimer;

    public ClientCombatSystem(LocalPlayer player) {
        this.player = player;
        this.nextStageTimer = 0;
    }

    public void attack() {
        SoyNetwork.NETWORK.sendToServer(new TitanAttackMessage());
    }

    public void tick() {
        if (this.nextStageTimer > 0) this.nextStageTimer--;
    }
}
