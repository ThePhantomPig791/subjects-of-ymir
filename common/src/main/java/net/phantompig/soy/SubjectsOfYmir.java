package net.phantompig.soy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.block.SoyBlockTags;
import net.phantompig.soy.block.SoyBlocks;
import net.phantompig.soy.command.TitanCommand;
import net.phantompig.soy.entity.SoyDamageSources;
import net.phantompig.soy.entity.SoyDamageTypeTags;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.entity.SoyPaintingVariants;
import net.phantompig.soy.feature.SoyFeatures;
import net.phantompig.soy.item.OdmAttachableAddonArmorItem;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.menu.SoyMenus;
import net.phantompig.soy.network.SoyNetwork;
import net.phantompig.soy.odm.OdmLevelHelper;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.player.SoyServerPlayerExtension;
import net.phantompig.soy.power.TitanPowerProvider;
import net.phantompig.soy.power.ability.ShedAbility;
import net.phantompig.soy.power.ability.SoyAbilities;
import net.phantompig.soy.power.condition.SoyConditionSerializers;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.recipe.SoyRecipeTypes;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.stat.SoyStats;
import net.phantompig.soy.titan.TitanInstance;
import net.phantompig.soy.titan.TitanRegistry;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladiumcore.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectsOfYmir {

    public static final String MOD_ID = "subjects_of_ymir";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceLocation SHIFTER_POWER = rsrc("shifter");

    public static void init() {
        LOGGER.info("Subjects of Ymir initializing...");

        SoyAbilities.ABILITIES.register();
        SoyConditionSerializers.CONDITION_SERIALIZERS.register();

        TitanRegistry.init();
        TitanPowerProvider.init();
        SoyProperties.init();

        SoySounds.init();
        SoyBlocks.init();
        SoyItems.init();
        SoyEntities.init();
        SoyBlockTags.init();
        SoyParticles.init();
        SoyMenus.init();
        SoyRecipeTypes.init();
        SoyFeatures.init();
        SoyStats.init();
        SoyNetwork.init();
        SoyDamageSources.init();
        SoyPaintingVariants.init();


        CommandEvents.REGISTER.register((dispatcher, selection) -> {
            TitanCommand.register(dispatcher);
        });

        LivingEntityEvents.HURT.register(((entity, damageSource, amount) -> {
            if (damageSource.is(SoyDamageTypeTags.CANNOT_CAUSE_SHIFT)
                    || !(entity instanceof SoyPlayerExtension soy)
                    || amount.get() < soy.getTitanInstance().getDamageThreshold()) {
                return EventResult.pass();
            }
            var titanInstance = soy.getTitanInstance();
            if (titanInstance.getProgress() != 0) {
                // hardcoded armored titan behavior for losing armor on hurt
                if (titanInstance.titan != null && titanInstance.titan.id.equals(rsrc("armored"))) {
                    Power power = PowerManager.getInstance(entity.level()).getPower(rsrc("titan/armored"));
                    AbilityInstance ability = PowerManager.getPowerHandler(entity).get().getPowerHolder(power).getAbilities().get("shed");
                    int subAmount = (int) Math.max(0, amount.get() / 120 * (30 - entity.getHealth()));
                    if (subAmount >= 1) {
                        ability.setUniqueProperty(ShedAbility.VALUE, ability.getProperty(ShedAbility.VALUE) - subAmount);
                        ShedAbility.update(entity, ability);
                    }
                }
            } else {
                titanInstance.canShiftTicks += (int) (amount.get() * 15);
            }
            return EventResult.pass();
        }));

        LivingEntityEvents.TICK.register((entity -> {
            if (entity instanceof SoyPlayerExtension soy && soy.getTitanInstance().titan != null) {
                soy.getTitanInstance().tick();
                if (entity instanceof ServerPlayer player && soy.getTitanInstance().memoryManager != null) soy.getTitanInstance().memoryManager.tick(player);
            }
            if (!entity.level().isClientSide && entity instanceof SoyServerPlayerExtension serverExt) {
                serverExt.soy$getCombatSystem().tick();
            }
        }));

        LivingEntityEvents.DEATH.register(((livingEntity, damageSource) -> {
            if (livingEntity instanceof SoyPlayerExtension ext && ext.getTitanInstance().titan != null && livingEntity instanceof Player player) {
                ext.getTitanInstance().setMarksTimer(0);
                ext.getTitanInstance().setCharge(0);
                ext.getTitanInstance().setProgress(0);
                if (++ext.getTitanInstance().deaths >= 13) {
                    player.drop(TitanInstance.toSpineIem(ext.getTitanInstance()), true, true);
                    TitanInstance.clearTitanFor(livingEntity);
                }
            }
            ItemStack leggings = livingEntity.getItemBySlot(EquipmentSlot.LEGS);
            if (leggings.getItem() instanceof OdmAttachableAddonArmorItem odm) {
                OdmLevelHelper.removeNode(livingEntity.level(), odm.getLeftHook(leggings));
                OdmLevelHelper.removeNode(livingEntity.level(), odm.getRightHook(leggings));
                odm.removeLeftHook(leggings);
                odm.removeRightHook(leggings);
            }
            return EventResult.pass();
        }));

        PlayerEvents.CLONE.register(((oldPlayer, newPlayer, wasDeath) -> {
            if (oldPlayer instanceof SoyPlayerExtension oldExt && newPlayer instanceof SoyPlayerExtension newExt) {
                newExt.setTitanInstance(TitanInstance.fromTag(newPlayer, oldExt.getTitanInstance().toTag()));
            }
        }));

        ChatEvents.SERVER_SUBMITTED.register((player, raw, message) -> {
            if (player instanceof SoyPlayerExtension ext) {
                ext.getTitanInstance().getMemoryManager().onChat(player, raw);
            }
            return EventResult.pass();
        });
    }

    public static void setup() {
        SoyStats.setup();

        SubjectsOfYmir.LOGGER.info("Subjects of Ymir setup complete");
    }

    public static ResourceLocation rsrc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
