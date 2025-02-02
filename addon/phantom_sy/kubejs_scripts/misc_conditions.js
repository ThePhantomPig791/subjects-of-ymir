

StartupEvents.registry('palladium:condition_serializer', (event) => {
    event.create('phantom_sy:full_health')
        .test((entity, properties) => {
            return entity.getHealth() == entity.getMaxHealth();
        });

    event.create('phantom_sy:hunger_level')
        .addProperty('min', 'integer', 0, 'The lowest hunger level that will return true')
        .addProperty('max', 'integer', 20, 'The highest hunger level that will return true')
        .test((entity, properties) => {
            const foodLevel = entity.getFoodLevel();
            return foodLevel >= properties.get('min') && foodLevel <= properties.get('max');
        });
});