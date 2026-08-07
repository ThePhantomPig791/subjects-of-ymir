package net.phantompig.soy.power.ability;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.phantompig.soy.entity.TitanCorpseEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.Titan;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.property.*;

public class ShedAbility extends Ability {
    public static final PalladiumProperty<Integer> VALUE = new IntegerProperty("value").sync(SyncType.EVERYONE);

    @Override
    public void registerUniqueProperties(PropertyManager manager) {
        manager.register(VALUE, 15);
    }

    @Override
    public void firstTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (entity instanceof SoyPlayerExtension ext && ext.getTitanInstance().getProgress() <= 1) {
            entry.setUniqueProperty(VALUE, 15);
        }
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && !entity.level().isClientSide() && entry.getEnabledTicks() % 15 == 0) {
            int value = entry.getProperty(VALUE);
            if (value > 1) {
                entry.setUniqueProperty(VALUE, --value);
                update(entity, entry);
            }
        }

        if (entity instanceof TitanCorpseEntity en && en.tickCount < 2) {
            entry.setUniqueProperty(VALUE, entry.getProperty(VALUE)); // don't even ask
        }
    }

    public static void update(LivingEntity entity, AbilityInstance entry) {
        int value = entry.getProperty(VALUE);
        if (entity instanceof SoyPlayerExtension ext) {
            ext.getTitanInstance().strengthIncreases.put("shed (or lack thereof)", value / 10f);
        }
        if (entity.getAttribute(Attributes.ARMOR).getModifier(Titan.TITAN_ARMOR_ATTRIBUTE_UUID) != null) {
            double armor = entity.getAttribute(Attributes.ARMOR).getValue();
            entity.getAttribute(Attributes.ARMOR).removeModifier(Titan.TITAN_ARMOR_ATTRIBUTE_UUID);
            entity.getAttribute(Attributes.ARMOR).addPermanentModifier(new AttributeModifier(Titan.TITAN_ARMOR_ATTRIBUTE_UUID, "titan armor", armor - 2, AttributeModifier.Operation.ADDITION));
        }
        PlayerUtil.playSoundToAll(entity.level(),
                entity.getX(),
                entity.getY() + entity.getBoundingBox().getYsize() / 2,
                entity.getZ(),
                48,
                SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                SoundSource.PLAYERS,
                0.7f,
                (float) (0.1 * Math.random() + 1.8 - value / 45f)
        );
    }

    public static int getMinimumShedLevel(LivingEntity entity) {
        int min = 15;
        for (AbilityInstance entry : AbilityUtil.getInstances(entity, SoyAbilities.SHED.get())) {
            if (entry.getProperty(VALUE) < min) min = entry.getProperty(VALUE);
        }
        return min;
    }
}
