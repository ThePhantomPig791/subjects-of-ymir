package net.phantompig.soy.item;

import java.awt.*;

public class GasHoldingItem extends DataHoldingItem {
    public GasHoldingItem(Properties properties, int max) {
        super(properties, "Gas", max, new Color(150, 211, 214));
    }
}
