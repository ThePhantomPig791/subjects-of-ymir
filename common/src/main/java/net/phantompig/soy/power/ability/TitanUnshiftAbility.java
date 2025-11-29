package net.phantompig.soy.power.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.phantompig.soy.player.SubjectsOfYmirPlayerExtension;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.util.icon.ItemIcon;
import net.threetag.palladium.util.property.BooleanProperty;
import net.threetag.palladium.util.property.PalladiumProperty;

public class TitanUnshiftAbility extends Ability {
    public static final PalladiumProperty<Boolean> SPAWN_SKELETON = new BooleanProperty("spawn_skeleton").configurable("If true, leaves behind a decaying titan body");

    public TitanUnshiftAbility() {
        this.withProperty(SPAWN_SKELETON, true);

        this.withProperty(ICON, new ItemIcon(Items.ROTTEN_FLESH));
        this.withProperty(HIDDEN_IN_BAR, false);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        var titanInstance = ((SubjectsOfYmirPlayerExtension) entity).getTitanInstance();
        if (titanInstance.titan == null) return;

        boolean forced = titanInstance.forceUnshift;

        if (enabled || forced) {
            titanInstance.progress = 0;
            titanInstance.forceUnshift = false;
        }
    }
}
