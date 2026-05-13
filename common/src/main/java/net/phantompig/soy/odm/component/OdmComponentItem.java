package net.phantompig.soy.odm.component;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.item.GasHoldingItem;

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
}
