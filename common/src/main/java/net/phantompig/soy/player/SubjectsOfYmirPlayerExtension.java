package net.phantompig.soy.player;

import net.phantompig.soy.titan.TitanInstance;
import org.jetbrains.annotations.NotNull;

public interface SubjectsOfYmirPlayerExtension {
    @NotNull
    TitanInstance getTitanInstance();

    void setTitanInstance(TitanInstance instance);
}
