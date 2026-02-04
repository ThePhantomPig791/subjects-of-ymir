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
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.renderer.PalladiumRenderTypes;
import net.threetag.palladium.client.renderer.renderlayer.AbstractPackRenderLayer;
import net.threetag.palladium.util.Easing;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.json.GsonUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

            float alpha = color.getAlpha() * Easing.inExpo(percentCharge) - progress * 0.3f;
            float radius = this.radius * Easing.inQuad(percentCharge) + progress * 0.5f;

            poseStack.pushPose();
            poseStack.translate(0, 0.5, 0);

            var vertexConsumer = bufferSource.getBuffer(PalladiumRenderTypes.LASER);
            Matrix4f matrix = poseStack.last().pose();

            // adapted from https://stackoverflow.com/questions/4081898/procedurally-generate-a-sphere-mesh
            final int M = 25, N = 25;
            Vector3f[] verts = new Vector3f[M * N];
            for (int i = 0; i < M * N; i++) {
                int m = i / M, n = i % N;
                float theta = (float) (Math.PI * m / (M - 1));
                float phi = (float) (2 * Math.PI * n / N);

                float sinT = (float) Math.sin(theta);

                verts[i] = new Vector3f(
                        sinT * (float) Math.cos(phi),
                        sinT * (float) Math.sin(phi),
                        (float) Math.cos(theta)
                );
            }

            float r = color.getRed() / 255f, g = color.getGreen() / 255f, b = color.getBlue() / 255f;
            /*for (int m = 0; m < M; m++) {
                for (int n = 0; n < N; n++) {
                    int mNext = m, nNext = n;
                    int i = mNext * N + nNext;

                    vertexConsumer.vertex(matrix, radius * verts[i].x, radius * verts[i].y, radius * verts[i].z).color(r, g, b, alpha).uv2(15728640).endVertex();
                    nNext++;
                    nNext %= N;
                    i = mNext * N + nNext;
                    vertexConsumer.vertex(matrix, radius * verts[i].x, radius * verts[i].y, radius * verts[i].z).color(r, g, b, alpha).uv2(15728640).endVertex();
                    mNext++;
                    mNext %= M;
                    i = mNext * N + nNext;
                    vertexConsumer.vertex(matrix, radius * verts[i].x, radius * verts[i].y, radius * verts[i].z).color(r, g, b, alpha).uv2(15728640).endVertex();
                    nNext = n;
                    i = mNext * N + nNext;
                    vertexConsumer.vertex(matrix, radius * verts[i].x, radius * verts[i].y, radius * verts[i].z).color(r, g, b, alpha).uv2(15728640).endVertex();
                }
            }*/

            for (int m = 0; m < M - 1; m++) {
                for (int n = 0; n < N; n++) {

                    int n1 = (n + 1) % N;

                    int i00 = m * N + n;
                    int i01 = m * N + n1;
                    int i11 = (m + 1) * N + n1;
                    int i10 = (m + 1) * N + n;

                    vertexConsumer.vertex(matrix, radius * verts[i00].x, radius * verts[i00].y, radius * verts[i00].z).color(r,g,b,alpha).uv2(15728640).endVertex();
                    vertexConsumer.vertex(matrix, radius * verts[i01].x, radius * verts[i01].y, radius * verts[i01].z).color(r,g,b,alpha).uv2(15728640).endVertex();
                    vertexConsumer.vertex(matrix, radius * verts[i11].x, radius * verts[i11].y, radius * verts[i11].z).color(r,g,b,alpha).uv2(15728640).endVertex();
                    vertexConsumer.vertex(matrix, radius * verts[i10].x, radius * verts[i10].y, radius * verts[i10].z).color(r,g,b,alpha).uv2(15728640).endVertex();
                }
            }

            /*
            for (int m = 0; m <= M; m++) {
                for (int n = 0; n < N; n++) {

                    float x = (float) (intermediate * Math.cos(2 * Math.PI * n/N));
                    float y = (float) (intermediate * Math.sin(2 * Math.PI * n/N));
                    float z = (float) Math.cos(Math.PI * m/M);

                    vertexConsumer.vertex(matrix, radius * x, radius * y, radius * z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).uv2(15728640).endVertex();

                    m++;
                    intermediate = (float) Math.sin(Math.PI * m / M);
                    x = (float) (intermediate * Math.cos(2 * Math.PI * n/N));
                    y = (float) (intermediate * Math.sin(2 * Math.PI * n/N));
                    z = (float) Math.cos(Math.PI * m/M);

                    vertexConsumer.vertex(matrix, radius * x, radius * y, radius * z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).uv2(15728640).endVertex();

                    n++;
                    intermediate = (float) Math.sin(Math.PI * m / M);
                    x = (float) (intermediate * Math.cos(2 * Math.PI * n/N));
                    y = (float) (intermediate * Math.sin(2 * Math.PI * n/N));
                    z = (float) Math.cos(Math.PI * m/M);

                    vertexConsumer.vertex(matrix, radius * x, radius * y, radius * z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).uv2(15728640).endVertex();

                    m--;
                    intermediate = (float) Math.sin(Math.PI * m / M);
                    x = (float) (intermediate * Math.cos(2 * Math.PI * n/N));
                    y = (float) (intermediate * Math.sin(2 * Math.PI * n/N));
                    z = (float) Math.cos(Math.PI * m/M);
                    vertexConsumer.vertex(matrix, radius * x, radius * y, radius * z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha).uv2(15728640).endVertex();

                    n--;
                }
            }*/

            poseStack.popPose();
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
