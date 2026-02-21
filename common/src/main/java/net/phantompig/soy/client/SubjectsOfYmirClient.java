package net.phantompig.soy.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.client.animation.BiteAnimation;
import net.phantompig.soy.client.animation.BlockAnimation;
import net.phantompig.soy.client.animation.DaggerAnimation;
import net.phantompig.soy.client.entity.TitanCorpseEntityRenderer;
import net.phantompig.soy.client.model.TitanCorpseModelLayer;
import net.phantompig.soy.client.renderer.PlayerInNapeRenderLayer;
import net.phantompig.soy.client.renderer.LightningSphereRenderLayer;
import net.phantompig.soy.client.texture.variable.*;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.item.DataHoldingItem;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.particle.SoyParticles;
import net.phantompig.soy.particle.SteamParticleType;
import net.threetag.palladium.client.dynamictexture.DynamicTextureManager;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;
import net.threetag.palladium.event.PalladiumClientEvents;
import net.threetag.palladiumcore.registry.client.EntityRendererRegistry;
import net.threetag.palladiumcore.registry.client.ParticleProviderRegistry;

public class SubjectsOfYmirClient {
    public static void init() {
        PackRenderLayerManager.registerParser(SubjectsOfYmir.rsrc("player_in_nape"), PlayerInNapeRenderLayer::parse);
        PackRenderLayerManager.registerParser(SubjectsOfYmir.rsrc("lightning_sphere"), LightningSphereRenderLayer::parse);

        DynamicTextureManager.registerVariable(new TitanIdTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanNamespaceTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanVariantTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanProgressTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanDecayTextureVariable.Serializer());
        DynamicTextureManager.registerVariable(new TitanEyeColorTextureVariable.Serializer());

        PalladiumClientEvents.REGISTER_ANIMATIONS.register(registry -> {
            registry.accept(SubjectsOfYmir.rsrc("block"), BlockAnimation.INSTANCE);
            registry.accept(SubjectsOfYmir.rsrc("bite"), BiteAnimation.INSTANCE);
            registry.accept(SubjectsOfYmir.rsrc("dagger"), DaggerAnimation.INSTANCE);
        });

        EntityRendererRegistry.register(SoyEntities.TITAN_CORPSE, TitanCorpseEntityRenderer::new);
        EntityRendererRegistry.registerModelLayer(TitanCorpseModelLayer.TITAN_CORPSE_MODEL_LAYER_LOCATION, TitanCorpseModelLayer::createBodyLayer);

        ParticleProviderRegistry.register(SoyParticles.STEAM, SteamParticleType.Provider::new);

        ItemProperties.register(SoyItems.INJECTION.get(), SubjectsOfYmir.rsrc("spinal_fluid"), (itemStack, clientLevel, livingEntity, i) -> {
            if (itemStack.getItem() instanceof DataHoldingItem item) {
                return (float) item.get(itemStack) / item.max;
            }
            return 0.0f;
        });
    }
}
