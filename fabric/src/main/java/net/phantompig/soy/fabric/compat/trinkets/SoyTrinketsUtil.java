package net.phantompig.soy.fabric.compat.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.compat.curiostrinkets.SoyCuriosTrinketsUtil;
import net.threetag.palladiumcore.util.Platform;

public class SoyTrinketsUtil extends SoyCuriosTrinketsUtil {
    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void write(LivingEntity entity, CompoundTag tag) {
        if (Platform.isModLoaded("trinkets")) {
            TrinketsApi.getTrinketComponent(entity).ifPresent(com -> com.writeToNbt(tag));
        }
    }

    @Override
    public void read(LivingEntity entity, CompoundTag tag) {
        if (Platform.isModLoaded("trinkets")) {
            TrinketsApi.getTrinketComponent(entity).ifPresent(com -> com.readFromNbt(tag));
        }
    }

    @Override
    public void clear(LivingEntity entity) {
        if (Platform.isModLoaded("trinkets")) {
            TrinketsApi.getTrinketComponent(entity).ifPresent(com -> com.getAllEquipped().forEach(tuple -> tuple.getA().inventory().clearContent()));
        }
    }
}
