StartupEvents.registry('palladium:condition_serializer', (event) => {
    event.create('phantom_sy:creative_mode')
        .test((entity, properties) => {
            return entity.isCreative() == true;
        })
    event.create('phantom_sy:item_on_cooldown')
        .addProperty('hand', 'string', 'mainhand', 'The hand to check --- "mainhand" or "offhand"')
        .test((entity, properties) => {
            let item;
            if (properties.get('hand') == 'mainhand') item = entity.getMainhandItem().getItem();
            else item = entity.getOffhandItem().getItem();

            if (item == null) return false;
            return entity.getCooldowns().isOnCooldown(item);
        })
})