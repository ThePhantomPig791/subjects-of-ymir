package net.phantompig.soy.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.client.animation.BazookaAnimation;
import net.phantompig.soy.item.DataHoldingItem;
import net.phantompig.soy.item.SoyItems;
import net.phantompig.soy.particle.*;
import net.threetag.palladium.event.PalladiumClientEvents;
import net.threetag.palladium.util.SplashTextUtil;
import net.threetag.palladiumcore.registry.client.ParticleProviderRegistry;

public class SubjectsOfYmirClient {
    public static void init() {

        ParticleProviderRegistry.register(SoyParticles.DIRT_CLOUD, DirtCloudParticleType.Provider::new);

        ItemProperties.register(SoyItems.INJECTION.get(), SubjectsOfYmir.rsrc("spinal_fluid"), (itemStack, clientLevel, livingEntity, i) -> {
            if (itemStack.getItem() instanceof DataHoldingItem item) {
                return (float) item.get(itemStack) / item.max;
            }
            return 0.0f;
        });

        PalladiumClientEvents.REGISTER_ANIMATIONS.register(registry -> {
            registry.accept(SubjectsOfYmir.rsrc("block"), BazookaAnimation.INSTANCE);
        });

        SplashTextUtil.addRandom(1, "Dedicate your heart!");

        SubjectsOfYmir.LOGGER.info("Subjects of Ymir initialized on the client");
    }
}
