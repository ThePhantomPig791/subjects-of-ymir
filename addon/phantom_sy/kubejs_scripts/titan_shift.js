let ClientboundSetEntityMotionPacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket');

const ScaleData = Java.loadClass('virtuoel.pehkui.api.ScaleData')
const ScaleType = Java.loadClass('virtuoel.pehkui.api.ScaleType')
const ScaleTypes = Java.loadClass('virtuoel.pehkui.api.ScaleTypes')

StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:titan_shift')
        .documentationDescription('Shifts into a titan')
        .addUniqueProperty('progress', 'integer', 0)
        .addUniqueProperty('charge', 'integer', 0)

        .tick((entity, entry, holder, enabled) => {
            const charge = entry.getPropertyByName('charge');
            const max_charge = palladium.getProperty(entity, 'phantom_sy:max_charge') + global.yna.getAdditionalMaxCharge(entity);
            const charge_glow = palladium.getProperty(entity, 'phantom_sy:charge_glow');

            const progress = entry.getPropertyByName('progress');

            if (enabled && charge == 0) { // first tick
                entry.setUniquePropertyByName('charge', 1);
                palladium.setProperty(entity, 'phantom_sy:is_charging_shift', true);
            }
            if (charge > 0 && charge < max_charge) { // ticks whilst charging
                if (enabled) {
                    entry.setUniquePropertyByName('charge', charge + 1);
                    palladium.setProperty(entity, 'phantom_sy:charge_glow', charge_glow + 2);
                    entity.level.sendParticles('minecraft:dust 1 1 0 0.5', entity.x, entity.y + 1, entity.z, /*count*/ (charge * 0.8), 0.6, 1, 0.6, /*speed*/ 1);
                    /*
                    // entity.lerpMotion(entity.motionX * 0.99, entity.motionY * 0.8, entity.motionZ * 0.99); // maybe take of the xz multiplication idk // ahh make it use the charge to slow more over time
                    // const mult = 1 / (((9 * charge) / 100) + 1);
                    const mult = Math.pow(0.9, 0.219 * charge);
                    entity.setMotionY(entity.motionY * mult);
                    if (entity.isPlayer()) {
                        entity.connection.send(new ClientboundSetEntityMotionPacket(entity));
                    }
                    */
                    if (entity.isPlayer() && charge % 30 == 0) {
                        entity.sendData('phantom_sy:vfx_explosion', {type: 'shockwaveRing', x: entity.x, y: entity.y + 1, z: entity.z, particleType: 'minecraft:cloud'});
                    }

                    entity.potionEffects.add('phantom_sy:glowing', 10, 0, true, false);
                } else {
                    entry.setUniquePropertyByName('progress', 1);
                    palladium.setProperty(entity, 'phantom_sy:is_charging_shift', false);
                    palladium.setProperty(entity, 'phantom_sy:charge_glow', charge * 4);
                }
            }
            if (charge == max_charge && progress == 0) { // last tick
                entry.setUniquePropertyByName('progress', 1);
                palladium.setProperty(entity, 'phantom_sy:is_charging_shift', false);
                palladium.setProperty(entity, 'phantom_sy:charge_glow', charge * 4);
            }

            if (progress == 1) { // first tick
                palladium.setProperty(entity, 'phantom_sy:is_shifting', true);

                scale(entity, entity, 0);

                entity.potionEffects.add('minecraft:regeneration', 40, 5, true, false);
                entity.potionEffects.add('minecraft:saturation', 40, 5, true, false);

                global.playSoundLocal(entity, 'phantom_sy:shift_local', 'PLAYERS', 1, 1);
                global.playSoundToAll(entity, 96, 'phantom_sy:shift', 'PLAYERS', 1, 1);

                if (entity.isPlayer()) {
                    if (Math.floor(charge / 20) * global.yna.getShiftExplosionMultiplier(entity) > 5) entity.sendData('phantom_sy:vfx_explosion', {type: 'smallExplosion', x: entity.x, y: entity.y, z: entity.z});
                }
            }
            if (!palladium.getProperty(entity, 'phantom_sy:cancel_shift')) {
                if (progress >= 1 && progress <= (global.titans.titan_shift_max_progress - 1)) { // in between ticks
                    entity.level.sendParticles('minecraft:dust 1 1 0 3', entity.x + 0.5, entity.y + progress, entity.z + 0.5, /*count*/ 50, 3, 1, 3, /*speed*/ 0.3);
    
                    palladium.setProperty(entity, 'phantom_sy:shift_progress', progress);
                    entry.setUniquePropertyByName('progress', progress + 1);
    
                    var explosion = entity.block.offset('up', progress).createExplosion();
                    var strength = Math.floor(charge / 20);
                    strength = Math.max(strength, 0);
                    explosion.causesFire(false)
                        .exploder(entity)
                        .explosionMode('mob')
                        .strength(strength * global.yna.getShiftExplosionMultiplier(entity))
                        .explode();
    
                    var lightningBolt = entity.block.offset('down', 5).createEntity('minecraft:lightning_bolt');
                    lightningBolt.spawn();
                }
                if (progress == global.titans.titan_shift_max_progress) { // last tick
                    palladium.setProperty(entity, 'phantom_sy:is_shifting', false);
                    palladium.setProperty(entity, 'phantom_sy:shift_progress', global.titans.titan_shift_max_progress);
                    palladium.setProperty(entity, 'phantom_sy:is_shifted', true);
                    palladium.setProperty(entity, 'phantom_sy:can_shift', 0);
                    palladium.setProperty(entity, 'phantom_sy:charge_glow', 0);
                    if (palladium.getProperty(entity, 'phantom_sy:marks') < (100 + (charge / 2))) palladium.setProperty(entity, 'phantom_sy:marks', 100 + Math.floor(charge / 2));
    
                    // yna strand stuff
                    global.yna.onShift(entity, charge);
    
                    entry.setUniquePropertyByName('progress', 0);
                    entry.setUniquePropertyByName('charge', 0);
                }
            } else {
                palladium.setProperty(entity, 'phantom_sy:is_shifted', true);
                palladium.setProperty(entity, 'phantom_sy:is_shifting', false);
                palladium.setProperty(entity, 'phantom_sy:charge_glow', 0);
                entry.setUniquePropertyByName('progress', 0);
                entry.setUniquePropertyByName('charge', 0);
                palladium.setProperty(entity, 'phantom_sy:cancel_shift', false);
            }
        });

    event.create('phantom_sy:unshift')
        .documentationDescription('Unshifts from titan form')
        .addProperty('create_skeleton', 'boolean', true, 'If true, a skeleton will be spawned with the properties of the titan you unshifted from')

        .firstTick((entity, entry, holder, enabled) => {
            if (enabled) {
                scale(entity, entity, 1);

                entity.attack(1);
                entity.potionEffects.add('minecraft:regeneration', 20, 0, true, false);
                entity.potionEffects.add('minecraft:blindness', 30, 0, true, false);
                entity.potionEffects.add('minecraft:night_vision', 20, 0, true, false);

                if (entry.getPropertyByName('create_skeleton')) {
                    var skeleton = entity.level.createEntity('minecraft:skeleton');
                    skeleton.mergeNbt({ NoAI: 1, DeathLootTable: 'minecraft:nothing' });
                    skeleton.potionEffects.add('minecraft:fire_resistance', 999, 0, true, false);
                    skeleton.teleportTo(entity.level, entity.x, entity.y + (palladium.getProperty(entity, 'phantom_sy:titan_height') * 1.3), entity.z, [], entity.yRot, entity.xRot);
                    scale(skeleton, entity, 0);
                    ScaleTypes.HITBOX_HEIGHT.getScaleData(skeleton).setScale(1);
                    ScaleTypes.EYE_HEIGHT.getScaleData(skeleton).setScale(1);
                    
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.titan', palladium.getProperty(entity, 'phantom_sy:titan'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.titan_skin', palladium.getProperty(entity, 'phantom_sy:titan_skin'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.shift_progress', palladium.getProperty(entity, 'phantom_sy:shift_progress') == 0 ? global.titans.titan_shift_max_progress : palladium.getProperty(entity, 'phantom_sy:shift_progress'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.titan_eye_color', palladium.getProperty(entity, 'phantom_sy:titan_eye_color'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.titan_hair_color', palladium.getProperty(entity, 'phantom_sy:titan_hair_color'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.titan_height', palladium.getProperty(entity, 'phantom_sy:titan_height'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.hardening', palladium.getProperty(entity, 'phantom_sy:hardening'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.armor.arms', palladium.getProperty(entity, 'phantom_sy:armor.arms'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.armor.legs', palladium.getProperty(entity, 'phantom_sy:armor.legs'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.armor.chest', palladium.getProperty(entity, 'phantom_sy:armor.chest'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.armor.head', palladium.getProperty(entity, 'phantom_sy:armor.head'));
                    palladium.setProperty(skeleton, 'phantom_sy:skeleton.decay', 700);
                    
                    superpowerUtil.addSuperpower(skeleton, 'phantom_sy:titan_skeleton');

                    skeleton.spawn();
                }
                
                entity.teleportTo(entity.level, entity.x, entity.y + (palladium.getProperty(entity, 'phantom_sy:titan_height') * 1.3), entity.z, [], entity.yRot, entity.xRot);

                global.yna.onUnshift(entity);
                
                palladium.setProperty(entity, 'phantom_sy:shift_progress', 0);
                palladium.setProperty(entity, 'phantom_sy:is_shifted', false);
                palladium.setProperty(entity, 'phantom_sy:charge_glow', 0);
                palladium.setProperty(entity, 'phantom_sy:can_shift', 0);
                palladium.setProperty(entity, 'phantom_sy:cancel_shift', false);
            }
        });
});

function scale(entity, sourceDataEntity, override) {
    if (override == 0) {
        ScaleTypes.HITBOX_WIDTH.getScaleData(entity).setScale(palladium.getProperty(sourceDataEntity, 'phantom_sy:titan_width'));
        ScaleTypes.HITBOX_HEIGHT.getScaleData(entity).setScale(palladium.getProperty(sourceDataEntity, 'phantom_sy:titan_height'));
        ScaleTypes.EYE_HEIGHT.getScaleData(entity).setScale(palladium.getProperty(sourceDataEntity, 'phantom_sy:titan_eye_height'));
        ScaleTypes.MOTION.getScaleData(entity).setScale(palladium.getProperty(sourceDataEntity, 'phantom_sy:titan_motion'));
        ScaleTypes.THIRD_PERSON.getScaleData(entity).setScale(palladium.getProperty(sourceDataEntity, 'phantom_sy:titan_third_person'));
        ScaleTypes.HEALTH.getScaleData(entity).setScale(palladium.getProperty(sourceDataEntity, 'phantom_sy:titan_health'));
        ScaleTypes.REACH.getScaleData(entity).setScale(palladium.getProperty(sourceDataEntity, 'phantom_sy:titan_reach'));
    } else {
        ScaleTypes.HITBOX_WIDTH.getScaleData(entity).setScale(override);
        ScaleTypes.HITBOX_HEIGHT.getScaleData(entity).setScale(override);
        ScaleTypes.EYE_HEIGHT.getScaleData(entity).setScale(override);
        ScaleTypes.MOTION.getScaleData(entity).setScale(override);
        ScaleTypes.THIRD_PERSON.getScaleData(entity).setScale(override);
        ScaleTypes.HEALTH.getScaleData(entity).setScale(override);
        ScaleTypes.REACH.getScaleData(entity).setScale(override);
        ScaleTypes.ATTACK.getScaleData(entity).setScale(override);
    }
}