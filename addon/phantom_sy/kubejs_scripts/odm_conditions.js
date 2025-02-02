StartupEvents.registry('palladium:condition_serializer', (event) => {
    event.create('phantom_sy:odm/gas')
        .addProperty('min', 'integer', 0, 'ODM\'s gas must be above or equal to this value')
        .addProperty('max', 'integer', 200, 'ODM\'s gas must be below or equal to this value')
        .test((entity, properties) => {
            let gas = palladium.getProperty(entity, 'phantom_sy:odm.gas');
            return gas >= properties.get('min') && gas <= properties.get('max');
        });

    event.create('phantom_sy:odm/handles')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:odm.handles') == true;
        });

    event.create('phantom_sy:odm/handle_blades')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:odm.handle_blades') == true;
        });

    event.create('phantom_sy:odm/sheath_blades')
        .addProperty('min', 'integer', 0, 'Sheath\'s blade count must be above or equal to this value')
        .addProperty('max', 'integer', 8, 'Sheath\'s blade count must be below or equal to this value')
        .test((entity, properties) => {
            let blades = palladium.getProperty(entity, 'phantom_sy:odm.sheath_blades');
            return blades >= properties.get('min') && blades <= properties.get('max');
        });
});