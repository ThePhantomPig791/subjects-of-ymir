ServerEvents.commandRegistry(event => {
    const { commands: Commands, arguments: Arguments } = event;

    event.register(Commands.literal('titan')
        .then(Commands.literal('set')
            .then(Commands.argument('titan_id', Arguments.STRING.create(event))
                .suggests((ctx, builder) => {
                    for (let key in global.titans.list) {
                        builder.suggest(key);
                    }
                    return builder.buildFuture();
                })
                .executes(ctx => {
                    const player = ctx.source.player;
                    const oldTitan = palladium.getProperty(player, 'phantom_sy:titan');
                    const newTitan = Arguments.STRING.getResult(ctx, 'titan_id');

                    palladium.setProperty(player, 'phantom_sy:titan', newTitan);
                    if (oldTitan != newTitan) {
                        palladium.setProperty(player, 'phantom_sy:titan_variant', global.titans.list[newTitan].variants[0]);
                        ctx.source.sendSuccess(Text.of([
                            'Set ',
                            player.name,
                            '\'s titan to "',
                            newTitan,
                            '" (previously "',
                            oldTitan,
                            '")'
                        ]), true);
                        return 1;
                    } else {
                        ctx.source.sendFailure(Text.of([
                            'Nothing changed; ',
                            player.name,
                            '\'s titan was already "',
                            newTitan,
                            '"'
                        ]));
                        return 0;
                    }
                })
            )
        )
        .then(Commands.literal('set_variant')
            .then(Commands.argument('variant_id', Arguments.STRING.create(event))
                .suggests((ctx, builder) => {
                    let titan = global.titans.list[(palladium.getProperty(ctx.source.player, 'phantom_sy:titan'))];
                    for (let i = 0; i < titan.variants.length; i++) {
                        builder.suggest(titan.variants[i]);
                    }
                    return builder.buildFuture();
                })
                .executes(ctx => {
                    const player = ctx.source.player;
                    const oldTitanVariant = palladium.getProperty(player, 'phantom_sy:titan_variant');
                    const newTitanVariant = Arguments.STRING.getResult(ctx, 'variant_id');

                    if (!global.titans.list[(palladium.getProperty(ctx.source.player, 'phantom_sy:titan'))].variants.includes(newTitanVariant)) {
                        ctx.source.sendFailure(Text.of([
                            'Unable to set variant for ',
                            player.name,
                            ': "',
                            newTitanVariant,
                            '" is not a variant for titan "',
                            palladium.getProperty(ctx.source.player, 'phantom_sy:titan'),
                            '"'
                        ]));
                    }

                    palladium.setProperty(player, 'phantom_sy:titan_variant', newTitanVariant);
                    if (oldTitanVariant != newTitanVariant) {
                        player.tell(`Set ${player.name.getString()}'s titan variant to "${newTitanVariant}" (previously "${oldTitanVariant}")`);
                        ctx.source.sendSuccess(Text.of([
                            'Set ',
                            player.name,
                            '\'s titan variant to "',
                            newTitanVariant,
                            '" (previously "',
                            oldTitanVariant,
                            '")'
                        ]), true);
                        return 1;
                    } else {
                        ctx.source.sendFailure(Text.of([
                            'Nothing changed; ',
                            player.name,
                            '\'s titan variant was already "',
                            newTitanVariant,
                            '"'
                        ]));
                        return 0;
                    }
                })
            )
        )
        .then(Commands.literal('recharge')
            .executes(ctx => {
                const player = ctx.source.player;
                const marks = palladium.getProperty(player, 'phantom_sy:marks');

                if (marks == 0) {
                    ctx.source.sendFailure(Text.of([
                        'Nothing changed; ',
                        player.name,
                        '\'s titan shifting is already fully charged'
                    ]));
                    return 0;
                } else {
                    ctx.source.sendSuccess(Text.of([
                        'Recharged ',
                        player.name,
                        '\'s titan shifting'
                    ]), true);
                    palladium.setProperty(player, 'phantom_sy:marks', 0);
                    return 1;
                }
            })
        )
    )
});