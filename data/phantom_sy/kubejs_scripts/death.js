EntityEvents.death(event => {
    const entity = event.entity;
    if (!entity.isPlayer() || palladium.getProperty(entity, 'phantom_sy:progress') == 0) return;

    palladium.setProperty(entity, 'phantom_sy:force_unshift', true);
    
    entity.setHealth(entity.getMaxHealth());

    entity.removeAllEffects();
    entity.potionEffects.add('minecraft:blindness', 300, 0, true, false);
    entity.potionEffects.add('minecraft:slowness', 400, 1, true, false);
    entity.potionEffects.add('minecraft:weakness', 300, 0, true, false);
    entity.potionEffects.add('minecraft:mining_fatigue', 400, 0, true, false);

    palladium.setProperty(entity, 'phantom_sy:marks', palladium.getProperty(entity, 'phantom_sy:marks') + 300);

    event.cancel();
});