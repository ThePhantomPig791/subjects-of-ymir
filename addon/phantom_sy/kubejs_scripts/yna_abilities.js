StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:increase_max_charge')
        .addProperty('amount', 'integer', 0, 'The amount to increase the max charge by');

    event.create('phantom_sy:increase_recharge')
        .addProperty('amount', 'integer', 0, 'The extra amount to recharge by');

    event.create('phantom_sy:increase_max_hardening')
        .addProperty('amount', 'integer', 0, 'The extra amount to increase the max hardening by');
    
    event.create('phantom_sy:multiply_shift_explosion')
        .addProperty('amount', 'float', 1, 'The number to multiply the shift explosion strength by');

    event.create('phantom_sy:increase_armor')
        .addProperty('amount', 'float', 1, 'The number to add to the total armor durability on shift');


    event.create('phantom_sy:steam_unshift_fx')
        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                global.playSoundToAll(entity, 16, 'minecraft:block.fire.extinguish', 'PLAYERS', 1, 1.5);
                global.playSoundToAll(entity, 16, 'minecraft:block.fire.extinguish', 'PLAYERS', 1, 1.7);
                global.playSoundToAll(entity, 16, 'minecraft:block.fire.extinguish', 'PLAYERS', 1, 1.9);

                entity.level.sendParticles(
                    'minecraft:campfire_signal_smoke',
                    entity.x,
                    entity.y,
                    entity.z,
                    /*count*/ 5 * entity.getBoundingBox().getYsize(),
                    entity.getBoundingBox().getXsize() * 1,
                    entity.getBoundingBox().getYsize() * 4,
                    entity.getBoundingBox().getZsize() * 1,
                    /*speed*/ 0.1
                );
                entity.level.sendParticles(
                    'minecraft:cloud',
                    entity.x,
                    entity.y,
                    entity.z,
                    /*count*/ 30,
                    entity.getBoundingBox().getXsize() * 2.5,
                    entity.getBoundingBox().getYsize() * 5,
                    entity.getBoundingBox().getZsize() * 2.5,
                    /*speed*/ 0.5
                );
            }
        });

    event.create('phantom_sy:harden')
        .addProperty('interval', 'integer', 20, 'The amount of ticks between each harden increase')
        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                entity.level.sendParticles(
                    'minecraft:dust 1 1 1 2',
                    entity.x,
                    entity.y,
                    entity.z,
                    /*count*/ 40,
                    entity.getBoundingBox().getXsize() * 1,
                    entity.getBoundingBox().getYsize() * 2,
                    entity.getBoundingBox().getZsize() * 1,
                    /*speed*/ 0.1
                );
                
                if (entity.age % entry.getPropertyByName('interval') == 0) {
                    const hardening = palladium.getProperty(entity, 'phantom_sy:hardening');
                    const max = palladium.getProperty(entity, 'phantom_sy:max_hardening') + global.yna.getAdditionalMaxHardening(entity);

                    if (hardening < max) {
                        palladium.setProperty(entity, 'phantom_sy:hardening', hardening + 1);
                        entity.causeFoodExhaustion(3);

                        entity.level.sendParticles(
                            'minecraft:dust 1 1 1 2',
                            entity.x,
                            entity.y,
                            entity.z,
                            /*count*/ 100,
                            entity.getBoundingBox().getXsize() * 1,
                            entity.getBoundingBox().getYsize() * 2,
                            entity.getBoundingBox().getZsize() * 1,
                            /*speed*/ 0.1
                        );
                        global.playSoundToAll(entity, 64, 'phantom_sy:harden', 'PLAYERS', 1, ((hardening + 1) / 6) + 1);
                    }

                    entity.modifyAttribute('palladium:punch_damage', '94ea5104-e7be-484a-a0b8-b18963ca45d3', hardening * 2, 'ADDITION');
                }
            }
        })
        .lastTick((entity, entry, holder, enabled) => {
            entity.modifyAttribute('palladium:punch_damage', '94ea5104-e7be-484a-a0b8-b18963ca45d3', 0, 'ADDITION');
        });
});