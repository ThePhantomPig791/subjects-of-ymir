NetworkEvents.dataReceived('phantom_sy:vfx_explosion', event => {
    switch (event.data.type) {
        case 'shockwaveRing':
            global.sy_vfx.explosions.shockwaveRing(client.level, event.data.x, event.data.y, event.data.z, event.data.particleType);
            break;
        case 'smallExplosion':
            global.sy_vfx.explosions.smallExplosion(Client.level, event.data.x, event.data.y, event.data.z);
            break;
    }
});