package net.phantompig.soy.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.network.TitanAttackMessage;

@Environment(EnvType.CLIENT) // duh
public class ClientCombatSystem {
    public final LocalPlayer player;

    public byte cooldown;

    public ClientCombatSystem(LocalPlayer player) {
        this.player = player;
    }

    public void attack() {
        SoyNetwork.NETWORK.sendToServer(new TitanAttackMessage());
    }
}
