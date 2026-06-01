package net.phantompig.soy.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.entity.OdmNodeEntity;
import net.threetag.palladium.client.renderer.PalladiumRenderTypes;
import net.threetag.palladium.util.RenderUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class OdmNodeEntityRenderer extends EntityRenderer<OdmNodeEntity> {
    public OdmNodeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(OdmNodeEntity entity) {
        return SubjectsOfYmir.rsrc("textures/blank.png");
    }

    @Override
    public void render(OdmNodeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        if (entity.getOwner() != null) {
            AtomicReference<Vec3> startPos = new AtomicReference<>();
            entity.getStuckEntity().ifPresentOrElse(e -> startPos.set(e.getPosition(partialTick).add(entity.getStuckOffset())), () -> startPos.set(entity.getPosition(partialTick)));
            Vec3 endPos = entity.getOwnerPosition(partialTick);
            poseStack.pushPose();
            RenderUtil.faceVec(poseStack, startPos.get(), endPos);
            RenderUtil.renderFilledBox(poseStack, buffer.getBuffer(PalladiumRenderTypes.LASER_NORMAL_TRANSPARENCY), new AABB(0, 0, 0, 0.05, 0.05, startPos.get().distanceTo(endPos)), 0, 0, 0, 1, 0);
            poseStack.popPose();
        }
    }
}
