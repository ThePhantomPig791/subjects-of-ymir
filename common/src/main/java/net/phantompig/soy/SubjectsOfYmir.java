package net.phantompig.soy;

import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.block.SoyBlockTags;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.command.TitanCommand;
import net.phantompig.soy.feature.SoyFeatures;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.menu.SoyMenus;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.recipe.SoyRecipeTypes;
import net.phantompig.soy.sound.SoySounds;
import net.threetag.palladiumcore.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectsOfYmir {

    public static final String MOD_ID = "subjects_of_ymir";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Subjects of Ymir initializing...");

        SoyBlocks.init();
        SoyItems.init();
        SoySounds.init();
        SoyBlockTags.init();
        SoyParticles.init();
        SoyRecipeTypes.init();
        SoyMenus.init();
        SoyFeatures.init();


        CommandEvents.REGISTER.register((dispatcher, selection) -> {
            TitanCommand.register(dispatcher);
        });
    }

    public static ResourceLocation rsrc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
