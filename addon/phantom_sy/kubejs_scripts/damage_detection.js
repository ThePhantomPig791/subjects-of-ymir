StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:damage_detection')
        .documentationDescription('Sets and ticks down the can_shift property accordingly') // Actually detecting the damage is dealt with in a server script
        
        .tick((entity, entry, holder, enabled) => {
            const time = palladium.getProperty(entity, 'phantom_sy:can_shift');
            if (time > 0 && !palladium.getProperty(entity, 'phantom_sy:is_charging_shift')) palladium.setProperty(entity, 'phantom_sy:can_shift', time - 1);
        });
});