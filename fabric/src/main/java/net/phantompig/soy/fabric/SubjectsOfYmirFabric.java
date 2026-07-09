package net.phantompig.soy.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.fml.config.ModConfig;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.SubjectsOfYmir;
import net.fabricmc.api.ModInitializer;
import net.phantompig.soy.fabric.compat.trinkets.SoyTrinketsUtil;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.compat.curiostrinkets.SoyCuriosTrinketsUtil;
import net.phantompig.soy.util.ChatUtil;
import net.threetag.palladiumcore.util.Platform;

import java.util.concurrent.CompletableFuture;

public class SubjectsOfYmirFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SubjectsOfYmir.init();
        SubjectsOfYmir.setup();

        ForgeConfigRegistry.INSTANCE.register(SubjectsOfYmir.MOD_ID, ModConfig.Type.SERVER, SoyConfig.Server.generateConfig());
        ForgeConfigRegistry.INSTANCE.register(SubjectsOfYmir.MOD_ID, ModConfig.Type.CLIENT, SoyConfig.Client.generateConfig());

        registerPlacedFeatures();

        ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE, (entity, message) -> {
            if (entity instanceof SoyPlayerExtension ext && ext.getTitanInstance().getProgress() > 0 && ext.getTitanInstance().titan != null && !ext.getTitanInstance().titan.canSpeak) {
                return CompletableFuture.completedFuture(ChatUtil.gibberishify(message));
            }
            return CompletableFuture.completedFuture(message);
        });

        if (Platform.isModLoaded("trinkets")) {
            SoyCuriosTrinketsUtil.INSTANCE = new SoyTrinketsUtil();
        }

        SubjectsOfYmir.LOGGER.info("Subjects of Ymir initialized on Fabric!");
    }

    private static void registerPlacedFeatures() {
        BiomeModifications.addFeature(BiomeSelectors.tag(TagKey.create(Registries.BIOME, SubjectsOfYmir.rsrc("has_iceburst_geode"))), GenerationStep.Decoration.UNDERGROUND_DECORATION, ResourceKey.create(Registries.PLACED_FEATURE, SubjectsOfYmir.rsrc("iceburst_geode")));
    }
}
