package net.phantompig.soy.titan;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SubjectsOfYmirPlayerExtension;
import net.phantompig.soy.property.SubjectsOfYmirProperties;
import oshi.annotation.concurrent.Immutable;

import java.util.List;

@Immutable
public class Titan {
    public final ResourceLocation id;
    public final List<String> variants;
    public final int health;
    public final float scale;
    public final int maxProgress;

    private Titan(ResourceLocation id, List<String> variants, int health, float scale, int maxProgress) {
        this.id = id;
        this.variants = variants;
        this.health = health;
        this.scale = scale;
        this.maxProgress = maxProgress;
    }


    public void startShift(LivingEntity entity, int charge) {

    }

    public void completedShift(LivingEntity entity, int charge) {

    }

    public void tick(LivingEntity entity) {
        SubjectsOfYmirProperties.SCALE.set(entity, this.scale * ((SubjectsOfYmirPlayerExtension) entity).getTitanInstance().getProgress() / this.maxProgress);
    }


    public static Titan fromJson(ResourceLocation id, JsonObject json) {
        var builder = new TitanBuilder();
        builder.id = id;
        builder.variants = GsonHelper.isArrayNode(json, "variants") ? GsonHelper.getAsJsonArray(json, "variants", new JsonArray()).asList().stream().map(JsonElement::getAsString).toList() : List.of("default");
        builder.health = GsonHelper.getAsInt(json, "health", 20);
        builder.scale = GsonHelper.getAsFloat(json, "scale", 1);
        builder.maxProgress = GsonHelper.getAsInt(json, "max_progress", 50);
        return builder.create();
    }


    public static class TitanBuilder {
        public ResourceLocation id;
        public List<String> variants;
        public int health;
        public float scale;
        public int maxProgress;

        public TitanBuilder() {}

        public Titan create() {
            return new Titan(id, variants, health, scale, maxProgress);
        }
    }
}
