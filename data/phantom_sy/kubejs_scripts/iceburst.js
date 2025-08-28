const $ClientboundSetEntityMotionPacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket');

BlockEvents.broken('phantom_sy:iceburst_ore', event => {
    if (event.player.mainHandItem.hasEnchantment('minecraft:silk_touch', 1)) return;
    const { block, level } = event;
    if (event.player.isCreative()) return;
    let center = block.pos.center;
    explode(center, level, 10, AABB.ofSize(center, 1, 1, 1).inflate(10));
})
LevelEvents.afterExplosion(event => {
    event.getAffectedBlocks().forEach(block => {
        if (block.id != 'phantom_sy:iceburst_ore') return;
        let center = block.pos.center;
        explode(center, event.level, 10, AABB.ofSize(center, 1, 1, 1).inflate(10));
    })
})

ItemEvents.rightClicked('phantom_sy:raw_iceburst', event => {
    if (event.hand == 'OFF_HAND' && (event.player.getOffHandItem().id != 'phantom_sy:raw_iceburst' || event.player.getMainHandItem().id == 'phantom_sy:gas_canister')) return;
    const { player, item } = event;
    let pos = player.rayTrace().hit ?? player.getEyePosition().add(player.getLookAngle().scale(3.5));
    explode(
        pos,
        player.level,
        3,
        AABB.ofSize(pos, 6, 6, 6)
    );
    if (!player.isCreative()) item.count--;
    player.swing(event.hand, true);
})

function explode(pos, level, strength, box) {
    level.getEntitiesWithin(box).forEach(e => {
        e.addDeltaMovement(pos
            .subtract(e.getEyePosition())
            .normalize()
            .reverse()
            .scale(Math.min(2, strength / pos.distanceToSqr(e.getEyePosition())))
        );
        if (e.isPlayer()) {
            e.connection.send(new $ClientboundSetEntityMotionPacket(e));
        }
    });
    level.sendParticles(
        'minecraft:cloud',
        pos.x(),
        pos.y(),
        pos.z(),
        strength,
        0.5, 0.5, 0.5,
        strength / 12
    )
    global.playSoundFromPos(level, pos.x(), pos.y(), pos.z(), 16, 'phantom_sy:iceburst', 'BLOCKS', strength / 6, 1 - (0.1 * strength / 8));
}