package net.phantompig.soy.odm.component;

import net.minecraft.core.Direction;
import oshi.annotation.concurrent.Immutable;

import java.util.HashMap;

@Immutable
public record OdmComponent(String name, OdmSlot slot, int gasCapacity, int bladeCapacity, HashMap<Direction, Float> gasStrafeStrength) {
    public String prettyName() {
        StringBuilder builder = new StringBuilder();
        for (String sub : name.split("_")) {
            builder.append(sub.substring(0, 1).toUpperCase().concat(sub.substring(1)).concat(" "));
        }
        return builder.toString();
    }
}
