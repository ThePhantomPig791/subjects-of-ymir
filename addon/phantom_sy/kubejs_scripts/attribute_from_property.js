const AttributeModifierAbility = Java.loadClass('net.threetag.palladium.power.ability.AttributeModifierAbility');

StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:attribute_from_property')
        .documentationDescription('Adds to an attribute from a palladium property value')
        .addProperty('attribute', 'string', 'minecraft:generic.armor', 'Determines which attribute should be modified.')
        .addProperty('property', 'string', 'example_property', 'The ID of the property to read from')
        .addProperty('uuid', 'string', 'e06feafc-9714-4d85-9526-fba1e5693699', 'The UUID to use for the attribute')

        .firstTick((entity, entry, holder, enabled) => {
            if (enabled) {
                entity.modifyAttribute(entry.getPropertyByName('attribute'), entry.getPropertyByName('uuid'), palladium.getProperty(entity, entry.getPropertyByName('property')), 'ADDITION');
            }
        })
        .lastTick((entity, entry, holder, enabled) => {
            if (enabled) {
                entity.removeAttribute(entry.getPropertyByName('attribute'), entry.getPropertyByName('uuid'));
            }
        })
});