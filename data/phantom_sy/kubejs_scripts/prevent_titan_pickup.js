ItemEvents.canPickUp(event => {
    if (palladium.getProperty(event.player, 'phantom_sy:progress') > 0) event.cancel();
})