package net.phantompig.soy.mixin.client;

import net.threetag.palladium.client.screen.power.PowerTab;
import net.threetag.palladium.power.IPowerHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PowerTab.class)
public interface PowerTabAccessor {
    @Accessor("powerHolder")
    IPowerHolder soy$getIPowerHolder();
}
