package net.phantompig.soy.forge.compat.curios;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.compat.curiostrinkets.SoyCuriosTrinketsUtil;
import net.threetag.palladiumcore.util.Platform;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Map;

public class SoyCuriosUtil extends SoyCuriosTrinketsUtil {
    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void write(LivingEntity entity, CompoundTag tag) {
        if (Platform.isModLoaded("curios")) {
            CuriosApi.getCuriosInventory(entity).ifPresent(handler -> tag.put("CuriosInventory", handler.writeTag()));
        }
    }

    @Override
    public void read(LivingEntity entity, CompoundTag tag) {
        if (Platform.isModLoaded("curios")) {
            CuriosApi.getCuriosInventory(entity).ifPresent(handler -> handler.readTag(tag.getCompound("CuriosInventory")));
        }
    }

    @Override
    public void clear(LivingEntity entity) {
        if (Platform.isModLoaded("curios")) {
            CuriosApi.getCuriosInventory(entity).ifPresent(handler -> handler.setCurios(Map.of()));
        }
    }
}
