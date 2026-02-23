package net.phantompig.soy.util;

import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.SubjectsOfYmir;
import net.threetag.palladium.client.renderer.renderlayer.CompoundPackRenderLayer;
import net.threetag.palladium.client.renderer.renderlayer.IPackRenderLayer;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;
import net.threetag.palladium.client.renderer.renderlayer.RenderLayerStates;
import net.threetag.palladium.compat.geckolib.renderlayer.GeckoLayerState;
import net.threetag.palladium.entity.PalladiumLivingEntityExtension;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.Collections;
import java.util.List;

public class AnimationUtil {
    public static void playTitanAnimation(LivingEntity entity, String controllerId, String animationTrigger) {
        if (!(entity instanceof PalladiumLivingEntityExtension extension)) return;
        IPackRenderLayer layer = PackRenderLayerManager.getInstance().getLayer(SubjectsOfYmir.rsrc("titan"));
        if (layer == null) return;
        List<IPackRenderLayer> layers;
        if (layer instanceof CompoundPackRenderLayer com) {
            layers = com.layers();
        } else {
            layers = Collections.singletonList(layer);
        }
        SubjectsOfYmir.LOGGER.info("layers: {}", layers);

        for (IPackRenderLayer renderLayer : layers) {
            RenderLayerStates.State state = extension.palladium$getRenderLayerStates().get(renderLayer);
            if (!(state instanceof GeckoLayerState gecko)) continue;
            AnimatableManager<?> manager = gecko.getAnimatableInstanceCache().getManagerForId(gecko.hashCode() + entity.getId());
            AnimationController<?> controller = manager.getAnimationControllers().get(controllerId);
            SubjectsOfYmir.LOGGER.info("bone snapshots: {}", manager.getBoneSnapshotCollection());
            if (controller == null) continue;
            //controller.forceAnimationReset();
            controller.stop();
            controller.tryTriggerAnimation(animationTrigger);
        }
    }
}
