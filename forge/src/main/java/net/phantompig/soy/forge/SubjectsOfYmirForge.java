package net.phantompig.soy.forge;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.phantompig.soy.SoyConfig;
import net.phantompig.soy.SubjectsOfYmir;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.phantompig.soy.client.SubjectsOfYmirClient;
import net.phantompig.soy.forge.compat.curios.SoyCuriosUtil;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.compat.curiostrinkets.SoyCuriosTrinketsUtil;
import net.phantompig.soy.util.ChatUtil;
import net.threetag.palladiumcore.forge.PalladiumCoreForge;
import net.threetag.palladiumcore.util.Platform;

@Mod(SubjectsOfYmir.MOD_ID)
@Mod.EventBusSubscriber(modid = SubjectsOfYmir.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SubjectsOfYmirForge {
    @SuppressWarnings("removal")
    public SubjectsOfYmirForge() {
        // Submit our event bus to let PalladiumCore register our content on the right time
        PalladiumCoreForge.registerModEventBus(SubjectsOfYmir.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SubjectsOfYmir.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SoyConfig.Server.generateConfig());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SoyConfig.Client.generateConfig());

        if (Platform.isClient()) {
            SubjectsOfYmirClient.init();
        }

        if (Platform.isModLoaded("curios")) {
            SoyCuriosTrinketsUtil.INSTANCE = new SoyCuriosUtil();
        }

        SubjectsOfYmir.LOGGER.info("Subjects of Ymir initialized on Forge!");
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        SubjectsOfYmir.setup();
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SubjectsOfYmirClient::setup);
    }

    @Mod.EventBusSubscriber(modid = SubjectsOfYmir.MOD_ID)
    public static class SubjectsOfYmirForgeEvents {
        @SubscribeEvent
        public static void obfuscateChatFromTitan(ServerChatEvent event) {
            if (event.getPlayer() instanceof SoyPlayerExtension ext && ext.getTitanInstance().getProgress() > 0 && ext.getTitanInstance().titan != null && !ext.getTitanInstance().titan.canSpeak) {
                event.setMessage(ChatUtil.gibberishify(event.getMessage()));
            }
        }
    }
}
