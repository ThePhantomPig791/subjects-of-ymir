StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:bite')
        .documentationDescription('Bites hand after enabling and then disabling')
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
        });
})