StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:swing_hand')
        .documentationDescription('Deals damage')
        .addProperty('hand', 'string', 'MAIN_HAND', 'Accepts MAIN_HAND or OFF_HAND')
        
        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                entity.swing(entry.getPropertyByName('hand'), true);
            }
        })
})