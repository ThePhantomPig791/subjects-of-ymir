package net.phantompig.soy.power.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.player.SubjectsOfYmirPlayerExtension;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.icon.ItemIcon;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;
import net.threetag.palladium.util.property.SyncType;

public class TitanShiftAbility extends Ability {
    public static final PalladiumProperty<Integer> MAX_CHARGE = new IntegerProperty("max_charge").configurable("Maximum charge that can be reached before shifting");
    public static final PalladiumProperty<Integer> CHARGE = new IntegerProperty("charge").sync(SyncType.EVERYONE).disablePersistence();

    public TitanShiftAbility() {
        this.withProperty(MAX_CHARGE, 50);

        this.withProperty(ICON, new ItemIcon(Items.BONE));
        this.withProperty(HIDDEN_IN_BAR, false);
    }

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(CHARGE, 0);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        var titanInstance = ((SubjectsOfYmirPlayerExtension) entity).getTitanInstance();
        if (titanInstance.titan == null) return;
        int progress = titanInstance.getProgress();
        int charge = entry.getProperty(CHARGE);
        final int maxCharge = entry.getProperty(MAX_CHARGE);

        if (enabled && charge < maxCharge) {
            entry.setUniqueProperty(CHARGE, ++charge);
            SubjectsOfYmir.LOGGER.info(String.valueOf(charge));
        } else {
            if (charge >= 1) {
                SubjectsOfYmir.LOGGER.info("pr {}" , titanInstance.getProgress());
                if (progress < titanInstance.titan.maxProgress) {
                    if (progress < 0) titanInstance.setProgress(0);
                    if (titanInstance.getProgress() == 0) {
                        // first shifting tick
                        titanInstance.titan.startShift(entity, charge);
                    }

                    // each shifting tick
                    titanInstance.setProgress(++progress);
                } else {
                    // completed shift
                    titanInstance.titan.completedShift(entity, charge);

                    entry.setUniqueProperty(CHARGE, 0);
                }
            }
        }

        if (progress == titanInstance.titan.maxProgress) {
            titanInstance.titan.tick(entity);
        }
    }


    @Override
    public String getDocumentationDescription() {
        return "Shifts into a titan; charges up while enabled then shifts once disabled";
    }
}
