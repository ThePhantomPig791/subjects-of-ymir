package net.phantompig.soy;

import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.command.TitanCommand;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.power.TitanPowerProvider;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.phantompig.soy.power.condition.SoyConditionSerializers;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.titan.TitanRegistry;
import net.threetag.palladiumcore.event.CommandEvents;
import net.threetag.palladiumcore.event.EventResult;
import net.threetag.palladiumcore.event.LivingEntityEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectsOfYmir {

    public static final String MOD_ID = "subjects_of_ymir";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        SoyAbilities.ABILITIES.register();
        SoyConditionSerializers.CONDITION_SERIALIZERS.register();

        TitanRegistry.init();
        TitanPowerProvider.init();
        SoyProperties.init();

        CommandEvents.REGISTER.register((dispatcher, selection) -> {
            TitanCommand.register(dispatcher);
        });

        LivingEntityEvents.DEATH.register((entity, source) -> {
            if (!(entity instanceof SoyPlayerExtension soy)) return EventResult.pass();
            var titanInstance = soy.getTitanInstance();
            if (titanInstance.titan == null || titanInstance.getProgress() == 0) return EventResult.pass();
            titanInstance.forceUnshift = true;
            return EventResult.cancel();
        });

        SoyEntities.init();
        SoySounds.SOUNDS.register();
    }

    public static ResourceLocation rsrc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
