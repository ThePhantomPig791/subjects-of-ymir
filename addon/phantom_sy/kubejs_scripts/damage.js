StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:damage')
        .documentationDescription('Deals damage')
        .addProperty('amount', 'integer', 2, 'The amount of damage to deal - a value of -1 will use the entity\'s attack damage attribute value instead')
        
        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                var amount = entry.getPropertyByName('amount');
                if (amount == -1) {
                    amount = entity.getAttributeTotalValue('minecraft:generic.attack_damage');
                }
                entity.attack(amount);
            }
        })
})