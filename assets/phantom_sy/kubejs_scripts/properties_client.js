// All client-sided palladium properties

PalladiumEvents.registerPropertiesClientSided((event) => {
    if (event.getEntityType() === 'minecraft:player') {
        event.registerProperty('phantom_sy:progress', 'integer', 0);
        event.registerProperty('phantom_sy:charge_glow', 'integer', 0);
        event.registerProperty('phantom_sy:decay', 'integer', 999);

        event.registerProperty('phantom_sy:titan', 'string', 'test');
        event.registerProperty('phantom_sy:titan_variant', 'string', 'default');

        event.registerProperty('phantom_sy:marks', 'integer', 0);


        event.registerProperty('phantom_sy:odm.hook_left', 'boolean', false);
        event.registerProperty('phantom_sy:odm.hook_left.x', 'float', 0);
        event.registerProperty('phantom_sy:odm.hook_left.y', 'float', 0);
        event.registerProperty('phantom_sy:odm.hook_left.z', 'float', 0);
        event.registerProperty('phantom_sy:odm.hook_right', 'boolean', false);
        event.registerProperty('phantom_sy:odm.hook_right.x', 'float', 0);
        event.registerProperty('phantom_sy:odm.hook_right.y', 'float', 0);
        event.registerProperty('phantom_sy:odm.hook_right.z', 'float', 0);
    }

    if (event.getEntityType() === 'minecraft:skeleton') {
        event.registerProperty('phantom_sy:progress', 'integer', 0);
        event.registerProperty('phantom_sy:titan', 'string', 'test');
        event.registerProperty('phantom_sy:titan_variant', 'string', 'default');
        event.registerProperty('phantom_sy:decay', 'integer', 999);
    }
});