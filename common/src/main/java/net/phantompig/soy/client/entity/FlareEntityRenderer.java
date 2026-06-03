package net.phantompig.soy.client.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.entity.FlareEntity;
import org.jetbrains.annotations.NotNull;

public class FlareEntityRenderer extends EntityRenderer<FlareEntity> {
    public FlareEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(FlareEntity entity) {
        return SubjectsOfYmir.rsrc("textures/blank.png");
    }
}
