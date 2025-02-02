StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:steam_healing')
        .documentationDescription('Heals the entity and emits particles')
        .addProperty('amount', 'integer', 1, 'The amount of health to heal')
        .addProperty('interval', 'integer', 40, 'The interval in ticks between each heal')
        .addProperty('hunger_cost', 'integer', 0, 'The hunger to deplete per heal')
        .addProperty('particle', 'string', 'phantom_sy:steam', 'The particle to emit')
        .addProperty('particle_amount_multiplier', 'float', 1, 'The amount of particles spawned is multiplied by this value (rising edge and every tick)')
        .addProperty('volume', 'float', 0.1, 'The volume of the steam sound')
        
        .firstTick((entity, entry, holder, enabled) => {
            if (enabled) {
                const amount = entry.getPropertyByName('amount');
                const particle_amount = Math.max(25 * amount * entry.getPropertyByName('particle_amount_multiplier'), 1);

                entity.level.sendParticles(entry.getPropertyByName('particle'), entity.x, entity.y + entity.getBoundingBox().getYsize() / 2, entity.z, particle_amount, entity.getBoundingBox().getXsize() * 0.25, entity.getBoundingBox().getYsize() * 0.25, entity.getBoundingBox().getZsize() * 0.25, 0.3 * amount);
                global.playSoundToAll(entity, 16, 'minecraft:block.fire.extinguish', 'PLAYERS', entry.getPropertyByName('volume') + 0.3, 1.7);
            }
        })
        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                const amount = entry.getPropertyByName('amount');
                if (entity.age % entry.getPropertyByName('interval') == 0) {
                    entity.heal(amount);
                    entity.causeFoodExhaustion(entry.getPropertyByName('hunger_cost'));
                }

                const particle_amount = Math.max(4 * amount * entry.getPropertyByName('particle_amount_multiplier'), 1);
                entity.level.sendParticles(entry.getPropertyByName('particle'), entity.x, entity.y + entity.getBoundingBox().getYsize() / 2, entity.z, particle_amount, entity.getBoundingBox().getXsize() * 0.25, entity.getBoundingBox().getYsize() * 0.25, entity.getBoundingBox().getZsize() * 0.25, 0.1 * amount);
                if (entity.age % 2 == 0) {
                    global.playSoundToAll(entity, 16, 'minecraft:block.fire.extinguish', 'PLAYERS', entry.getPropertyByName('volume'), 1.8);
                }
            }
        });
})