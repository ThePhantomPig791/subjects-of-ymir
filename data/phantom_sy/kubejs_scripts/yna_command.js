ServerEvents.commandRegistry(event => {
    const { commands: Commands, arguments: Arguments } = event;

    event.register(Commands.literal('yna')
        .then(Commands.argument('entity', Arguments.ENTITY.create(event))
            .then(Commands.literal('list')
                .executes(ctx => {
                    const player = Arguments.ENTITY.getResult(ctx, 'entity');
                    const source = ctx.source.player;

                    if (player.persistentData.yna_strands == null) {
                        player.persistentData.yna_strands = [];
                    }

                    if (player.persistentData.yna_strands.length == 0) {
                        source.tell(`${player.name.getString()} has no YNA strands`);
                        return 0;
                    } else {
                        source.tell(`${player.name.getString()} has the following YNA strands: `);
                        for (var i = 0; i < player.persistentData.yna_strands.length; i++) {
                            source.tell(' | ' + player.persistentData.yna_strands[i]);
                        }
                        return 1;
                    }
                })
            )
            .then(Commands.literal('add')
                .then(Commands.argument('id', Arguments.STRING.create(event))
                    .suggests((ctx, builder) => {
                        for (var i = 0; i < global.yna.strands.length; i++) {
                            if (!global.yna.hasStrand(Arguments.ENTITY.getResult(ctx, 'entity'), global.yna.strands[i].id)) builder.suggest(global.yna.strands[i].id);
                        }
                        return builder.buildFuture();
                    })
                    .executes(ctx => {
                        const player = Arguments.ENTITY.getResult(ctx, 'entity');
                        const source = ctx.source.player;
                        const strand = Arguments.STRING.getResult(ctx, 'id');

                        if (player.persistentData.yna_strands == null) {
                            player.persistentData.yna_strands = [];
                        }

                        if (!global.yna.isValidStrand(strand)) {
                            source.tell(`Could not add YNA strand to ${player.name.getString()}; unknown strand ID "${strand}"`);
                            return 0;
                        }

                        if (global.yna.hasStrand(player, strand)) {
                            source.tell(`${player.name.getString()} already has YNA strand "${strand}"`);
                            return 0;
                        }

                        player.persistentData.yna_strands.push(strand);
                        source.tell(`Added YNA strand "${strand}" to ${player.name.getString()}`);
                        return 1;
                    })
                )
            )
            .then(Commands.literal('remove')
                .then(Commands.argument('id', Arguments.STRING.create(event))
                    .suggests((ctx, builder) => {
                        const player = Arguments.ENTITY.getResult(ctx, 'entity');
                        for (var i = 0; i < player.persistentData.yna_strands.length; i++) {
                            builder.suggest(player.persistentData.yna_strands[i]);
                        }
                        return builder.buildFuture();
                    })
                    .executes(ctx => {
                        const player = Arguments.ENTITY.getResult(ctx, 'entity');
                        const source = ctx.source.player;
                        const strand = Arguments.STRING.getResult(ctx, 'id');

                        if (!global.yna.isValidStrand(strand)) {
                            source.tell(`Could not remove YNA strand from ${player.name.getString()}; unknown strand ID "${strand}"`);
                            return 0;
                        }

                        if (!global.yna.hasStrand(player, strand)) {
                            source.tell(`${player.name.getString()} did not have YNA strand "${strand}"`);
                            return 0;
                        }

                        for (var i = 0; i < player.persistentData.yna_strands.length; i++) {
                            var yna_strand = player.persistentData.yna_strands[i];
                            if (yna_strand == strand) {
                                player.persistentData.yna_strands.remove(i);
                                break;
                            }
                        }
                        source.tell(`Removed YNA strand "${strand}" from ${player.name.getString()}`);
                        return 1;
                    })
                )
            )
            .then(Commands.literal('clear')
                .executes(ctx => {
                    const player = Arguments.ENTITY.getResult(ctx, 'entity');
                    const source = ctx.source.player;

                    if (player.persistentData.yna_strands == null || player.persistentData.yna_strands.length == 0) {
                        player.persistentData.yna_strands = [];
                        source.tell(`${player.name.getString()} did not have any YNA strands`);
                        return 0;
                    }
                    
                    player.persistentData.yna_strands = [];
                    source.tell(`Removed all YNA strands from ${player.name.getString()}`);
                    return 1;
                })
            )
        )
    )
});