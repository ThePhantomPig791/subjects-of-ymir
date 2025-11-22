/*PlayerEvents.tick(event => {
    let hooks = [global.odm.getHook(event.player, 'right'), global.odm.getHook(event.player, 'left')];
    for (let i = 0; i < hooks.length; i++) {
        let hook = hooks[i];
        let side = i == 0 ? 'right' : 'left';
        if (hook == null) continue;
        if (hook.type == 'minecraft:marker') {
            if (hook.block.id == 'minecraft:air') {
                convertToProjectile(hook);
            }
        }
    }
});*/

ServerEvents.commandRegistry(event => {
    const { commands: Commands, arguments: Arguments } = event;

    event.register(
        Commands.literal('odm_hook')
            .requires(src => src.hasPermission(2))
            .executes(ctx => {
                const entity = ctx.source.entity;
                if (entity.type != 'palladium:custom_projectile' || !entity.tags.contains('phantom_sy.odm_hook')) {
                    ctx.source.sendFailure(Text.of(['This command should only be executed on/by ODM hook projectile entities!']));
                    return 0;
                }
                let side = entity.tags.contains('phantom_sy.odm_hook_right') ? 'right' : 'left';
                let owner = entity.owner;
                palladium.setProperty(owner, `phantom_sy:odm.hook_${side}.distance`, entity.position().distanceTo(owner.position()));
                global.playSoundToAll(entity, 32, 'phantom_sy:hook_land', 'PLAYERS', 1, Math.random() * 0.2 + 0.9);
                convertToMarker(entity);
                return 1;
            })
    )
});

EntityEvents.death(event => {
    if (!event.entity.player) return;
    let rightHook = global.odm.getHook(event.entity, 'right');
    if (rightHook) rightHook.discard();
    let leftHook = global.odm.getHook(event.entity, 'left');
    if (leftHook) leftHook.discard();
});

function convertToMarker(hook) {
    let marker = hook.level.createEntity('minecraft:marker');
    marker.setPos(hook.position());
    marker.mergeNbt({ data: { Owner: hook.nbt.Owner }, Tags: hook.tags });
    hook.discard();
    marker.spawn();
}
function convertToProjectile(hook) {
    let projectile = hook.level.createEntity('palladium:custom_projectile');
    projectile.setPos(hook.position());
    projectile.mergeNbt({ Owner: hook.nbt.data.Owner, Tags: hook.tags, Size: 0.4, DieOnBlockHit: false, });
    hook.discard();
    projectile.spawn();
}