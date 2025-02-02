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
        })
        .lastTick((entity, entry, holder, enabled) => {
            palladium.setProperty(entity, 'phantom_sy:odm.gas', 0);
            palladium.setProperty(entity, 'phantom_sy:odm.sheath_blades', 0);
            palladium.setProperty(entity, 'phantom_sy:odm.handles', false);
            if (palladium.getProperty(entity, 'phantom_sy:odm.handle_blades')) {
                palladium.setProperty(entity, 'phantom_sy:odm.handle_blades', false);
                entity.block.popItem('minecraft:iron_nugget'); // replace with blade item later
            }
        });

    event.create('phantom_sy:odm/gas_burst')
        .addProperty('strength', 'integer', 1, 'The strength of the velocity to be added')
        .addProperty('gas_cost', 'integer', 1, 'How much gas to remove')
        .addProperty('gas_chance', 'float', 1, 'Gas will only be removed this percent of the time (number 0 - 1)')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                if (Math.random() <= entry.getPropertyByName('gas_chance')) depleteGas(entity, entry.getPropertyByName('gas_cost'));
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
                if (Math.random() <= entry.getPropertyByName('gas_chance')) depleteGas(entity, entry.getPropertyByName('gas_cost'));
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
                entity.block.popItem('minecraft:iron_nugget'); // replace with blade item later

                global.playSoundToAll(entity, 16, 'minecraft:block.iron_trapdoor.close', 'PLAYERS', 0.1, 1.9);
                global.playSoundToAll(entity, 16, 'minecraft:block.iron_trapdoor.open', 'PLAYERS', 0.3, 1.8);
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

function depleteGas(entity, amount) {
    getOdm(entity).nbt.gas -= amount;
}