package net.phantompig.soy.power.ability;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.TitanInstance;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.icon.ItemIcon;

public class TitanShiftAbility extends Ability {

    public TitanShiftAbility() {
        this.withProperty(ICON, new ItemIcon(Items.BONE));
        this.withProperty(HIDDEN_IN_BAR, false);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        var titanInstance = ((SoyPlayerExtension) entity).getTitanInstance();
        if (titanInstance.titan == null) return;
        int progress = titanInstance.getProgress();
        int charge = titanInstance.getCharge();
        final int maxCharge = titanInstance.titan.maxCharge;

        if (enabled && charge < maxCharge) {
            titanInstance.setCharge(++charge);
            // SubjectsOfYmir.LOGGER.info("charge {}", charge);
        } else {
            if (charge >= 1) {
                // SubjectsOfYmir.LOGGER.info("pr {}" , titanInstance.getProgress());
                if (progress < titanInstance.titan.maxProgress && !titanInstance.forceUnshift) {
                    if (progress < 0) titanInstance.setProgress(0);
                    if (titanInstance.getProgress() == 0) {
                        // first shifting tick
                        titanInstance.titan.startShift(entity, charge);
                        titanInstance.startScaleChange();
                        titanInstance.setDecay(TitanInstance.START_CORPSE_DECAY);
                        titanInstance.canShiftTicks = 0;

                        if (entity instanceof Player player) {
                            titanInstance.playerInventory = player.getInventory().save(new ListTag());
                            player.getInventory().clearContent();
                        }
                    }

                    // each shifting tick
                    titanInstance.setProgress(++progress);
                    titanInstance.titan.tickDuringShift(entity, charge);
                } else {
                    // completed shift
                    titanInstance.titan.completedShift(entity, charge);
                    titanInstance.setCharge(0);
                }
            }
        }

        if (progress == titanInstance.titan.maxProgress) {
            titanInstance.titan.tick(entity);
        }

        // triggered from the unshift ability. i moved it here so you can "cancel" mid-shift, the other option was to make another property for tracking cancelled shifts
        if (titanInstance.forceUnshift && titanInstance.getProgress() > 0) {
            titanInstance.forceUnshift = false;

            titanInstance.titan.unshift(entity);

            if (entity instanceof Player player && titanInstance.playerInventory != null) {
                player.getInventory().dropAll();
                player.getInventory().load(titanInstance.playerInventory);
                titanInstance.playerInventory = null;
            }
        }
    }


    @Override
    public String getDocumentationDescription() {
        return "Shifts into a titan; charges up while enabled then shifts once disabled";
    }
}
