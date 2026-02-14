package net.phantompig.soy.titan;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.threetag.palladium.power.SuperpowerUtil;
import oshi.annotation.concurrent.Immutable;

import java.util.List;

@Immutable
public class Titan {
    public final ResourceLocation id;
    public final ResourceLocation powerPath;
    public final List<String> variants;
    public final float scale;
    public final int maxProgress;
    public final int maxCharge;

    public final float speed, jump;

    private Titan(ResourceLocation id, List<String> variants, float scale, int maxProgress, int maxCharge, float speed, float jump) {
        this.id = id;
        this.variants = variants;
        this.scale = scale;
        this.maxProgress = maxProgress;
        this.maxCharge = maxCharge;
        this.speed = speed;
        this.jump = jump;

        this.powerPath = id.withPath("titan/" + id.getPath());
    }


    public void startShift(LivingEntity entity, int charge) {
        SuperpowerUtil.addSuperpower(entity, this.powerPath);
    }

    public void completedShift(LivingEntity entity, int charge) {

    }

    public void unshift(LivingEntity entity) {
        SuperpowerUtil.removeSuperpower(entity, this.powerPath);
    }

    public void tick(LivingEntity entity) {

    }


    public static Titan fromJson(ResourceLocation id, JsonObject json) {
        var builder = new TitanBuilder();
        builder.id = id;
        builder.variants = GsonHelper.isArrayNode(json, "variants") ? GsonHelper.getAsJsonArray(json, "variants", new JsonArray()).asList().stream().map(JsonElement::getAsString).toList() : List.of("default");
        builder.scale = GsonHelper.getAsFloat(json, "scale", 1);
        builder.maxProgress = GsonHelper.getAsInt(json, "max_progress", 15);
        builder.maxCharge = GsonHelper.getAsInt(json, "max_charge", 50);
        builder.speed = GsonHelper.getAsFloat(json, "speed", 1);
        builder.jump = GsonHelper.getAsFloat(json, "jump", 1);
        return builder.create();
    }


    public static class TitanBuilder {
        public ResourceLocation id;
        public List<String> variants;
        public float scale;
        public int maxProgress;
        public int maxCharge;
        public float speed;
        public float jump;

        public TitanBuilder() {}

        public Titan create() {
            return new Titan(id, variants, scale, maxProgress, maxCharge, speed, jump);
        }
    }
}
