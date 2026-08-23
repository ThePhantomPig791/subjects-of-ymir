package net.phantompig.soy.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.mixin.client.PowerTabAccessor;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.power.ability.GrabbingAbility;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.renderer.PalladiumRenderTypes;
import net.threetag.palladium.client.screen.power.PowersScreen;
import net.threetag.palladium.entity.BodyPart;
import net.threetag.palladiumcore.util.Platform;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

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

    /**
     * @return True if the entity's rendering should be canceled
     */
    public static boolean transformPosestackForGrabbedEntity(Entity entity, PoseStack poseStack, float partialTicks) {
        if (SoyProperties.GRABBED.isRegistered(entity) && SoyProperties.GRABBED.get(entity) && entity instanceof LivingEntity livingEntity) {
            LivingEntity grabber = GrabbingAbility.getGrabber(livingEntity);
            if (grabber == null) return false;

            if (Objects.equals(Minecraft.getInstance().player, grabber) && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                return true;
            } else if (grabber instanceof AbstractClientPlayer player) {
                Vec3 delta = grabber.getPosition(partialTicks).subtract(entity.getPosition(partialTicks));
                poseStack.translate(delta.x, delta.y, delta.z);

                if (Platform.isForge() && grabber.isCrouching()) {
                    poseStack.translate(0, 6, 0);
                }
                poseStack.mulPoseMatrix(BodyPart.getTransformationMatrix(BodyPart.RIGHT_ARM, new Vector3f(0, 0.6f, 0), player, partialTicks).normalize3x3());

                var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
                Vec3 offset = renderer.getRenderOffset(player, partialTicks);
                poseStack.translate(offset.x, offset.y, offset.z);

                poseStack.mulPose(Direction.NORTH.getRotation());
            }
        }
        return false;
    }

    public static final ResourceLocation WIDGETS = SubjectsOfYmir.rsrc("textures/gui/powers/widgets.png"); // power screen icons
    public static final ResourceLocation WINDOW = SubjectsOfYmir.rsrc("textures/gui/powers/window.png");
    public static final ResourceLocation TABS = SubjectsOfYmir.rsrc("textures/gui/powers/tabs.png");
    public static final ResourceLocation VANILLA_WIDGETS = SubjectsOfYmir.rsrc("textures/gui/widgets.png"); // buttons
    public static final ResourceLocation VANILLA_ICONS = SubjectsOfYmir.rsrc("textures/gui/titan_icons.png"); // health bar and stuff


    public static boolean isTitan() {
        return Minecraft.getInstance().player instanceof SoyPlayerExtension ext && ext.soy$getTitanInstance().getProgress() > 0;
    }
    public static boolean shouldRenderTitanGui() {
        if (
                Minecraft.getInstance().screen instanceof PowersScreen screen
                        && screen.selectedTab != null
        ) {
            ResourceLocation id = ((PowerTabAccessor) screen.selectedTab).soy$getIPowerHolder().getPower().getId();
            return SubjectsOfYmir.isShifterOrTitanPower(id);
        }
        return false;
    }
    public static ResourceLocation titanAtlas(ResourceLocation original, ResourceLocation titanReplacement) {
        return shouldRenderTitanGui() ? titanReplacement : original;
    }

    public static ResourceLocation widgetsAtlas(ResourceLocation original) {
        return titanAtlas(original, WIDGETS);
    }
    public static ResourceLocation windowAtlas(ResourceLocation original) {
        return titanAtlas(original, WINDOW);
    }
    public static ResourceLocation tabsAtlas(ResourceLocation original) {
        return titanAtlas(original, TABS);
    }
    public static ResourceLocation vanillaWidgetsAtlas(ResourceLocation original) {
        return titanAtlas(original, VANILLA_WIDGETS);
    }
    public static ResourceLocation vanillaIconsAtlas(ResourceLocation original) {
        return isTitan() ? VANILLA_ICONS : original;
    }
}
