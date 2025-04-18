let CuriosTrinketsUtil = Java.loadClass('net.threetag.palladium.compat.curiostinkets.CuriosTrinketsUtil');
let ClientboundSetEntityMotionPacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket');
let Vec3 = Java.loadClass('net.minecraft.world.phys.Vec3')

StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:odm/circuits')
        .firstTick((entity, entry, holder, enabled) => {
            if (!enabled) {
                palladium.setProperty(entity, 'phantom_sy:odm.handles', false);
            }
        })
        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                const odmItem = getOdm(entity);

                if (odmItem.nbt == null) odmItem.nbt = {};

                if (odmItem.nbt.gas == null) odmItem.nbt.gas = 0;
                palladium.setProperty(entity, 'phantom_sy:odm.gas', odmItem.nbt.gas);
                
                if (odmItem.nbt.sheath_blades == null) odmItem.nbt.sheath_blades = 0;
                palladium.setProperty(entity, 'phantom_sy:odm.sheath_blades', odmItem.nbt.sheath_blades);
            }

            // hooks
            let rightHookX = palladium.getProperty(entity, 'phantom_sy:odm.hook.right.x');
            let rightHookY = palladium.getProperty(entity, 'phantom_sy:odm.hook.right.y');
            let rightHookZ = palladium.getProperty(entity, 'phantom_sy:odm.hook.right.z');
            // console.log(rightHookX + ', ' + rightHookY + ', ' + rightHookZ)
            if (rightHookX != null && rightHookY != null && rightHookZ != null) {
                applyHookVelocity(entity, rightHookX, rightHookY, rightHookZ, 0.02, 0.01, 0.02);

                global.drawParticleLine(entity, rightHookX, rightHookY, rightHookZ);
            }
        })
        .lastTick((entity, entry, holder, enabled) => {
            palladium.setProperty(entity, 'phantom_sy:odm.gas', 0);
            palladium.setProperty(entity, 'phantom_sy:odm.sheath_blades', 0);
            palladium.setProperty(entity, 'phantom_sy:odm.handles', false);
            if (palladium.getProperty(entity, 'phantom_sy:odm.handle_blades')) {
                palladium.setProperty(entity, 'phantom_sy:odm.handle_blades', false);
                entity.block.popItem('phantom_sy:odm_blade'); // do i need two of these? figure it out later
            }
        });

    event.create('phantom_sy:odm/gas_burst')
        .addProperty('strength', 'integer', 1, 'The strength of the velocity to be added')
        .addProperty('gas_cost', 'integer', 1, 'How much gas to remove')
        .addProperty('gas_chance', 'float', 1, 'Gas will only be removed this percent of the time (number 0 - 1)')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                depleteGasChance(entity, entry.getPropertyByName('gas_cost'), entry.getPropertyByName('gas_chance'));
                global.playSoundToAll(entity, 16, 'phantom_sy:gas_burst', 'PLAYERS', 1, 0.7 + Math.random() * 0.2);

                let strength = entry.getPropertyByName('strength');
                let move = entity.getLookAngle().scale(strength);
                entity.addDeltaMovement(move);
                entity.addDeltaMovement(new Vec3(0, Math.abs(strength) * 0.4, 0));
            
                if (entity.isPlayer()) {
                  entity.connection.send(new ClientboundSetEntityMotionPacket(entity));
                }
            }
        });

    event.create('phantom_sy:odm/gas_slow_fall')
        .addProperty('gas_cost', 'integer', 1, 'How much gas to remove')
        .addProperty('gas_chance', 'float', 1, 'Gas will only be removed this percent of the time (number 0 - 1)')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                depleteGasChance(entity, entry.getPropertyByName('gas_cost'), entry.getPropertyByName('gas_chance'));
                global.playSoundToAll(entity, 16, 'phantom_sy:gas_burst', 'PLAYERS', 0.3, 1.4 + Math.random() * 0.2);

                entity.addDeltaMovement(new Vec3(0, 0.05, 0));
                if (entity.fallDistance >= 0.7) entity.fallDistance -= 0.7;
            
                if (entity.isPlayer()) {
                    entity.connection.send(new ClientboundSetEntityMotionPacket(entity));
                }
            }
        });

    event.create('phantom_sy:odm/handles')
        .firstTick((entity, entry, holder, enabled) => {
            if (enabled) {
                palladium.setProperty(entity, 'phantom_sy:odm.handles', true);

                global.playSoundToAll(entity, 16, 'minecraft:item.armor.equip_iron', 'PLAYERS', 0.42, 1.8);
                global.playSoundToAll(entity, 16, 'minecraft:item.armor.equip_leather', 'PLAYERS', 0.54, 1.2);
                global.playSoundToAll(entity, 16, 'minecraft:item.armor.equip_generic', 'PLAYERS', 0.54, 1.4);
            }
        })
        .lastTick((entity, entry, holder, enabled) => {
            if (enabled) {
                palladium.setProperty(entity, 'phantom_sy:odm.handles', false);

                global.playSoundToAll(entity, 16, 'minecraft:item.armor.equip_iron', 'PLAYERS', 0.42, 1.4);
                global.playSoundToAll(entity, 16, 'minecraft:item.armor.equip_leather', 'PLAYERS', 0.54, 1);
                global.playSoundToAll(entity, 16, 'minecraft:item.armor.equip_generic', 'PLAYERS', 0.54, 1.1);
            }
        });

    event.create('phantom_sy:odm/reload_blades')
        .firstTick((entity, entry, holder, enabled) => {
            if (enabled) {
                const odmItem = getOdm(entity);
                odmItem.nbt.sheath_blades--;
                palladium.setProperty(entity, 'phantom_sy:odm.handle_blades', true);

                global.playSoundToAll(entity, 16, 'minecraft:block.iron_trapdoor.open', 'PLAYERS', 0.6, 1.3);
                global.playSoundToAll(entity, 16, 'minecraft:item.armor.equip_iron', 'PLAYERS', 0.6, 1.5);
            }
        });

    event.create('phantom_sy:odm/drop_blades')
        .firstTick((entity, entry, holder, enabled) => {
            if (enabled) {
                palladium.setProperty(entity, 'phantom_sy:odm.handle_blades', false);
                entity.block.popItem('phantom_sy:odm_blade'); // do i need two of these? figure it out later

                global.playSoundToAll(entity, 16, 'minecraft:block.iron_trapdoor.close', 'PLAYERS', 0.1, 1.9);
                global.playSoundToAll(entity, 16, 'minecraft:block.iron_trapdoor.open', 'PLAYERS', 0.3, 1.8);
            }
        });

    event.create('phantom_sy:odm/hook')
        .addProperty('hook', 'string', 'right', '\'right\' or \'left\'')
        .addProperty('play_sound', 'boolean', true, 'If this plays the ODM shoot hook sound (set to false when this should just remove the hook)')
        .addProperty('gas_cost', 'integer', 1, 'How much gas to remove (set to 0 when this should just remove the hook)')
        .firstTick((entity, entry, holder, enabled) => {
            if (enabled) {
                let hook = entry.getPropertyByName('hook');
                palladium.setProperty(entity, `phantom_sy:odm.hook.${hook}.x`, null);
                palladium.setProperty(entity, `phantom_sy:odm.hook.${hook}.y`, null);
                palladium.setProperty(entity, `phantom_sy:odm.hook.${hook}.z`, null);

                depleteGas(entity, entry.getPropertyByName('gas_cost'));

                if (entry.getPropertyByName('play_sound') == true) {
                    global.playSoundToAll(entity, 16, 'minecraft:block.iron_trapdoor.close', 'PLAYERS', 0.1, 0.6);
                }
            }
        });
});

function getOdm(entity) {
    const items = CuriosTrinketsUtil.getInstance().getItemsInSlot(entity, Platform.isForge() ? 'belt' : 'legs/belt');
    for (let e of items) {
        if (e.id == 'phantom_sy:odm_gear') {
            return e;
        }
    }
}

function depleteGasChance(entity, amount, chance) {
    if (Math.random() <= chance) depleteGas(entity, amount);
}
function depleteGas(entity, amount) {
    getOdm(entity).nbt.gas -= amount;
}

function applyHookVelocity(entity, targetX, targetY, targetZ, xStrength, yStrength, zStrength) {
    // took this from my old bad spiderman addonpack. i have no idea what it's doing
    var v = [(targetX - entity.x) * xStrength, (targetY - entity.y) * yStrength, (targetZ - entity.z) * zStrength];
    v.forEach(element => {
        element = element - (Math.floor(entity.motionX + entity.motionY + entity.motionZ) / 2)
        if (Math.abs(element) > 1.5) {
            element = element * 0.1;
        }
    });

    entity.addMotion(v[0], v[1], v[2]);
    if (entity.isPlayer()) entity.connection.send(new ClientboundSetEntityMotionPacket(entity));
    //if (v[1] >= 0) e.fallDistance = 0;
}

global.drawParticleLine = function(entity, xf, yf, zf) {
    let x = entity.x, y = entity.y, z = entity.z;
    let dist = (xf - x) * (xf - x) + (yf - y) * (yf - y) + (zf - z) * (zf - z);
    let numParticles = dist / 1;
    let dx = xf - x, dy = yf - y, dz = zf - z;
    let incx = dx / numParticles, incy = dy / numParticles, incz = dz / numParticles;

    for (let i = 0; i < numParticles; i++) {
        entity.level.sendParticles('minecraft:dust 0 0 0 0.1', x, y, z, /*count*/ 1, 0, 0, 0, /*speed*/ 0);
        x += incx;
        y += incy;
        z += incz;
    }
}