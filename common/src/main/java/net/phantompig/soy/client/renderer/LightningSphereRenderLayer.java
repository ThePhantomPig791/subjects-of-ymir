package net.phantompig.soy.client.renderer;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.util.RenderingUtil;
import net.threetag.palladium.client.renderer.renderlayer.AbstractPackRenderLayer;
import net.threetag.palladium.util.Easing;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.json.GsonUtil;

import java.awt.*;

public class LightningSphereRenderLayer extends AbstractPackRenderLayer {
    private final float chargeThreshold;
    private final int chargeMax;
    private final Color color;
    private final float radius;

    public LightningSphereRenderLayer(float chargeThreshold, int chargeMax, Color color, float radius) {
        this.chargeThreshold = chargeThreshold;
        this.chargeMax = chargeMax;
        this.color = color;
        this.radius = radius;
    }

    @Override
    public void render(DataContext context, PoseStack poseStack, MultiBufferSource bufferSource, EntityModel<Entity> parentModel, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity entity = context.getLivingEntity();

        if (entity instanceof Player player) {
            int charge = SoyProperties.CHARGE.get(player);
            int progress = SoyProperties.PROGRESS.get(player);
            float percentCharge = Math.max((charge - (chargeThreshold * chargeMax) + partialTicks) / chargeMax, 0);

            float alpha = color.getAlpha() * Easing.inExpo(percentCharge) - progress * 0.5f;
            float radius = this.radius * Easing.inQuad(percentCharge) + progress * 0.5f;

            float r = color.getRed() / 255f, g = color.getGreen() / 255f, b = color.getBlue() / 255f;

            if (SoyConfig.Client.shouldShiftRenderSphere()) {
                RenderingUtil.renderSphere(poseStack, bufferSource, radius, r, g, b, alpha);
            }
            if (percentCharge > 0.75 || progress > 1) {
                for (int i = (int) (((progress / 10f) + 8) * Math.random()); i > 4; i--) {
                    RenderingUtil.renderTriangle(poseStack, bufferSource, (float) (3.1415 * Mth.sin(ageInTicks)), (float) (1.57 * Mth.cos(ageInTicks + 100)), 0.2f, 500 * radius, r, g, b, alpha);
                }
            }
        }
    }

    public static LightningSphereRenderLayer parse(JsonObject json) {
        return new LightningSphereRenderLayer(
                GsonHelper.getAsFloat(json, "charge_threshold", 0),
                GsonHelper.getAsInt(json, "charge_max", 50),
                GsonUtil.getAsColor(json, "color", new Color(0.75f, 0.6f, 0.1f)),
                GsonHelper.getAsFloat(json, "radius", 1)
        );
    }
}
