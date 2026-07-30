package net.phantompig.soy.forge.compat.curios;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.phantompig.soy.player.SoyPlayerExtension;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

public class SoyCuriosEvents {
    @SubscribeEvent
    public static void curioUnequip(CurioUnequipEvent event) {
        if (event.getEntity() instanceof SoyPlayerExtension ext && ext.getTitanInstance().getProgress() > 0) {
            event.setResult(Event.Result.DENY);
        }
    }
}
