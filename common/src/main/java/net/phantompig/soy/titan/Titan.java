package net.phantompig.soy.titan;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.stat.SoyStats;
import net.threetag.palladium.power.SuperpowerUtil;
import net.threetag.palladium.util.PlayerUtil;
import oshi.annotation.concurrent.Immutable;

import java.util.List;
import java.util.UUID;

@Immutable
public class Titan {
    public static final UUID TITAN_HEALTH_ATTRIBUTE_UUID = UUID.fromString("09ed062b-73c1-4a2d-0803-8512514aa03e");
    public static final UUID TITAN_ARMOR_ATTRIBUTE_UUID = UUID.fromString("12abc62b-73c1-4a2d-0000-8512514aa03e");

    public final ResourceLocation id;
    public final ResourceLocation powerPath;
    public final List<String> variants;
    public final int resolution;
    public final float scale;
    public final int maxProgress;
    public final int maxCharge;

    public final TitanStats stats;

    private Titan(ResourceLocation id, List<String> variants, int resolution, float scale, int maxProgress, int maxCharge, TitanStats stats) {
        this.id = id;
        this.variants = variants;
        this.resolution = resolution;
        this.scale = scale;
        this.maxProgress = maxProgress;
        this.maxCharge = maxCharge;
        this.stats = stats;

        this.powerPath = id.withPath("titan/" + id.getPath());
    }


    public void startShift(LivingEntity entity, int charge) {
        SuperpowerUtil.addSuperpower(entity, this.powerPath);
        if (entity.getAttribute(Attributes.MAX_HEALTH).getModifier(TITAN_HEALTH_ATTRIBUTE_UUID) == null) {
            entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(TITAN_HEALTH_ATTRIBUTE_UUID, "titan extra health", this.stats.extraHealth, AttributeModifier.Operation.ADDITION));
        }
        if (entity.getAttribute(Attributes.ARMOR).getModifier(TITAN_ARMOR_ATTRIBUTE_UUID) == null) {
            entity.getAttribute(Attributes.ARMOR).addPermanentModifier(new AttributeModifier(TITAN_ARMOR_ATTRIBUTE_UUID, "titan armor", this.stats.extraArmor, AttributeModifier.Operation.ADDITION));
        }
        entity.extinguishFire();
        entity.removeAllEffects();
        if (entity instanceof Player player) {
            player.awardStat(SoyStats.TIMES_SHIFTED, 1);
        }
    }

    public void completedShift(LivingEntity entity, int charge) {

    }

    public void unshift(LivingEntity entity) {
        SuperpowerUtil.removeSuperpower(entity, this.powerPath);
        entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(TITAN_HEALTH_ATTRIBUTE_UUID);
        entity.getAttribute(Attributes.ARMOR).removeModifier(TITAN_ARMOR_ATTRIBUTE_UUID);
        entity.heal(20);
        entity.removeAllEffects();
        entity.extinguishFire();
        if (entity instanceof ServerPlayer player) {
            player.heal(1);
            player.hurt(player.damageSources().magic(), 1);
            player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), List.of(player.getAttribute(Attributes.MAX_HEALTH), player.getAttribute(Attributes.ARMOR))));
        }
    }

    public void tick(LivingEntity entity) {
        if (entity instanceof Player player) {
            player.awardStat(SoyStats.TIME_AS_TITAN, 1);

            if (entity.getHealth() != entity.getMaxHealth()) {
                float emptyHealthPercent = 1 - entity.getHealth() / entity.getMaxHealth();
                for (int count = 0; count <= 3.5 * emptyHealthPercent; count++) {
                    if (0.5 * Math.random() < emptyHealthPercent) PlayerUtil.spawnParticleForAll(
                            entity.level(),
                            64,
                            (ParticleOptions) SoyParticles.LARGE_STEAM.get(),
                            true,
                            entity.getX() + entity.getBoundingBox().getXsize() * (Math.random() - 0.5),
                            entity.getY() + entity.getBoundingBox().getYsize() / 3 + 6 * (Math.random() - 0.5),
                            entity.getZ() + entity.getBoundingBox().getZsize() * (Math.random() - 0.5),
                            0, 0, 0,
                            0.1f * emptyHealthPercent * (float) (Math.random() - 0.5),
                            1
                    );
                }
            }
        }
    }


    public static Titan fromJson(ResourceLocation id, JsonObject json) {
        var builder = new TitanBuilder();
        builder.id = id;
        builder.variants = GsonHelper.isArrayNode(json, "variants") ? GsonHelper.getAsJsonArray(json, "variants", new JsonArray()).asList().stream().map(JsonElement::getAsString).toList() : List.of("default");
        builder.resolution = GsonHelper.getAsInt(json, "resolution", 128);
        builder.scale = GsonHelper.getAsFloat(json, "scale", 1);
        builder.maxProgress = GsonHelper.getAsInt(json, "max_progress", 15);
        builder.maxCharge = GsonHelper.getAsInt(json, "max_charge", 50);
        builder.stats = TitanStats.fromJson(json.getAsJsonObject("stats"));
        return builder.create();
    }


    public static class TitanBuilder {
        public ResourceLocation id;
        public List<String> variants;
        public int resolution;
        public float scale;
        public int maxProgress;
        public int maxCharge;
        public TitanStats stats;

        public TitanBuilder() {}

        public Titan create() {
            return new Titan(id, variants, resolution, scale, maxProgress, maxCharge, stats);
        }

        public TitanBuilder withStats(TitanStats stats) {
            this.stats = stats;
            return this;
        }
    }

    public static class TitanStats {
        public float speed;
        public float jump;
        public double extraHealth;
        public double extraArmor;

        public TitanStats() {}

        public static TitanStats fromJson(JsonObject json) {
            TitanStats stats = new TitanStats();
            stats.jump = GsonHelper.getAsFloat(json, "jump", 1);
            stats.speed = GsonHelper.getAsFloat(json, "speed", 1);
            stats.extraHealth = GsonHelper.getAsDouble(json, "extra_health", 0);
            stats.extraArmor = GsonHelper.getAsDouble(json, "extra_armor", 0);
            return stats;
        }
    }

    @Override
    public String toString() {
        return "Titan{" +
                "id=" + id +
                ", variants=" + variants +
                '}';
    }
}
