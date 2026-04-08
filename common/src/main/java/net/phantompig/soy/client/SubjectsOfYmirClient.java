package net.phantompig.soy.client;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.client.animation.BiteAnimation;
import net.phantompig.soy.client.animation.BlockAnimation;
import net.phantompig.soy.client.animation.DaggerAnimation;
import net.phantompig.soy.client.animation.modifier.TitanAttackSpeedModifier;
import net.phantompig.soy.client.entity.TitanCorpseEntityRenderer;
import net.phantompig.soy.client.model.TitanCorpseModelLayer;
import net.phantompig.soy.client.renderer.PlayerInNapeRenderLayer;
import net.phantompig.soy.client.renderer.LightningSphereRenderLayer;
import net.phantompig.soy.client.screen.overlay.SoyOverlays;
import net.phantompig.soy.client.texture.variable.*;
import net.phantompig.soy.entity.SoyEntities;
import net.phantompig.soy.item.DataHoldingItem;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.particle.*;
import net.threetag.palladium.client.dynamictexture.DynamicTextureManager;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;
import net.threetag.palladium.event.PalladiumClientEvents;
import net.threetag.palladium.util.SplashTextUtil;
import net.threetag.palladiumcore.registry.client.EntityRendererRegistry;
import net.threetag.palladiumcore.registry.client.OverlayRegistry;
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
        DynamicTextureManager.registerVariable(new HardeningAllVariable.Serializer());
        DynamicTextureManager.registerVariable(new HardeningKnucklesVariable.Serializer());
        DynamicTextureManager.registerVariable(new HardeningHandsVariable.Serializer());
        DynamicTextureManager.registerVariable(new MarksVariable.Serializer());

        PalladiumClientEvents.REGISTER_ANIMATIONS.register(registry -> {
            registry.accept(SubjectsOfYmir.rsrc("block"), BlockAnimation.INSTANCE);
            registry.accept(SubjectsOfYmir.rsrc("bite"), BiteAnimation.INSTANCE);
            registry.accept(SubjectsOfYmir.rsrc("dagger"), DaggerAnimation.INSTANCE);
        });

        EntityRendererRegistry.register(SoyEntities.TITAN_CORPSE, TitanCorpseEntityRenderer::new);
        EntityRendererRegistry.registerModelLayer(TitanCorpseModelLayer.TITAN_CORPSE_MODEL_LAYER_LOCATION, TitanCorpseModelLayer::createBodyLayer);

        ParticleProviderRegistry.register(SoyParticles.SMALL_STEAM, SmallSteamParticleType.Provider::new);
        ParticleProviderRegistry.register(SoyParticles.LARGE_STEAM, LargeSteamParticleType.Provider::new);
        ParticleProviderRegistry.register(SoyParticles.EMBER, EmberParticleType.Provider::new);
        ParticleProviderRegistry.register(SoyParticles.DIRT_CLOUD, DirtCloudParticleType.Provider::new);

        ItemProperties.register(SoyItems.INJECTION.get(), SubjectsOfYmir.rsrc("spinal_fluid"), SubjectsOfYmirClient::getModelProgressForDataItem);
        ItemProperties.register(SoyItems.VIAL.get(), SubjectsOfYmir.rsrc("spinal_fluid"), SubjectsOfYmirClient::getModelProgressForDataItem);

        PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register((player, animationStack) -> {
            ModifierLayer<IAnimation> layer = new ModifierLayer<>();
            layer.addModifierBefore(new TitanAttackSpeedModifier(player));
            animationStack.addAnimLayer(0, layer);
            PlayerAnimationAccess.getPlayerAssociatedData(player).set(SubjectsOfYmir.rsrc("soy_animation"), layer);
        });

        SplashTextUtil.addRandom(250, "Dedicate your heart!");

        OverlayRegistry.registerOverlay(SubjectsOfYmir.MOD_ID + "/titan_exhaustion", SoyOverlays::renderExhaustionOverlay);
        OverlayRegistry.registerOverlay(SubjectsOfYmir.MOD_ID + "/all_hardening", SoyOverlays::renderAllHardeningOverlay);

        SubjectsOfYmir.LOGGER.info("Subjects of Ymir initialized on the client");
    }

    private static float getModelProgressForDataItem(ItemStack itemStack, ClientLevel clientLevel, LivingEntity livingEntity, int i) {
        if (itemStack.getItem() instanceof DataHoldingItem item) {
            return (float) item.get(itemStack) / item.max;
        }
        return 0.0f;
    }
}
