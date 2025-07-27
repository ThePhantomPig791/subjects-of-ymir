StartupEvents.registry('palladium:condition_serializer', (event) => {
    event.create('phantom_sy:progress')
        .addProperty('min', 'integer', 0, 'Shifting progress must be above or equal to this value')
        .addProperty('max', 'integer', global.titans.MAX_SHIFT_PROGRESS, 'Shifting progress must be below or equal to this value (default value is the max shift progress)')
        .test((entity, properties) => {
            let progress = palladium.getProperty(entity, 'phantom_sy:progress');
            return progress >= properties.get('min') && progress <= properties.get('max');
        })
    event.create('phantom_sy:charge')
        .addProperty('min', 'integer', 0, 'Charge must be above or equal to this value')
        .addProperty('max', 'integer', 0, 'Charge must be below or equal to this value')
        .test((entity, properties) => {
            let progress = palladium.getProperty(entity, 'phantom_sy:charge');
            return progress >= properties.get('min') && progress <= properties.get('max');
        })
    event.create('phantom_sy:in_healing_phase')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:in_healing_phase') == true;
        })
    event.create('phantom_sy:creative_mode')
        .test((entity, properties) => {
            return entity.isCreative() == true;
        })
})