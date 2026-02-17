package net.phantompig.soy.power.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.PlaySoundAbility;
import net.threetag.palladium.util.PlayerUtil;

public class RepeatSoundAbility extends PlaySoundAbility {
    public RepeatSoundAbility() {
        this.withProperty(SOUND, new ResourceLocation("item.elytra.flying"))
                .withProperty(VOLUME, 1.0F)
                .withProperty(PITCH, 1.0F)
                .withProperty(PLAY_SELF, false)
                .withProperty(PLAY_OTHERS, false);
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance instance, IPowerHolder holder, boolean enabled) {
        if (enabled) {
            if (instance.getProperty(PLAY_SELF)) {
                if (entity instanceof Player player) {
                    PlayerUtil.playSound(player, entity.getX(), entity.getEyeY(), entity.getZ(), instance.getProperty(SOUND), entity.getSoundSource(), instance.getProperty(VOLUME), instance.getProperty(PITCH));
                }
            } else if (instance.getProperty(PLAY_OTHERS)) {
                PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getEyeY(), entity.getZ(), 100.0, instance.getProperty(SOUND), entity.getSoundSource(), instance.getProperty(VOLUME), instance.getProperty(PITCH), (playerx) -> playerx != entity);
            } else {
                PlayerUtil.playSoundToAll(entity.level(), entity.getX(), entity.getEyeY(), entity.getZ(), 100.0, instance.getProperty(SOUND), entity.getSoundSource(), instance.getProperty(VOLUME), instance.getProperty(PITCH));
            }
        }
    }

    @Override
    public String getDocumentationDescription() {
        return "Plays a sound every tick when enabled.";
    }
}
