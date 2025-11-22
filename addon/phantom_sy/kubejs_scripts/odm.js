let $CuriosTrinketsUtil = Java.loadClass('net.threetag.palladium.compat.curiostinkets.CuriosTrinketsUtil');
let $ClientboundSetEntityMotionPacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket');
let $Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3');

global.odm = {};

global.odm.turbines = {
    // all properties and their defaults below
    empty: {},
    prototype_1: {
        accepts_sheath: false,
        max_gas: 500,
        strafe_directions: ['forward', 'up']
    },
    prototype_2: {

    },
    prototype_3: {

    },
    version_1: {

    },
    version_2: {

    }
}
for (let id in global.odm.turbines) {
    let turb = global.odm.turbines[id];
    console.log('----')
    console.log(turb)
    if (turb.accepts_sheath == null) turb.accepts_sheath = true;
    if (turb.max_gas == null) turb.max_gas = 0;
    if (turb.strafe_directions == null) turb.strafe_directions = ['forward', 'left', 'right', 'up']
    console.log(turb)
}

global.odm.sheaths = { // these properties are per sheath, not per pair of sheaths
    empty: {},
    blade: {
        max_gas: 1000
    },
    gas: {
        max_gas: 2000
    }
}
for (let id in global.odm.sheaths) {
    let sheath = global.odm.sheaths[id];
    if (sheath.max_gas == null) sheath.max_gas = 1000;
}

global.odm.indexOfOdmPiece = (list, id) => {
    if (id == null) return 0;
    let i = 0;
    for (let el in list) {
        if (el == id) return i;
        i++;
    }
    return 0;
}
global.odm.getOdmPiece = (list, id) => {
    if (id == null) return null;
    for (let el in list) {
        if (el == id) return list[el];
    }
    return null;
}

ItemEvents.modelProperties(event => {
    event.registerAll('phantom_sy:turbine', (item, level, entity, seed) => {
        return global.odm.indexOfOdmPiece(global.odm.turbines, item.nbt?.Odm?.Turbine) / 10;
    })
    event.registerAll('phantom_sy:sheath_left', (item, level, entity, seed) => {
        return global.odm.indexOfOdmPiece(global.odm.sheaths, item.nbt?.Odm?.SheathLeft) / 10;
    })
    event.registerAll('phantom_sy:sheath_right', (item, level, entity, seed) => {
        return global.odm.indexOfOdmPiece(global.odm.sheaths, item.nbt?.Odm?.SheathRight) / 10;
    })
})


global.odm.getTurbineGas = (odmItem) => {
    return odmItem.nbt?.Odm?.TurbineGas ?? 0;
}
global.odm.getSheathLeftGas = (odmItem) => {
    return odmItem.nbt?.Odm?.SheathLeftGas ?? 0;
}
global.odm.getSheathRightGas = (odmItem) => {
    return odmItem.nbt?.Odm?.SheathRightGas ?? 0;
}

global.odm.getMaxTurbineGas = (odmItem) => {
    return global.odm.getOdmPiece(global.odm.turbines, odmItem.nbt?.Odm?.Turbine)?.max_gas ?? 0;
}
global.odm.getMaxSheathLeftGas = (odmItem) => {
    return global.odm.getOdmPiece(global.odm.sheaths, odmItem.nbt?.Odm?.SheathLeft)?.max_gas ?? 0;
}
global.odm.getMaxSheathRightGas = (odmItem) => {
    return global.odm.getOdmPiece(global.odm.sheaths, odmItem.nbt?.Odm?.SheathRight)?.max_gas ?? 0;
}

global.odm.getTotalGas = (odmItem) => {
    return global.odm.getTurbineGas(odmItem) + global.odm.getSheathLeftGas(odmItem) + global.odm.getSheathRightGas(odmItem);
}
global.odm.getTotalMaxGas = (odmItem) => {
    return global.odm.getMaxTurbineGas(odmItem) + global.odm.getMaxSheathLeftGas(odmItem) + global.odm.getMaxSheathRightGas(odmItem);
}

global.odm.consumeGas = (odmItem, amount) => { // returns false if the odm doesn't have enough gas (the gas is still subtracted though. L)
    if (amount > 0 && global.odm.getSheathLeftGas(odmItem) > 1) {
        amount = consumeGas(odmItem, amount, 'SheathLeftGas');
    }
    if (amount > 0 && global.odm.getSheathRightGas(odmItem) > 1) {
        amount = consumeGas(odmItem, amount, 'SheathRightGas');
    }
    if (amount > 0 && global.odm.getTurbineGas(odmItem) > 1) {
        amount = consumeGas(odmItem, amount, 'TurbineGas');
    }
    return amount == 0;
}
function consumeGas(odmItem, amount, odmGasProperty) {
    let available = odmItem.nbt.Odm[odmGasProperty] ?? 0;
    let leftover = available - amount;
    if (leftover < 0) { // this means we subtracted too much gas, and should return how much should be taken away from somewhere else
        odmItem.nbt.Odm[odmGasProperty] = 0;
        return -leftover;
    }
    odmItem.nbt.Odm[odmGasProperty] = leftover;
    return 0;
}

global.odm.getOdm = (entity) => {
    const items = $CuriosTrinketsUtil.getInstance().getItemsInSlot(entity, Platform.isForge() ? 'belt' : 'legs/belt');
    for (let e of items) {
        if (e.id == 'phantom_sy:odm') {
            return e;
        }
    }
    return null;
}


StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:odm_circuits')
        .documentationDescription('Tick functions for ODM gear')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                let odm = global.odm.getOdm(entity);
                palladium.setProperty(entity, 'phantom_sy:odm.gas', global.odm.getTotalGas(odm));
                palladium.setProperty(entity, 'phantom_sy:odm.max_gas', global.odm.getTotalMaxGas(odm));


                let hooks = [global.odm.getHook(entity, 'right'), global.odm.getHook(entity, 'left')];
                for (let i = 0; i < hooks.length; i++) {
                    let hook = hooks[i];
                    let side = i == 0 ? 'right' : 'left';

                    if (hook != null) {
                        palladium.setProperty(entity, `phantom_sy:odm.hook_${side}`, true);
                        palladium.setProperty(entity, `phantom_sy:odm.hook_${side}.x`, hook.x);
                        palladium.setProperty(entity, `phantom_sy:odm.hook_${side}.y`, hook.y);
                        palladium.setProperty(entity, `phantom_sy:odm.hook_${side}.z`, hook.z);

                        let hookDistance = palladium.getProperty(entity, `phantom_sy:odm.hook_${side}.distance`);
                        let realDistance = entity.position().distanceTo(hook.position());
                        if (hook.type == 'minecraft:marker' && realDistance > hookDistance) {
                            let strength = -10 * Math.pow(1.1, -0.01 * (realDistance - hookDistance)) + 10;
                            // let strength = 0.01 * (realDistance - hookDistance);
                            global.odm.pull(entity, hook, strength);
                        } else {
                            palladium.setProperty(entity, `phantom_sy:odm.hook_${side}.distance`, realDistance);
                        }
                    } else {
                        palladium.setProperty(entity, `phantom_sy:odm.hook_${side}`, false);
                    }
                }
            }
        })

    event.create('phantom_sy:reel_hook')
        .documentationDescription('Reels right/left hooks')

        .addProperty('side', 'string', 'right', 'The hook side (right or left) to reel or recall')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                let side = entry.getPropertyByName('side').toLowerCase();

                let hook = global.odm.getHook(entity, side);
                if (hook == null) return;

                if (hook.type == 'minecraft:marker' && global.odm.consumeGas(global.odm.getOdm(entity), 1)) {
                    let realDistance = entity.position().distanceTo(hook.position());
                    let hookDistance = palladium.getProperty(entity, `phantom_sy:odm.hook_${side}.distance`);
                    if (hookDistance > 3 * realDistance) {
                        hookDistance = realDistance;
                    }
                    if (hookDistance > 1.5 * realDistance) {
                        hookDistance *= 0.8;
                    } else {
                        hookDistance -= 2;
                    }
                    palladium.setProperty(entity, `phantom_sy:odm.hook_${side}.distance`, Math.max(1, hookDistance));
                    if (entity.age % 2 == 0) global.playSoundToAll(entity, 32, 'phantom_sy:reel', 'PLAYERS', 0.5, 0.2 * Math.random() + 1);
                }
            }
        })

    event.create('phantom_sy:shoot_hook')
        .documentationDescription('Shoots right/left hooks')

        .addProperty('side', 'string', 'right', 'The hook side (right or left) to fire')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                let side = entry.getPropertyByName('side').toLowerCase();

                let hook = global.odm.getHook(entity, side);
                if (hook == null) {
                    shootHook(entity, 4, side);
                    global.odm.consumeGas(global.odm.getOdm(entity), 3);
                    global.playSoundToAll(entity, 32, 'phantom_sy:shoot_hook', 'PLAYERS', 0.75, 0.1 * (entity.age % 2) + 0.95);
                }
            }
        })

    
    
    event.create('phantom_sy:recall_hook')
        .documentationDescription('Recalls right/left hooks')

        .addProperty('side', 'string', 'right', 'The hook side (right or left) to reel or recall')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                let side = entry.getPropertyByName('side').toLowerCase();

                let hook = global.odm.getHook(entity, side);
                if (hook == null) return;
                hook.discard();
                global.odm.consumeGas(global.odm.getOdm(entity), 1);
            }
        })


    event.create('phantom_sy:gas_strafe')
        .documentationDescription('Allows the player to strafe when in the air using WASD and Space, consuming ODM gas')

        .tick((entity, entry, holder, enabled) => {
            if (enabled && !entity.onGround()) {
            let directions = global.odm.getOdmPiece(global.odm.turbines, global.odm.getOdm(entity).nbt?.Odm?.Turbine).strafe_directions;

            if (palladium.getProperty(entity, 'left_key_down') && directions.includes('left')) {
                if (global.odm.getHook(entity, 'right')?.type == 'minecraft:marker') {
                    if (global.odm.consumeGas(global.odm.getOdm(entity), 3)) { // orbit leftwards around right hook
                        strafe(entity, 0, entity.yaw - 90, oribtStrength);
                    }
                } else if (global.odm.consumeGas(global.odm.getOdm(entity), 1)) {
                    strafe(entity, 0, entity.yaw - 90, 1);
                }
            }
            if (palladium.getProperty(entity, 'right_key_down') && directions.includes('right')) {
                if (global.odm.getHook(entity, 'left')?.type == 'minecraft:marker') {
                    if (global.odm.consumeGas(global.odm.getOdm(entity), 3)) { // orbit rightwards around left hook
                        strafe(entity, 0, entity.yaw + 90, oribtStrength);
                    }
                } else if (global.odm.consumeGas(global.odm.getOdm(entity), 1)) {
                    strafe(entity, 0, entity.yaw + 90, 1);
                }
            }
            if (palladium.getProperty(entity, 'forward_key_down') && directions.includes('forward')) {
                if (global.odm.consumeGas(global.odm.getOdm(entity), 1)) {
                    strafe(entity, deIntensifyPitch(entity.pitch), entity.yaw, 1);
                }
            }
            if (palladium.getProperty(entity, 'backwards_key_down') && directions.includes('backward')) {
                if (global.odm.consumeGas(global.odm.getOdm(entity), 1)) {
                    strafe(entity, deIntensifyPitch(entity.pitch), entity.yaw + 180, 1);
                }
            }
            if (palladium.getProperty(entity, 'jump_key_down') && directions.includes('up')) {
                if (global.odm.consumeGas(global.odm.getOdm(entity), 2)) {
                    strafe(entity, -90, 0, 1);
                }
            }
        }
        })
})

const baseStrafeStrength = 0.04;
const oribtStrength = 3;

function strafe(entity, pitch, yaw, strength) {
    let direction = $Vec3.directionFromRotation(pitch, yaw).scale(baseStrafeStrength * strength);
    entity.addDeltaMovement(direction);
    if (entity.isPlayer()) {
        entity.connection.send(new $ClientboundSetEntityMotionPacket(entity));
    }
    entity.level.spawnParticles(
        'minecraft:cloud',
        true,
        entity.x,
        entity.y + 1,
        entity.z,
        0,
        0.2,
        0,
        /*count*/ 1,
        /*speed*/ 0.1
    );
    global.playSoundToAll(entity, 16, 'phantom_sy:gas', 'PLAYERS', 0.4, Math.random() * 0.2 + 1.4 / (0.2 * strength));
}

function deIntensifyPitch(x) { // https://www.desmos.com/calculator/btea69orrh
    return -0.0000439557 * Math.pow(x, 3) + 0.0000847253 * Math.pow(x, 2) + 1.00316 * x - 0.868569;
}


StartupEvents.registry('palladium:condition_serializer', (event) => {
    event.create('phantom_sy:odm_hook_out')
        .addProperty('side', 'string', 'right', 'The hook side (right or left) to check')
        .test((entity, properties) => {
            return global.odm.getHook(entity, properties.get('side')) != null;
        })

    event.create('phantom_sy:odm_handle_held')
        .addProperty('side', 'string', 'right', 'The hand (right, left, or either) to check')
        .test((entity, properties) => {
            let side = properties.get('side');
            let mainArm = entity.getMainArm().toString().toLowerCase();

            if (side == 'either') return entity.mainHandItem.id == 'phantom_sy:odm_handle' || entity.offHandItem.id == 'phantom_sy:odm_handle';
            if (side == mainArm) return entity.mainHandItem.id == 'phantom_sy:odm_handle';
            else return entity.offHandItem.id == 'phantom_sy:odm_handle';
        })
})


global.odm.getHook = (entity, side) => {
    let hook = null;
    entity.level.getEntities().forEach(e => {
        if (e.type == 'palladium:custom_projectile' && e.tags.contains(`phantom_sy.odm_hook_${side}`) && e.owner == entity) hook = e;
        if (e.type == 'minecraft:marker' && e.tags.contains(`phantom_sy.odm_hook_${side}`) && (e.nbt.data.getUUID('Owner').equals(entity.uuid) == true)) hook = e;
    });
    return hook;
}

global.odm.pull = (entity, hook, strength) => {
    let entityPos = entity.position().add(0, 0.65, 0);
    let hookPos = hook.position();
    entity.addDeltaMovement(hookPos
        .subtract(entityPos)
        .normalize()
        .scale(strength)
    );
    if (entity.isPlayer()) {
        entity.connection.send(new $ClientboundSetEntityMotionPacket(entity));
    }
}


function toRadians(degrees) {
    return degrees * JavaMath.PI / 180;
}

function toDegrees(radians) {
    return radians * 180 / JavaMath.PI;
}

function shootHook(entity, speed, side) {
    let innac = 3 * (Math.random() * 2 - 1);

    let projectile = entity.block.createEntity('palladium:custom_projectile');
    projectile.mergeNbt({ CommandOnBlockHit: 'odm_hook', Size: 0.4, DieOnBlockHit: false, Tags: [`phantom_sy.odm_hook_${side}`, 'phantom_sy.odm_hook'] });

    let yaw = -toRadians(entity.getYaw() + ((side == 'right' ? 1 : -1) * 20) + innac);
    let pitch = -toRadians(55 + innac);

    let x = entity.x + Math.cos(pitch) * Math.sin(yaw), y = entity.y + entity.getEyeHeight() + Math.sin(pitch), z = entity.z + Math.cos(pitch) * Math.cos(yaw);

    projectile.setPos(x, y, z);
    projectile.setDeltaMovement(entity.getLookAngle().scale(speed).add(entity.getDeltaMovement()));
    projectile.setOwner(entity);
    projectile.spawn();
}