package net.phantompig.soy.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.phantompig.soy.combat.ServerCombatSystem;
import net.phantompig.soy.player.SoyServerPlayerExtension;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements SoyServerPlayerExtension {
    @Unique
    private ServerCombatSystem soy$combatSystem = new ServerCombatSystem((ServerPlayer) (Object) this);

    @Override
    public @NotNull ServerCombatSystem soy$getCombatSystem() {
        return this.soy$combatSystem;
    }

    @Override
    public void soy$setCombatSystem(ServerCombatSystem combatSystem) {
        this.soy$combatSystem = combatSystem;
    }
}
