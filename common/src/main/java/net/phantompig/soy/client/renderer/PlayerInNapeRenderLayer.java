package net.phantompig.soy.client.renderer;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.property.SoyProperties;
import net.threetag.palladium.client.renderer.renderlayer.AbstractPackRenderLayer;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.context.DataContext;

public class PlayerInNapeRenderLayer extends AbstractPackRenderLayer {
    public PlayerModel<Player> model;

    public void setModel(Player player, PlayerModel playerModel) {
        boolean smallArms = PlayerUtil.hasSmallArms(player);
        model = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(smallArms ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), smallArms);
        model.head.xScale = model.head.yScale = model.head.zScale = 0.7f;
        playerModel.copyPropertiesTo(model);
    }

    @Override
    public void render(DataContext context, PoseStack poseStack, MultiBufferSource bufferSource, EntityModel<Entity> parentModel, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity entity = context.getLivingEntity();

        if (entity instanceof Player player && parentModel instanceof PlayerModel playerModel) {
            float scale = SoyProperties.SCALE.get(player);

            if (model == null) {
                setModel(player, playerModel);
            }

            var body = playerModel.body;

            poseStack.pushPose();
            poseStack.scale(1 / scale, 1 / scale, 1 / scale);
            poseStack.translate(body.x, body.y, body.z + 0.5 * (scale - 1) / scale);
            poseStack.mulPose(Axis.XP.rotation(body.xRot));
            poseStack.mulPose(Axis.YP.rotation(body.yRot));
            poseStack.mulPose(Axis.ZP.rotation(body.zRot));

            model.renderToBuffer(
                    poseStack,
                    bufferSource.getBuffer(RenderType.entityCutout(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(context.getEntity()).getTextureLocation(context.getEntity()))),
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    1, 1, 1, 1);

            poseStack.popPose();
        }
    }

    public static PlayerInNapeRenderLayer parse(JsonObject json) {
        return new PlayerInNapeRenderLayer();
    }
}
