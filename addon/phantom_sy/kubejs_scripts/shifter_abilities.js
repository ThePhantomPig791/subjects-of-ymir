const ScaleData = Java.loadClass('virtuoel.pehkui.api.ScaleData')
const ScaleType = Java.loadClass('virtuoel.pehkui.api.ScaleType')
const ScaleTypes = Java.loadClass('virtuoel.pehkui.api.ScaleTypes')

const SoundEvents = Java.loadClass('net.minecraft.sounds.SoundEvents');

const ClientboundSetEntityMotionPacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket');

const INFINITE = Java.loadClass('java.lang.Integer').MAX_VALUE;
const LOG256 = Math.log(256);

StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:shift')
        .documentationDescription('Shifts into a titan; charges while enabled then shifts once disabled or once the charge reaches its max')

        .addProperty('max_charge', 'integer', 50, 'The maximum charging ticks')
        .addProperty('bonus_charge', 'integer', 0, 'Free charge; affects explosions')
        
        .addUniqueProperty('charge', 'integer', 0)
        .addUniqueProperty('healing_phase_ticks', 'integer', 0)

        .tick((entity, entry, holder, enabled) => {
            const MARKS = palladium.getProperty(entity, 'phantom_sy:marks');
            if (entity.age % 10 == 0) {
                if (MARKS > 0) palladium.setProperty(entity, 'phantom_sy:marks', MARKS - 1);
                if (MARKS < 0) palladium.setProperty(entity, 'phantom_sy:marks', 0);
            }

            const CAN_SHIFT_TIME = palladium.getProperty(entity, 'phantom_sy:can_shift');
            if (CAN_SHIFT_TIME > 0) palladium.setProperty(entity, 'phantom_sy:can_shift', CAN_SHIFT_TIME - 1);


            const CHARGE = entry.getPropertyByName('charge');
            const MAX_CHARGE = entry.getPropertyByName('max_charge');
            const SCALE = global.titans.list[palladium.getProperty(entity, 'phantom_sy:titan')].scale;
            let progress = palladium.getProperty(entity, 'phantom_sy:progress');
            if (enabled && CHARGE != MAX_CHARGE) {
                // each charging tick

                palladium.setProperty(entity, 'phantom_sy:charge_glow', Math.floor(Math.pow(10, LOG256 / MAX_CHARGE * CHARGE)) - 1); // https://www.desmos.com/calculator/hjxxuultbj
                entry.setUniquePropertyByName('charge', CHARGE + 1);
            } else {
                if (CHARGE >= 1) {
                    // heal the player before starting to shift --- this is the "healing phase" and it only lasts for a max of 30 ticks
                    // (don't interrupt a concurrent transformation; only delay an upcoming one after it charges up)
                    let healingPhaseTicks = entry.getPropertyByName('healing_phase_ticks');
                    if (!entity.isCreative() && healingPhaseTicks <= 30 && progress == 0 && ((entity.getHealth() / entity.getMaxHealth()) < 0.9)) {
                        if (entity.age % 5 == 0) entity.heal(Math.ceil(CHARGE / 20));
                        entity.level.sendParticles('minecraft:dust 1 1 0 0.5', entity.x, entity.y + 1, entity.z, /*count*/ 5, 2.5, 2, 2.5, /*speed*/ 1);
                        palladium.setProperty(entity, 'phantom_sy:in_healing_phase', true);
                        entry.setUniquePropertyByName('healing_phase_ticks', ++healingPhaseTicks);
                        palladium.setProperty(entity, 'phantom_sy:can_shift', 0);
                    } else {
                        if (progress < global.titans.MAX_SHIFT_PROGRESS) {
                            if (progress < 0) progress = 0;
                            if (progress == 0) {
                                // first shifting tick

                                palladium.superpowers.addSuperpower(entity, `phantom_sy:titan_${palladium.getProperty(entity, 'phantom_sy:titan')}`);

                                palladium.setProperty(entity, 'phantom_sy:in_healing_phase', false);
                                entry.setUniquePropertyByName('healing_phase_ticks', 0);

                                setScaleTime(entity, 9);
                                scale(entity, SCALE);
                                setScaleTime(entity, 20);
                                entity.potionEffects.add('minecraft:resistance', 10, 1, true, false);

                                global.playSoundLocal(entity, 'phantom_sy:shift_local', 'PLAYERS', 1, 1);
                                global.playSoundToAll(entity, 96, 'phantom_sy:shift', 'PLAYERS', 1, 1);
                            }

                            // each shifting tick

                            palladium.setProperty(entity, 'phantom_sy:progress', ++progress);
            
                            var explosion = entity.block.offset('up', progress).createExplosion();
                            var strength = Math.floor((CHARGE + entry.getPropertyByName('bonus_charge')) / 10);
                            strength = Math.max(strength, 0);
                            explosion.causesFire(false)
                                .exploder(entity)
                                .explosionMode('mob')
                                .strength(strength)
                                .explode();
            
                            var lightningBolt = entity.block.offset('down', 5).createEntity('minecraft:lightning_bolt');
                            lightningBolt.spawn();

                            entity.heal(1);
                            entity.resetFallDistance();
                            entity.level.sendParticles('minecraft:dust 1 1 0 3', entity.x + 0.5, entity.y + progress, entity.z + 0.5, /*count*/ 50, 3, 1, 3, /*speed*/ 0.3);
                        } else {
                            // on completed shift

                            var explosion = entity.block.offset('up', SCALE * 1.5).createExplosion();
                            var strength = Math.floor((CHARGE + entry.getPropertyByName('bonus_charge')) / 5);
                            strength = Math.max(strength, 0);
                            explosion.causesFire(false)
                                .exploder(entity)
                                .explosionMode('mob')
                                .strength(strength)
                                .explode();

                            entry.setUniquePropertyByName('charge', 0);

                            entity.heal(10);
                            entity.potionEffects.add('minecraft:regeneration', 40, 5, true, false);
                            entity.potionEffects.add('minecraft:saturation', 40, 5, true, false);
                            entity.setFoodLevel(20);

                            if (palladium.superpowers.hasSuperpower(entity, 'phantom_sy:new_shifter')) {
                                palladium.superpowers.removeSuperpower(entity, 'phantom_sy:new_shifter');
                                palladium.superpowers.addSuperpower(entity, 'phantom_sy:shifter');
                            }
                        }
                    }
                }
            }

            if (progress == global.titans.MAX_SHIFT_PROGRESS) {
                // every tick while shifted

                if (entity.age % 5 == 0) {
                    if (MARKS < 500) palladium.setProperty(entity, 'phantom_sy:marks', MARKS + 1);
                }
            }
        })

    event.create('phantom_sy:unshift')
        .documentationDescription('Unshifts from titan form')

        .addProperty('spawn_skeleton', 'boolean', true, 'Whether to spawn a titan corpse fitted to the player\'s titan')

        .tick((entity, entry, holder, enabled) => {
            let foodLevel = entity.type == 'minecraft:player' ? entity.getFoodLevel() : 20;
            let forced = enabled ? false : palladium.getProperty(entity, 'phantom_sy:force_unshift') || foodLevel <= 6
            if (enabled || forced) {
                const Y_DISPLACEMENT = global.titans.list[palladium.getProperty(entity, 'phantom_sy:titan')].scale * 1.2;

                palladium.superpowers.removeSuperpower(entity, `phantom_sy:titan_${palladium.getProperty(entity, 'phantom_sy:titan')}`);

                if (entry.getPropertyByName('spawn_skeleton') || forced) {
                    var skeleton = entity.level.createEntity('minecraft:skeleton');
                    skeleton.mergeNbt({ NoAI: 1, DeathLootTable: 'minecraft:nothing' });
                    skeleton.potionEffects.add('minecraft:fire_resistance', INFINITE, 0, true, false);
                    skeleton.potionEffects.add('minecraft:invisibility', INFINITE, 0, true, false);
                    skeleton.teleportTo(entity.level, entity.x, entity.y + Y_DISPLACEMENT, entity.z, [], entity.yRot, entity.xRot);
                    scaleFromSourceEntity(skeleton, entity);
                    ScaleTypes.HITBOX_HEIGHT.getScaleData(skeleton).setScale(1);
                    ScaleTypes.EYE_HEIGHT.getScaleData(skeleton).setScale(1);

                    palladium.setProperty(skeleton, 'phantom_sy:titan', palladium.getProperty(entity, 'phantom_sy:titan'));
                    palladium.setProperty(skeleton, 'phantom_sy:titan_variant', palladium.getProperty(entity, 'phantom_sy:titan_variant'));
                    palladium.setProperty(skeleton, 'phantom_sy:progress', palladium.getProperty(entity, 'phantom_sy:progress'));
                    palladium.setProperty(skeleton, 'phantom_sy:decay', 700);
                    
                    superpowerUtil.addSuperpower(skeleton, `phantom_sy:titan_${palladium.getProperty(entity, 'phantom_sy:titan')}`);

                    skeleton.spawn();
                }

                scaleInstant(entity, 1);
                if (entity.type == 'minecraft:player') entity.setFoodLevel(20);
                entity.teleportTo(entity.level, entity.x, entity.y + Y_DISPLACEMENT, entity.z, [], entity.yRot, entity.xRot);
                entity.attack(1);
                entity.potionEffects.add('minecraft:regeneration', 10, 0, true, false);
                entity.potionEffects.add('minecraft:resistance', 20, 1, true, false);
                entity.potionEffects.add('minecraft:blindness', 10, 0, true, false);
                entity.potionEffects.add('minecraft:night_vision', 20, 0, true, false);

                entity.addDeltaMovement(entity.getLookAngle().reverse().scale(0.5));
                if (entity.isPlayer()) {
                    entity.connection.send(new ClientboundSetEntityMotionPacket(entity));
                }

                palladium.setProperty(entity, 'phantom_sy:progress', 0);
                palladium.setProperty(entity, 'phantom_sy:force_unshift', false);
            }


            if (entity.type == 'minecraft:skeleton') {
                const SCALE = global.titans.list[palladium.getProperty(entity, 'phantom_sy:titan')].scale;
                let decay = palladium.getProperty(entity, 'phantom_sy:decay');
                if (decay > 20 && entity.health > 0) {
                    if (entity.age % 3 == 0) {
                        palladium.setProperty(entity, 'phantom_sy:decay', decay - 1);
                    }
                    if (entity.age % 2 == 0) {
                        global.playSoundToAll(entity, 64, 'minecraft:block.fire.extinguish', 'NEUTRAL', 1, 1.2);
                    }

                    entity.level.sendParticles(
                        'minecraft:campfire_cosy_smoke',
                        entity.x,
                        entity.y,
                        entity.z,
                        /*count*/ 5,
                        entity.getBoundingBox().getXsize() / 2.5,
                        SCALE * 0.5,
                        entity.getBoundingBox().getZsize() / 2.5,
                        /*speed*/ 0.3
                    );

                    entity.level.sendParticles(
                        'minecraft:ash',
                        entity.x,
                        entity.y,
                        entity.z,
                        /*count*/ 30,
                        entity.getBoundingBox().getXsize() * 2.5,
                        SCALE,
                        entity.getBoundingBox().getZsize() * 2.5,
                        /*speed*/ 1
                    );

                    entity.level.sendParticles(
                        'minecraft:cloud',
                        entity.x,
                        entity.y,
                        entity.z,
                        /*count*/ 30,
                        entity.getBoundingBox().getXsize() * 2.5,
                        SCALE,
                        entity.getBoundingBox().getZsize() * 2.5,
                        /*speed*/ 0.5
                    );
                } else {
                    entity.remove('discarded');
                }
            } else if (palladium.getProperty(entity, 'phantom_sy:decay') != 999) palladium.setProperty(entity, 'phantom_sy:decay', 999);
        })

    event.create('phantom_sy:damage_in_radius')
        .documentationDescription('Damages nearby entities')

        .addProperty('distance', 'float', 4.0, 'The maximum distance away an entity can be for it to be damaged')
        .addProperty('damage', 'integer', 2, 'The damage to deal')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                const distance = entry.getPropertyByName('distance');
                const nearbyEntities = entity.level.getEntities(entity, entity.getBoundingBox().inflate(distance));
                for (let i = 0; i < nearbyEntities.length; i++) {
                    let e = nearbyEntities[i];
                    if (e.distanceToSqr(entity) > (distance * distance)) continue;
                    e.attack(entry.getPropertyByName('damage'));
                    e.addDeltaMovement(e.getPosition(0).subtract(entity.getPosition(0)).normalize().scale(1 / e.distanceToSqr(entity)));
                }
            }
        })

    event.create('phantom_sy:bite')
        .documentationDescription('Bites hand (deals damage) after enabling and then disabling')

        .addProperty('amount', 'integer', 3, 'The amount of damage to deal')
        .addProperty('time', 'integer', 10, 'The max time this ability needs to be enabled in order to deal damage')
        
        .addUniqueProperty('timer', 'integer', 0)
        .addUniqueProperty('prev_timer', 'integer', 0)
        
        .tick((entity, entry, holder, enabled) => {
            const timer = entry.getPropertyByName('timer');
            entry.setUniquePropertyByName('prev_timer', timer);
            if (enabled && timer < entry.getPropertyByName('time')) {
                entry.setUniquePropertyByName('timer', timer + 1);
            } else if (!enabled && timer > 0) {
                entry.setUniquePropertyByName('timer', timer - 1);
            }
        })
        .lastTick((entity, entry, holder, enabled) => {
            if (enabled) {
                if (entry.getPropertyByName('timer') == entry.getPropertyByName('time') && !entity.isCrouching()) {
                    entity.attack(entry.getPropertyByName('amount'));
                }
            }
        })

    event.create('phantom_sy:throw_lightning_embers')
        .documentationDescription('Spawns lightning trails between the player and nearby blocks and entities')

        .addProperty('range', 'float', 4.5, 'The range for the target position')
        .addProperty('entity_chance', 'float', 0.25, 'If there are blocks and entities available to target, the entity will be targeted this percent (decimal) of the time')
        .addProperty('chance', 'float', 1, 'Lightning trails spawn this percent (decimal) of the time')
        .addProperty('amount', 'integer', 1, 'This many lightning trails are spawned each time')
        
        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                /*if (Math.random() < entry.getPropertyByName('chance')) {
                    for (let i = 0; i < entry.getPropertyByName('amount'); i++) {
                        global.throwLightningEmbers(entity, entry.getPropertyByName('range'), 0.1, entry.getPropertyByName('entity_chance'));
                    }
                }*/
            }
        })
})


function scaleFromSourceEntity(entity, sourceDataEntity) {
    ScaleTypes.HITBOX_WIDTH.getScaleData(entity).setTargetScale(ScaleTypes.HITBOX_WIDTH.getScaleData(sourceDataEntity).getScale());
    ScaleTypes.HITBOX_HEIGHT.getScaleData(entity).setTargetScale(ScaleTypes.HITBOX_HEIGHT.getScaleData(sourceDataEntity).getScale());
    ScaleTypes.EYE_HEIGHT.getScaleData(entity).setTargetScale(ScaleTypes.EYE_HEIGHT.getScaleData(sourceDataEntity).getScale());
    ScaleTypes.THIRD_PERSON.getScaleData(entity).setTargetScale(ScaleTypes.THIRD_PERSON.getScaleData(sourceDataEntity).getScale());
    ScaleTypes.REACH.getScaleData(entity).setTargetScale(ScaleTypes.REACH.getScaleData(sourceDataEntity).getScale());
}

function scale(entity, value) {
    ScaleTypes.HITBOX_WIDTH.getScaleData(entity).setTargetScale(value);
    ScaleTypes.HITBOX_HEIGHT.getScaleData(entity).setTargetScale(value);
    ScaleTypes.EYE_HEIGHT.getScaleData(entity).setTargetScale(value);
    ScaleTypes.THIRD_PERSON.getScaleData(entity).setTargetScale(value);
    ScaleTypes.REACH.getScaleData(entity).setTargetScale(value);
}

function scaleInstant(entity, value) {
    ScaleTypes.HITBOX_WIDTH.getScaleData(entity).setScale(value);
    ScaleTypes.HITBOX_HEIGHT.getScaleData(entity).setScale(value);
    ScaleTypes.EYE_HEIGHT.getScaleData(entity).setScale(value);
    ScaleTypes.THIRD_PERSON.getScaleData(entity).setScale(value);
    ScaleTypes.REACH.getScaleData(entity).setScale(value);
}

function setScaleTime(entity, value) {
    ScaleTypes.HITBOX_WIDTH.getScaleData(entity).setScaleTickDelay(value);
    ScaleTypes.HITBOX_HEIGHT.getScaleData(entity).setScaleTickDelay(value);
    ScaleTypes.EYE_HEIGHT.getScaleData(entity).setScaleTickDelay(value);
    ScaleTypes.THIRD_PERSON.getScaleData(entity).setScaleTickDelay(value);
    ScaleTypes.REACH.getScaleData(entity).setScaleTickDelay(value);
}


global.throwLightningEmbers = function(entity, range, raycastStep, entityChancePercentage) {
    if (raycastStep <= 0) raycastStep = 0.1;
    if (range < 1) range = 1;

    let source = entity.position().add(0, entity.getEyeHeight() * Math.random(), 0);

    let nearbyEntities = entity.level.getEntities(entity, entity.boundingBox.inflate(range));
    let nearbyBlocks = [];
    for (let x = -range; x <= range; x++) {
        for (let y = -range; y <= range; y++) {
            for (let z = -range; z <= range; z++) {
                let block = entity.block.offset(x, y, z);
                if (block.getBlockState().blocksMotion() && global.checkLineOfSight(entity.level, source, block.getPos().getCenter(), raycastStep)) nearbyBlocks.push(block.getPos().getCenter());

            }
        }
    }

    let target;

    if ((Math.random() < entityChancePercentage || nearbyBlocks.length == 0) && nearbyEntities.length > 0) {
        // entity target
        if (nearbyEntities.length == 0) return;
        do {
            target = nearbyEntities.get(Math.random() * nearbyEntities.length); // it's probably bad practice to not have consistency in the type of a single variable but oh well
            target = target.position().add(0, target.getEyeHeight() * Math.random(), 0);
        } while (!global.checkLineOfSight(entity.level, source, target, raycastStep));
    } else {
        // block target
        if (nearbyBlocks.length == 0) return;
        target = nearbyBlocks[Math.floor(Math.random() * nearbyBlocks.length)];
    }

    if (target == undefined) return;
    
    let particlePos = source;
    while (particlePos.distanceToSqr(target) > (raycastStep * raycastStep)) {
        particlePos = particlePos.add(particlePos.vectorTo(target.add(
            Math.random() * 1 - 0.5,
            particlePos.distanceTo(target) * 0.5 * (0.25 * Math.random() + 0.5),
            Math.random() * 1 - 0.5
        )).normalize().scale(raycastStep));
        entity.level.sendParticles(
            'phantom_sy:lightning_ember',
            particlePos.x(),
            particlePos.y(),
            particlePos.z(),
            /*count*/ 1,
            0,
            0,
            0,
            /*speed*/ 0
        );
        if (particlePos.distanceToSqr(target) <= (raycastStep * raycastStep + 0.25)) break;
        // console.log(`${source} -> ${particlePos} -> ${target} | ${particlePos.distanceTo(target)}`)
    }
}

global.checkLineOfSight = function(level, from, to, step) { // both positions are a Vec3
    let stepVec = from.vectorTo(to).normalize().scale(step);
    let stepPosition = from;
    while (stepPosition.distanceToSqr(to) > (step * step)) {
        stepPosition = stepPosition.add(stepVec);
        if (level.getBlock(stepPosition.x(), stepPosition.y(), stepPosition.z()).getBlockState().blocksMotion()) return false;
        if (stepPosition.distanceToSqr(to) <= (step * step)) break;
        console.log('stepping los')
    }
    return true;
}