package net.phantompig.soy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.entity.ThrownBladeEntity;
import org.jetbrains.annotations.NotNull;

public class ThrownBladeEntityRenderer extends EntityRenderer<ThrownBladeEntity> {
    private final ItemRenderer itemRenderer;

    public ThrownBladeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(ThrownBladeEntity entity) {
        return SubjectsOfYmir.rsrc("textures/blank.png");
    }

    @Override
    public void render(ThrownBladeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(2, 2, 2);
        poseStack.translate(0, 0.25, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(90 - entityYaw));
        poseStack.mulPose(Axis.ZN.rotationDegrees((float) (10 * entity.getDeltaMovement().lengthSqr() * (entity.tickCount + partialTick))));
        this.itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
