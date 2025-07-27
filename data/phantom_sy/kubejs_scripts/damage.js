EntityEvents.hurt(event => {
    const entity = event.entity;
    if (event.damage < 2) return;
    if (!palladium.superpowers.hasSuperpower(entity, 'phantom_sy:shifter')) return;
    if (palladium.getProperty(entity, 'phantom_sy:progress') != 0) return;
    palladium.setProperty(entity, 'phantom_sy:can_shift', 200);
});