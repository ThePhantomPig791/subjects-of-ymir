let $CuriosTrinketsUtil = Java.loadClass('net.threetag.palladium.compat.curiostinkets.CuriosTrinketsUtil');
let $ClientboundSetEntityMotionPacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket');

global.odm = {};

global.odm.turbines = {
    // accepts_sheath: true
    // max_gas: 0
    empty: {},
    prototype_1: {
        accepts_sheath: false,
        max_gas: 500
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
for (let turb in global.odm.turbines) {
    if (turb.accepts_sheath == null) turb.accepts_sheath = true;
    if (turb.max_gas == null) turb.max_gas = 0;
}

global.odm.sheaths = { // these properties are per sheath, not per pair of sheaths
    // max_gas: 1000
    empty: {},
    blade: {
        max_gas: 1000
    },
    gas: {
        max_gas: 2000
    }
}
for (let sheath in global.odm.sheaths) {
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
                        }
                    } else {
                        palladium.setProperty(entity, `phantom_sy:odm.hook_${side}`, false);
                    }
                }
            }
        })

    event.create('phantom_sy:hook')
        .documentationDescription('Reels and recalls right/left hooks')

        .addProperty('side', 'string', 'right', 'The hook side (right or left) to reel or recall')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                let side = entry.getPropertyByName('side').toLowerCase();

                let hook = global.odm.getHook(entity, side);
                if (hook == null) return;

                if (hook.type == 'minecraft:marker') {
                    let realDistance = entity.position().distanceTo(hook.position());
                    palladium.setProperty(entity, `phantom_sy:odm.hook_${side}.distance`, Math.max(1, Math.min(realDistance - 1, palladium.getProperty(entity, `phantom_sy:odm.hook_${side}.distance`) - 2)));
                }
                if (entity.crouching) {
                    hook.discard();
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
                }
            }
        })
})

StartupEvents.registry('palladium:condition_serializer', (event) => {
    event.create('phantom_sy:odm_hook_out')
        .addProperty('side', 'string', 'right', 'The hook side (right or left) to check')
        .test((entity, properties) => {
            return global.odm.getHook(entity, properties.get('side')) != null;
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