const SoundEvents = Java.loadClass('net.minecraft.sounds.SoundEvents');

StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:titan_skeleton')
        .documentationDescription('Logic for titan skeletons, don\'t give this to a player')

        .tick((entity, entry, holder, enabled) => {
            entity.mergeNbt({ Fire: -20 });

            const decay = palladium.getProperty(entity, 'phantom_sy:skeleton.decay');
            const height = palladium.getProperty(entity, 'phantom_sy:skeleton.titan_height');

            if (decay < 20 || entity.getHealth() == 0) entity.remove('discarded');

            if (entity.age % 3 == 0) { // every 3 ticks
                palladium.setProperty(entity, 'phantom_sy:skeleton.decay', decay - 1);
            }

            if (entity.age % 2 == 0) { // every 2 ticks
                entity.playSound(SoundEvents.FIRE_EXTINGUISH, 1, 1);
            }

            entity.level.sendParticles(
                'minecraft:campfire_cosy_smoke',
                entity.x,
                entity.y - (height / 2),
                entity.z,
                /*count*/ 5,
                entity.getBoundingBox().getXsize() / 2.5,
                height / 2.5,
                entity.getBoundingBox().getZsize() / 2.5,
                /*speed*/ 0.3
            );

            entity.level.sendParticles(
                'minecraft:ash',
                entity.x,
                entity.y - (height / 2),
                entity.z,
                /*count*/ 30,
                entity.getBoundingBox().getXsize() * 2.5,
                height * 2.5,
                entity.getBoundingBox().getZsize() * 2.5,
                /*speed*/ 1
            );

            entity.level.sendParticles(
                'minecraft:cloud',
                entity.x,
                entity.y - (height / 2),
                entity.z,
                /*count*/ 30,
                entity.getBoundingBox().getXsize() * 2.5,
                height * 2.5,
                entity.getBoundingBox().getZsize() * 2.5,
                /*speed*/ 0.5
            );
        })
})