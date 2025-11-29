package net.phantompig.soy;

import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.command.TitanCommand;
import net.phantompig.soy.power.TitanPowerProvider;
import net.phantompig.soy.power.ability.SubjectsOfYmirAbilities;
import net.phantompig.soy.titan.TitanRegistry;
import net.threetag.palladiumcore.event.CommandEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectsOfYmir {

    public static final String MOD_ID = "subjects_of_ymir";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        SubjectsOfYmirAbilities.ABILITIES.register();

        TitanRegistry.init();
        TitanPowerProvider.init();
        SubjectsOfYmirAbilities.init();

        CommandEvents.REGISTER.register((dispatcher, selection) -> {
            TitanCommand.register(dispatcher);
        });
    }

    public static ResourceLocation rsrc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
