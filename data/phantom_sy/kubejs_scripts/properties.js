// All server-sided palladium properties - properties marked with // at the end are also on the client

PalladiumEvents.registerProperties((event) => {
    if (event.getEntityType() === 'minecraft:player') {
        event.registerProperty('phantom_sy:progress', 'integer', 0); //
        event.registerProperty('phantom_sy:charge_glow', 'integer', 0); //
        event.registerProperty('phantom_sy:in_healing_phase', 'boolean', false);
        event.registerProperty('phantom_sy:force_unshift', 'boolean', false);
        event.registerProperty('phantom_sy:decay', 'integer', 999); //

        event.registerProperty('phantom_sy:titan', 'string', 'test'); //
        event.registerProperty('phantom_sy:titan_variant', 'string', 'default'); //

        event.registerProperty('phantom_sy:marks', 'integer', 0); //

        event.registerProperty('phantom_sy:can_shift', 'integer', 0);

        
        event.registerProperty('phantom_sy:odm.gas', 'integer', 0);
        event.registerProperty('phantom_sy:odm.max_gas', 'integer', 0);
    }

    if (event.getEntityType() === 'minecraft:skeleton') {
        event.registerProperty('phantom_sy:progress', 'integer', 0); //
        event.registerProperty('phantom_sy:titan', 'string', 'test'); //
        event.registerProperty('phantom_sy:titan_variant', 'string', 'default'); //
        event.registerProperty('phantom_sy:decay', 'integer', 999); //
    }
});