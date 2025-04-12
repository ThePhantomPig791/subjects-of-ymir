let NbtUtils = Java.loadClass("net.minecraft.nbt.NbtUtils");

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
                    console.log('fail')
                    return 0;
                }

                let owner = entity.server.playerList.getPlayer(NbtUtils.loadUUID(entity.nbt.Owner));
                palladium.setProperty(owner, 'phantom_sy:odm.hook.right.x', entity.x);
                palladium.setProperty(owner, 'phantom_sy:odm.hook.right.y', entity.y);
                palladium.setProperty(owner, 'phantom_sy:odm.hook.right.z', entity.z);
                owner.tell('jhio')
                console.log(owner)
                console.log(entity.x)
                console.log(palladium.getProperty(owner, 'phantom_sy:odm.hook.right.x'));

                entity.discard();

                console.log('hit')

                return 1;
            })
        )
    )
});