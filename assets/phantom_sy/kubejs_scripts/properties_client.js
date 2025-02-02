// All client-sided palladium properties - all of these are also obviously on the server

PalladiumEvents.registerPropertiesClientSided((event) => {
    if (event.getEntityType() === 'minecraft:player') {
        event.registerProperty('phantom_sy:is_shifted', 'boolean', false);
        event.registerProperty('phantom_sy:is_shifing', 'boolean', false);

        event.registerProperty('phantom_sy:shift_progress', 'integer', 0);

        event.registerProperty('phantom_sy:titan', 'string', '');
        event.registerProperty('phantom_sy:titan_skin', 'string', '');
        event.registerProperty('phantom_sy:titan_eye_color', 'string', 'FFFFFF');
        event.registerProperty('phantom_sy:titan_hair_color', 'string', 'FFFFFF');

        event.registerProperty('phantom_sy:titan_y_displacement', 'integer', 0);

        event.registerProperty('phantom_sy:charge_glow', 'integer', 0);
        
        event.registerProperty('phantom_sy:marks', 'integer', 0);
        
        event.registerProperty('phantom_sy:hardening', 'integer', 0);

        event.registerProperty('phantom_sy:armor.arms', 'integer', 0); // think of these numbers like durability
        event.registerProperty('phantom_sy:armor.legs', 'integer', 0);
        event.registerProperty('phantom_sy:armor.chest', 'integer', 0);
        event.registerProperty('phantom_sy:armor.head', 'integer', 0);

        
        event.registerProperty('phantom_sy:odm.handles', 'boolean', false);
        event.registerProperty('phantom_sy:odm.sheath_blades', 'integer', 0);
        event.registerProperty('phantom_sy:odm.handle_blades', 'boolean', false);
    }

    if (event.getEntityType() === 'minecraft:skeleton') {
        event.registerProperty('phantom_sy:skeleton.titan', 'string', '');
        event.registerProperty('phantom_sy:skeleton.titan_skin', 'string', '');
        event.registerProperty('phantom_sy:skeleton.shift_progress', 'integer', 0);
        event.registerProperty('phantom_sy:skeleton.titan_eye_color', 'string', '');
        event.registerProperty('phantom_sy:skeleton.titan_hair_color', 'string', '');
        event.registerProperty('phantom_sy:skeleton.decay', 'integer', 0);
        event.registerProperty('phantom_sy:skeleton.hardening', 'integer', 0);

        event.registerProperty('phantom_sy:skeleton.armor.arms', 'integer', 0);
        event.registerProperty('phantom_sy:skeleton.armor.legs', 'integer', 0);
        event.registerProperty('phantom_sy:skeleton.armor.chest', 'integer', 0);
        event.registerProperty('phantom_sy:skeleton.armor.head', 'integer', 0);
    }
});