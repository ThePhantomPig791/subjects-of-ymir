package net.phantompig.soy.power;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.player.SubjectsOfYmirPlayerExtension;
import net.threetag.palladium.power.*;
import net.threetag.palladium.power.provider.PowerProvider;
import net.threetag.palladium.power.provider.PowerProviders;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class TitanPowerProvider extends PowerProvider {
    public static final RegistrySupplier<PowerProvider> TITAN_POWERS = PowerProviders.PROVIDERS.register("subjects_of_ymir/titans", TitanPowerProvider::new);

    @Override
    public void providePowers(LivingEntity entity, IPowerHandler handler, PowerCollector collector) {
        if (entity instanceof SubjectsOfYmirPlayerExtension playerExt) {
            if (playerExt.getTitanInstance().titan == null) return;
            ResourceLocation id = playerExt.getTitanInstance().titan.id;
            PowerManager inst = PowerManager.getInstance(null);
            Power power = inst.getPower(new ResourceLocation(id.getNamespace() + ":titan/" + id.getPath()));
            if (power == null) {
                SubjectsOfYmir.LOGGER.error("Unable to find power for titan {}", id);
                return;
            }
            collector.addPower(power, () -> new Validator(id));
            collector.addPower(inst.getPower(SubjectsOfYmir.rsrc("shifter")), () -> new Validator(id));
        }
    }

    public static void init() {}

    public record Validator(ResourceLocation id) implements IPowerValidator {
        @Override
        public boolean stillValid(LivingEntity entity, Power power) {
            if (entity instanceof SubjectsOfYmirPlayerExtension playerExt) {
                return playerExt.getTitanInstance().is(this.id);
            }
            return false;
        }
    }
}
