package net.phantompig.soy.titan;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import oshi.annotation.concurrent.Immutable;

@Immutable
public class Titan {
    public final ResourceLocation id;
    public final int health;

    public Titan(ResourceLocation id, int health) {
        this.id = id;
        this.health = health;
    }

    public static Titan fromJson(ResourceLocation id, JsonObject json) {
        return new Titan(id, GsonHelper.getAsInt(json, "health", 20));
    }
}
