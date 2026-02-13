package net.phantompig.soy.client.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.client.model.TitanCorpseModelLayer;
import net.phantompig.soy.entity.TitanCorpseEntity;
import org.jetbrains.annotations.NotNull;

public class TitanCorpseEntityRenderer extends LivingEntityRenderer<TitanCorpseEntity, HumanoidModel<TitanCorpseEntity>> {
    public TitanCorpseEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new TitanCorpseModelLayer(context.bakeLayer(TitanCorpseModelLayer.TITAN_CORPSE_MODEL_LAYER_LOCATION)), 0.5f);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(TitanCorpseEntity entity) {
        return SubjectsOfYmir.rsrc("textures/blank.png");
    }

    @Override
    protected boolean shouldShowName(TitanCorpseEntity entity) {
        return false;
    }
}
