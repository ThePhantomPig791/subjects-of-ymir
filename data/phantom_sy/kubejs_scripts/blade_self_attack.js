EntityEvents.hurt(event => {
    if (event.source?.player == null || event.source.player == event.entity) return;
    if (event.source.player.mainHandItem.id != 'phantom_sy:blade') return;
    event.source.player.attack(event.source, 3);
})