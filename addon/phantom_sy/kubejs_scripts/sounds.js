const PlayerUtil = Java.loadClass('net.threetag.palladium.util.PlayerUtil');

global.playSoundToAll = function(entity, range, sound, category, volume, pitch) {
    PlayerUtil['playSoundToAll(net.minecraft.class_1937,double,double,double,double,net.minecraft.class_3414,net.minecraft.class_3419,float,float)'](entity.level, entity.x, entity.y, entity.z, range, sound, category, volume, pitch);
}
global.playSoundLocal = function(player, sound, category, volume, pitch) {
    PlayerUtil['playSound(net.minecraft.class_1657,double,double,double,net.minecraft.class_3414,net.minecraft.class_3419,float,float)'](player, player.x, player.y, player.z, sound, category, volume, pitch);
}
global.playSoundFromPos = function(level, x, y, z, range, sound, category, volume, pitch) {
    PlayerUtil['playSoundToAll(net.minecraft.class_1937,double,double,double,double,net.minecraft.class_3414,net.minecraft.class_3419,float,float)'](level, x, y, z, range, sound, category, volume, pitch);
}

StartupEvents.registry('sound_event', event =>{
    event.create('phantom_sy:shift');
    event.create('phantom_sy:shift_local');
    event.create('phantom_sy:steam');
    event.create('phantom_sy:iceburst');
    event.create('phantom_sy:gas');
    event.create('phantom_sy:reel');
    event.create('phantom_sy:shoot_hook');
    event.create('phantom_sy:hook_land');
})