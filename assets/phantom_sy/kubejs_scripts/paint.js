let $BodyPart = Java.loadClass('net.threetag.palladium.entity.BodyPart');
let Vector3f = Java.loadClass('org.joml.Vector3f');

ClientEvents.paintScreen(event => {
    const { player, level } = event.mc;

    let nearbyEntities = level.getEntities(null, player.getBoundingBox().inflate(100)).forEach(entity => {
        if (!entity.player) return;

        if (palladium.getProperty(entity, 'phantom_sy:odm.hook_left')) {
            renderLine(entity, level, 'left', event);
        };
        if (palladium.getProperty(entity, 'phantom_sy:odm.hook_right')) {
            renderLine(entity, level, 'right', event);
        };
    });
})

function renderLine(entity, level, side, event) {
    // let target = entity.position().add(entity.getLookAngle().scale(30));
    let target = new Vec3d(palladium.getProperty(entity, `phantom_sy:odm.hook_${side}.x`), palladium.getProperty(entity,  `phantom_sy:odm.hook_${side}.y`), palladium.getProperty(entity,  `phantom_sy:odm.hook_${side}.z`));
    // let pos = player.getEyePosition();
    let pos = $BodyPart.getInWorldPosition($BodyPart.byName('chest'), new Vec3f((side == 'left' ? 1 : -1) * 0.25, 0.65, 0), entity, event.delta);
    if (!pos) return;

    let scale = 0.05;
    let add = target.subtract(pos).normalize().scale(scale);
    let distance = pos.distanceTo(target);
    for (let i = 0; i < distance; i += scale) {
        level.spawnParticles(
            'phantom_sy:line',
            true,
            pos.x(),
            pos.y(),
            pos.z(),
            0,
            0,
            0,
            /*count*/ 1,
            /*speed*/ 0
        );
        pos = pos.add(add);
    }
}