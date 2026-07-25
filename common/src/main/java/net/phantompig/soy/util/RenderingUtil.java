package net.phantompig.soy.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.threetag.palladium.client.renderer.PalladiumRenderTypes;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderingUtil {
    public static void renderSphere(PoseStack poseStack, MultiBufferSource bufferSource, float radius, float r, float g, float b, float alpha) {
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

        poseStack.popPose();
    }

    public static void renderTriangle(PoseStack poseStack, MultiBufferSource bufferSource, float yaw, float pitch, float angle, float radius, float r, float g, float b, float alpha) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);

        var vertexConsumer = bufferSource.getBuffer(PalladiumRenderTypes.LASER);
        Matrix4f matrix = poseStack.last().pose();

        vertexConsumer.vertex(matrix, 0, 0, 0).color(r, g, b, alpha).uv2(15728640).endVertex();
        vertexConsumer.vertex(matrix, (float) (radius * Mth.cos(pitch + angle) * Math.sin(yaw + angle)), (float) (radius * Mth.cos(pitch + angle) * Math.cos(yaw + angle)), (float) (radius * Math.sin(yaw + angle))).color(r, g, b, alpha).uv2(15728640).endVertex();
        angle *= -1;
        vertexConsumer.vertex(matrix, (float) (radius * Mth.cos(pitch + angle) * Math.sin(yaw + angle)), (float) (radius * Mth.cos(pitch + angle) * Math.cos(yaw + angle)), (float) (radius * Math.sin(yaw + angle))).color(r, g, b, alpha).uv2(15728640).endVertex();
        vertexConsumer.vertex(matrix, 0, 0, 0).color(r, g, b, alpha).uv2(15728640).endVertex();

        poseStack.popPose();
    }
}
