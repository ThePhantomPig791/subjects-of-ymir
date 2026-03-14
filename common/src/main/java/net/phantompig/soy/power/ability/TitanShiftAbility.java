package net.phantompig.soy.power.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.phantompig.soy.player.SoyPlayerExtension;
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
        } else {
            if (charge >= 1) {
                if (progress < titanInstance.titan.maxProgress && !titanInstance.forceUnshift) {
                    if (progress < 0) titanInstance.setProgress(0);
                    if (titanInstance.getProgress() == 0) {
                        // first shifting tick
                        titanInstance.titan.startShift(entity, charge);
                    }

                    // each shifting tick
                    titanInstance.titan.tickDuringShift(entity, progress, charge);
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

        if (titanInstance.forceUnshift && titanInstance.getProgress() > 0) {
            titanInstance.forceUnshift = false;

            titanInstance.titan.unshift(entity);
        }
    }


    @Override
    public String getDocumentationDescription() {
        return "Shifts into a titan; charges up while enabled then shifts once disabled";
    }
}
