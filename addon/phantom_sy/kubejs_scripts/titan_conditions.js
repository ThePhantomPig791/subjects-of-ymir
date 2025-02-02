StartupEvents.registry('palladium:condition_serializer', (event) => {
    event.create('phantom_sy:is_shifted')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:is_shifted') == true;
        });

    event.create('phantom_sy:is_shifting')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:is_shifting') == true;
        });

    event.create('phantom_sy:shift_progress')
        .addProperty('min', 'integer', 0, 'Shifting progress must be above or equal to this value')
        .addProperty('max', 'integer', global.titans.titan_shift_max_progress, 'Shifting progress must be below or equal to this value (default value is the max shift progress)')
        .test((entity, properties) => {
            let progress = palladium.getProperty(entity, 'phantom_sy:shift_progress');
            return progress >= properties.get('min') && progress <= properties.get('max');
        });

    event.create('phantom_sy:has_titan_skin')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:titan') != null
                && palladium.getProperty(entity, 'phantom_sy:titan') != ''
                && palladium.getProperty(entity, 'phantom_sy:titan_skin') != null
                && palladium.getProperty(entity, 'phantom_sy:titan_skin') != '';
        });

    event.create('phantom_sy:can_shift')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:can_shift') > 0 && palladium.getProperty(entity, 'phantom_sy:marks') < 80;
        });

    event.create('phantom_sy:has_skeleton_titan_skin')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:skeleton.titan_skin') != null && palladium.getProperty(entity, 'phantom_sy:skeleton.titan_skin') != '';
        });

    event.create('phantom_sy:is_charging_shift')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:is_charging_shift') == true;
        });
});