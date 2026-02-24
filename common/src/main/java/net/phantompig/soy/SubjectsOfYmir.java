package net.phantompig.soy;

import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.block.SoyBlockTags;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.command.TitanCommand;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.feature.SoyFeatures;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.menu.SoyMenus;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.player.SoyServerPlayerExtension;
import net.phantompig.soy.power.TitanPowerProvider;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.phantompig.soy.power.condition.SoyConditionSerializers;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.recipe.SoyRecipeTypes;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.stat.SoyStats;
import net.phantompig.soy.titan.TitanInstance;
import net.phantompig.soy.titan.TitanRegistry;
import net.threetag.palladiumcore.event.CommandEvents;
import net.threetag.palladiumcore.event.EventResult;
import net.threetag.palladiumcore.event.LivingEntityEvents;
import net.threetag.palladiumcore.event.PlayerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectsOfYmir {

    public static final String MOD_ID = "subjects_of_ymir";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Subjects of Ymir initializing...");

        SoyAbilities.ABILITIES.register();
        SoyConditionSerializers.CONDITION_SERIALIZERS.register();

        TitanRegistry.init();
        TitanPowerProvider.init();
        SoyProperties.init();

        SoyBlocks.init();
        SoyItems.init();
        SoyEntities.init();
        SoySounds.init();
        SoyBlockTags.init();
        SoyParticles.init();
        SoyMenus.init();
        SoyRecipeTypes.init();
        SoyFeatures.init();
        SoyStats.init();
        SoyNetwork.init();


        CommandEvents.REGISTER.register((dispatcher, selection) -> {
            TitanCommand.register(dispatcher);
        });

        LivingEntityEvents.HURT.register(((entity, damageSource, amount) -> {
            if (!(entity instanceof SoyPlayerExtension soy)) return EventResult.pass();
            var titanInstance = soy.getTitanInstance();
            if (titanInstance.titan == null || titanInstance.getProgress() != 0) return EventResult.pass();
            titanInstance.canShiftTicks += (int) (amount.get() * 15);
            return EventResult.pass();
        }));

        LivingEntityEvents.TICK.register((entity -> {
            if (entity instanceof SoyPlayerExtension soy && soy.getTitanInstance().titan != null) {
                soy.getTitanInstance().tick();
            }
            if (!entity.level().isClientSide && entity instanceof SoyServerPlayerExtension serverExt) {
                serverExt.soy$getCombatSystem().tick();
            }
        }));

        PlayerEvents.CLONE.register(((oldPlayer, newPlayer, wasDeath) -> {
            if (oldPlayer instanceof SoyPlayerExtension oldExt && newPlayer instanceof SoyPlayerExtension newExt) {
                newExt.setTitanInstance(TitanInstance.fromTag(newPlayer, oldExt.getTitanInstance().toTag()));
            }
        }));
    }

    public static ResourceLocation rsrc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
