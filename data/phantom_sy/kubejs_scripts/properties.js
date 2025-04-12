// All server-sided palladium properties - properties marked with // at the end are also on the client

PalladiumEvents.registerProperties((event) => {
    if (event.getEntityType() === 'minecraft:player') {
        event.registerProperty('phantom_sy:is_shifted', 'boolean', false); //
        event.registerProperty('phantom_sy:is_shifing', 'boolean', false); //

        event.registerProperty('phantom_sy:shift_progress', 'integer', 0); //
        event.registerProperty('phantom_sy:time_to_shift', 'integer', 60);
        
        event.registerProperty('phantom_sy:titan', 'string', ''); //
        event.registerProperty('phantom_sy:titan_skin', 'string', ''); //
        event.registerProperty('phantom_sy:titan_eye_color', 'string', 'FFFFFF'); //
        event.registerProperty('phantom_sy:titan_hair_color', 'string', 'FFFFFF'); //

        event.registerProperty('phantom_sy:titan_y_displacement', 'integer', 0); //
        event.registerProperty('phantom_sy:titan_width', 'float', 1.0);
        event.registerProperty('phantom_sy:titan_height', 'float', 1.0);
        event.registerProperty('phantom_sy:titan_eye_height', 'float', 1.0);
        event.registerProperty('phantom_sy:titan_motion', 'float', 1.0);
        event.registerProperty('phantom_sy:titan_third_person', 'float', 1.0);
        event.registerProperty('phantom_sy:titan_health', 'float', 1.0);
        event.registerProperty('phantom_sy:titan_reach', 'float', 1.0);

        event.registerProperty('phantom_sy:titan_attack_damage', 'integer', 1.0);

        event.registerProperty('phantom_sy:can_shift', 'integer', 0);

        event.registerProperty('phantom_sy:charge_glow', 'integer', 0); //
        event.registerProperty('phantom_sy:max_charge', 'integer', (3 * 20));
        event.registerProperty('phantom_sy:is_charging_shift', 'boolean', false);
        
        event.registerProperty('phantom_sy:marks', 'integer', 0); //

        event.registerProperty('phantom_sy:cancel_shift', 'boolean', false);

        event.registerProperty('phantom_sy:hardening', 'integer', 0); //
        event.registerProperty('phantom_sy:max_hardening', 'integer', 3);

        event.registerProperty('phantom_sy:armor.arms', 'integer', 0); //
        event.registerProperty('phantom_sy:armor.legs', 'integer', 0); //
        event.registerProperty('phantom_sy:armor.chest', 'integer', 0); //
        event.registerProperty('phantom_sy:armor.head', 'integer', 0); //

        event.registerProperty('phantom_sy:armor_invuln', 'integer', 0);


        event.registerProperty('phantom_sy:odm.gas', 'integer', 0);
        event.registerProperty('phantom_sy:odm.handles', 'boolean', false); //
        event.registerProperty('phantom_sy:odm.sheath_blades', 'integer', 0); //
        event.registerProperty('phantom_sy:odm.handle_blades', 'boolean', false); //

        event.registerProperty('phantom_sy:odm.hook.right.x', 'double', null);
        event.registerProperty('phantom_sy:odm.hook.right.y', 'double', null);
        event.registerProperty('phantom_sy:odm.hook.right.z', 'double', null);
    }

    if (event.getEntityType() === 'minecraft:skeleton') {        
        event.registerProperty('phantom_sy:skeleton.titan', 'string', ''); //
        event.registerProperty('phantom_sy:skeleton.titan_skin', 'string', ''); //
        event.registerProperty('phantom_sy:skeleton.shift_progress', 'integer', 0); //
        event.registerProperty('phantom_sy:skeleton.titan_eye_color', 'string', ''); //
        event.registerProperty('phantom_sy:skeleton.titan_hair_color', 'string', ''); //
        event.registerProperty('phantom_sy:skeleton.decay', 'integer', 0); //
        event.registerProperty('phantom_sy:skeleton.titan_height', 'float', 1.0);
        event.registerProperty('phantom_sy:skeleton.hardening', 'integer', 0); //

        event.registerProperty('phantom_sy:skeleton.armor.arms', 'integer', 0); //
        event.registerProperty('phantom_sy:skeleton.armor.legs', 'integer', 0); //
        event.registerProperty('phantom_sy:skeleton.armor.chest', 'integer', 0); //
        event.registerProperty('phantom_sy:skeleton.armor.head', 'integer', 0); //
    }
});