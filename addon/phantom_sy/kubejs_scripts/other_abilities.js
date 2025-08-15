let EnchantmentHelper = Java.loadClass('net.minecraft.world.item.enchantment.EnchantmentHelper');
let EquipmentSlot = Java.loadClass('net.minecraft.world.entity.EquipmentSlot');
let MobType = Java.loadClass('net.minecraft.world.entity.MobType');
let ServerPlayer = Java.loadClass('net.minecraft.server.level.ServerPlayer');

StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:offhand_attack')
        .documentationDescription('Attacks with the entity\'s offhand item')

        .addProperty('damage', 'float', 2, 'The damage to deal')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                let entityHit = entity.rayTrace().entity;

                if (entityHit == null) return;
                entityHit.attack(entity.damageSources().mobAttack(entity), entry.getPropertyByName('damage'));
                entity.getOffhandItem().hurtEnemy(entityHit, entity);
                entity.addItemCooldown(entity.getOffhandItem(), 6);
                if (entity.isPlayer()) entity.swing('OFF_HAND');
            }
        })
})