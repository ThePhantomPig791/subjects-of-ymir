package net.phantompig.soy.client.renderer;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.renderer.PalladiumRenderTypes;
import net.threetag.palladium.client.renderer.renderlayer.AbstractPackRenderLayer;
import net.threetag.palladium.util.Easing;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.json.GsonUtil;
import org.joml.Matrix4f;

import java.awt.*;

public class LightningSphereRenderLayer extends AbstractPackRenderLayer {
    private final int chargeThreshold;
    private final int chargeMax;
    private final Color color;
    private final float radius;

    public LightningSphereRenderLayer(int chargeThreshold, int chargeMax, Color color, float radius) {
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
            float percentCharge = Math.max((charge - chargeThreshold + partialTicks - progress) / chargeMax, 0);
            float alpha = percentCharge * color.getAlpha();

            float radius = this.radius * Easing.inOutCubic(percentCharge);

            poseStack.pushPose();

            var vertexConsumer = bufferSource.getBuffer(PalladiumRenderTypes.LASER);
            Matrix4f matrix = poseStack.last().pose();

            // https://stackoverflow.com/questions/4081898/procedurally-generate-a-sphere-mesh
            final int M = 50, N = 50;
            for (int m = 0; m <= M; m ++) {
                for (int n = 0; n < N; n ++) {
                    float intermediate = (float) Math.sin(Math.PI * m / M);
                    float x = (float) (intermediate * Math.cos(2 * Math.PI * n/N));
                    float y = (float) (intermediate * Math.sin(2 * Math.PI * n/N));
                    float z = (float) Math.cos(Math.PI * m/M);

                    vertexConsumer.vertex(matrix, radius * x, radius * y + 0.5f, radius * z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).uv2(15728640).endVertex();
                    vertexConsumer.vertex(matrix, radius * y, radius * z + 0.5f, radius * x).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).uv2(15728640).endVertex();
                    vertexConsumer.vertex(matrix, radius * z, radius * x + 0.5f, radius * y).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).uv2(15728640).endVertex();
                }
            }

            poseStack.popPose();
        }
    }

    public static LightningSphereRenderLayer parse(JsonObject json) {
        return new LightningSphereRenderLayer(
                GsonHelper.getAsInt(json, "charge_threshold", 0),
                GsonHelper.getAsInt(json, "charge_max", 50),
                GsonUtil.getAsColor(json, "color", new Color(0.75f, 0.6f, 0.1f)),
                GsonHelper.getAsFloat(json, "radius", 1)
        );
    }
}
