StartupEvents.registry('palladium:condition_serializer', (event) => {
    event.create('phantom_sy:has_strand')
        .addProperty('id', 'string', 'example_id', 'The strand ID to check')
        .test((entity, properties) => {
            return global.yna.hasStrand(entity, properties.get('id'));
        });

    event.create('phantom_sy:max_hardening')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:hardening') == (palladium.getProperty(entity, 'phantom_sy:max_hardening') + global.yna.getAdditionalMaxHardening(entity));
        });

    event.create('phantom_sy:armor_durability')
        .addProperty('part', 'string', 'arms', 'The armor part to check')
        .addProperty('min', 'integer', 1, 'The lowest acceptable durability for the specified part')
        .addProperty('max', 'integer', 9999999, 'The highest acceptable durability for the specified part')
        .test((entity, properties) => {
            return palladium.getProperty(entity, 'phantom_sy:armor.' + properties.get('part')) >= properties.get('min') && palladium.getProperty(entity, 'phantom_sy:armor.' + properties.get('part')) <= properties.get('max');
        });
});