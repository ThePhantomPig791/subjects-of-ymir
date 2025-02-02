const Mth = Java.loadClass('net.minecraft.util.Mth');

global.sy_vfx = {};


global.sy_vfx.explosions = {};
global.sy_vfx.explosions.shockwaveRing = function(clientLevel, x, y, z, type) {
    for (let i = 0; i < Mth.TWO_PI; i += (Mth.PI / 20)) {
        clientLevel.addParticle(type, true, x, y, z, Math.cos(i), -0.1, Math.sin(i));
    }
}
global.sy_vfx.explosions.smallExplosion = function(clientLevel, x, y, z) {
    y++;
    for (let i = 0; i < Mth.TWO_PI; i += (Mth.PI / 25)) {
        clientLevel.addParticle('phantom_sy:explosion', true, x, y, z,
            randBetween(-0.5, 0.5) * Math.cos(i),
            randBetween(0.1, 1.5),
            randBetween(-0.5, 0.5) * Math.sin(i));
    }
    global.sy_vfx.explosions.shockwaveRing(clientLevel, x, y, z, 'phantom_sy:large_smoke');
    global.sy_vfx.explosions.shockwaveRing(clientLevel, x, y, z, 'minecraft:cloud');
}

function randBetween(min, upper) {
    return upper * Math.random() + min;
}