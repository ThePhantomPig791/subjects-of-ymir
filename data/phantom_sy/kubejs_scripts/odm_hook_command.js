ServerEvents.commandRegistry(event => {
    const { commands: Commands, arguments: Arguments } = event;

    event.register(Commands.literal('odm_hook')
        .then(Commands.literal('entity')
            .executes(ctx => {
                const entity = ctx.source.entity;

                return 1;
            })
        )
        .then(Commands.literal('block')
            .executes(ctx => {
                const entity = ctx.source.entity;
                if (entity.type != 'palladium:custom_projectile') {
                    entity.tell('This command is meant for internal use by the ODM hooks. Please don\'t execute it yourself!');
                    return 0;
                }

                let hookStationary = entity.level.createEntity('minecraft:armor_stand');
                hookStationary.mergeNbt({ NoGravity: 1 });
                hookStationary.teleportTo(entity.x, entity.y, entity.z);
                palladium.setProperty(hookStationary, 'phantom_sy:odm_hook.owner', entity.nbt.Owner);
                // superpowerUtil.addSuperpower(hookStationary, 'phantom_sy:odm_hook');
                hookStationary.spawn();

                entity.remove("discarded");

                return 1;
            })
        )
    )
});