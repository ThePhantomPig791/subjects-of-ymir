package net.phantompig.soy.player;

import net.phantompig.soy.titan.TitanInstance;
import org.jetbrains.annotations.NotNull;

public interface SoyPlayerExtension {
    @NotNull
    TitanInstance soy$getTitanInstance();

    void soy$setTitanInstance(TitanInstance instance);
}
