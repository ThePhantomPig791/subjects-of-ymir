package net.phantompig.soy.player;

import net.phantompig.soy.combat.ServerCombatSystem;
import org.jetbrains.annotations.NotNull;


public interface SoyServerPlayerExtension {
    @NotNull
    ServerCombatSystem soy$getCombatSystem();

    void soy$setCombatSystem(ServerCombatSystem combatSystem);
}
