let $DamageTypeTags = Java.loadClass('net.minecraft.tags.DamageTypeTags');

let blockedThisTick = null;
EntityEvents.hurt(event => {
    const { entity, source } = event;
    if (blockedThisTick == entity.uuid) return;

    if (event.damage > 2 && palladium.getProperty(entity, 'phantom_sy:progress') == 0) {
        palladium.setProperty(entity, 'phantom_sy:can_shift', 200);
    }

    if (palladium.abilities.isEnabled(entity, 'phantom_sy:shifter', 'blocking')) {
        if (isDamageSourceBlocked(entity, source)) {
            let partialDamage = event.damage * 0.2;
            blockedThisTick = entity.uuid;
            entity.attack(source, partialDamage);
            event.cancel();
        }
    }
    if (blockedThisTick) blockedThisTick = false;
});


function isDamageSourceBlocked(entity, source) {
    let directEntity = source.immediate;
    if (directEntity == null) return false;
    let pierce = false;
    if ((directEntity.type == 'minecraft:arrow' || directEntity.type == 'minecraft:spectral_arrow') && directEntity.getPierceLevel() > 0) {
        pierce = true;
    }
    if (source.is($DamageTypeTags.BYPASSES_SHIELD) || pierce) return false;
    let sourcePos = source.getSourcePosition();
    if (sourcePos == null) return;
    let flatLookVec = entity.getLookAngle().multiply(1, 0, 1).normalize();
    let vecTo = sourcePos.vectorTo(entity.position()).multiply(1, 0, 1).normalize();
    return vecTo.dot(flatLookVec) < 0.0;

}