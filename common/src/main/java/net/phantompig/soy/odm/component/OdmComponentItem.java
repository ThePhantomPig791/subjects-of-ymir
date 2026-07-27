package net.phantompig.soy.odm.component;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.phantompig.soy.item.GasHoldingItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OdmComponentItem extends GasHoldingItem {
    public final OdmComponent odmComponent;

    public OdmComponentItem(Properties properties, OdmComponent component) {
        super(properties, component.gasCapacity());
        this.odmComponent = component;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.odmComponent.gasCapacity() != 0 && super.isBarVisible(stack);
    }

    public int consume(ItemStack stack, int amount) {
        int remaining = get(stack);
        int newAmount = remaining - amount;
        this.set(stack, Math.max(newAmount, 0));
        return Math.max(0, -newAmount);
    }

    public boolean canStrafe(Direction direction) {
        return this.odmComponent.gasStrafeStrength().containsKey(direction);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, components, isAdvanced);
        components.add(Component.translatable("tooltip.subjects_of_ymir.odm_component"));
    }
}
