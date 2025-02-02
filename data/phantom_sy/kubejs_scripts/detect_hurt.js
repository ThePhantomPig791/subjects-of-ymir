EntityEvents.hurt(event => {
    const entity = event.entity;
    if (!entity.isPlayer() || event.damage <= 2 || !abilityUtil['isTypeEnabled(net.minecraft.class_1309,net.minecraft.class_2960)'](entity, 'phantom_sy:damage_detection') || palladium.getProperty(entity, 'phantom_sy:is_shifted')) return;
    palladium.setProperty(entity, 'phantom_sy:can_shift', 200);
});