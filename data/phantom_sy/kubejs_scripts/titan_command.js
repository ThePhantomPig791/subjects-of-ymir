ServerEvents.commandRegistry(event => {
    const { commands: Commands, arguments: Arguments } = event;

    event.register(Commands.literal('titan')
        .then(Commands.literal('set')
            .then(Commands.argument('titan_id', Arguments.STRING.create(event))
                .suggests((ctx, builder) => {
                    for (let i = 0; i < global.titans.list.length; i++) {
                        builder.suggest(global.titans.list[i].id);
                    }
                    return builder.buildFuture();
                })
                .executes(ctx => {
                    const player = ctx.source.player;
                    const oldTitan = palladium.getProperty(player, 'phantom_sy:titan');
                    const newTitan = Arguments.STRING.getResult(ctx, 'titan_id');

                    palladium.setProperty(player, 'phantom_sy:titan', newTitan);
                    if (oldTitan != newTitan) {
                        palladium.setProperty(player, 'phantom_sy:titan_skin', global.titans.getTitanFromId(newTitan).skins[0]);
                        player.tell(`Set ${player.name.getString()}'s titan to "${newTitan}" (previously "${oldTitan}")`);
                        return 1;
                    } else {
                        player.tell(`Nothing changed; ${player.name.getString()}'s titan was already "${newTitan}"`);
                        return 0;
                    }
                })
            )
        )
        .then(Commands.literal('set_skin')
            .then(Commands.argument('skin_id', Arguments.STRING.create(event))
                .suggests((ctx, builder) => {
                    let titan = global.titans.getTitanFromId(palladium.getProperty(ctx.source.player, 'phantom_sy:titan'));
                    console.log(titan.skins)
                    for (let i = 0; i < titan.skins.length; i++) {
                        builder.suggest(titan.skins[i]);
                    }
                    return builder.buildFuture();
                })
                .executes(ctx => {
                    const player = ctx.source.player;
                    const oldTitanSkin = palladium.getProperty(player, 'phantom_sy:titan_skin');
                    const newTitanSkin = Arguments.STRING.getResult(ctx, 'skin_id');

                    palladium.setProperty(player, 'phantom_sy:titan_skin', newTitanSkin);
                    if (oldTitanSkin != newTitanSkin) {
                        player.tell(`Set ${player.name.getString()}'s titan skin to "${newTitanSkin}" (previously "${oldTitanSkin}")`);
                        return 1;
                    } else {
                        player.tell(`Nothing changed; ${player.name.getString()}'s titan skin was already "${newTitanSkin}"`);
                        return 0;
                    }
                })
            )
        )
        .then(Commands.literal('y_offset')
            .then(Commands.argument('offset', Arguments.INTEGER.create(event))
                .executes(ctx => {
                    const player = ctx.source.player;
                    const offset = Arguments.INTEGER.getResult(ctx, 'offset');

                    palladium.setProperty(player, 'phantom_sy:titan_y_displacement', offset);
                    player.tell(`Set ${player.name.getString()}'s titan offset to ${offset}`);
                    return 1;
                })
            )
        )
        .then(Commands.literal('scale')
            .then(Commands.argument('width', Arguments.FLOAT.create(event))
                .then(Commands.argument('height', Arguments.FLOAT.create(event))
                    .then(Commands.argument('eye_height', Arguments.FLOAT.create(event))
                        .then(Commands.argument('motion', Arguments.FLOAT.create(event))
                            .then(Commands.argument('third_person', Arguments.FLOAT.create(event))
                                .then(Commands.argument('health', Arguments.FLOAT.create(event))
                                    .then(Commands.argument('reach', Arguments.FLOAT.create(event))
                                        .executes(ctx => {
                                            const player = ctx.source.player;
                                            const width = Arguments.FLOAT.getResult(ctx, 'width');
                                            const height = Arguments.FLOAT.getResult(ctx, 'height');
                                            const eyeHeight = Arguments.FLOAT.getResult(ctx, 'eye_height');
                                            const motion = Arguments.FLOAT.getResult(ctx, 'motion');
                                            const thirdPerson = Arguments.FLOAT.getResult(ctx, 'third_person');
                                            const health = Arguments.FLOAT.getResult(ctx, 'health');
                                            const reach = Arguments.FLOAT.getResult(ctx, 'reach');

                                            palladium.setProperty(player, 'phantom_sy:titan_width', width);
                                            palladium.setProperty(player, 'phantom_sy:titan_height', height);
                                            palladium.setProperty(player, 'phantom_sy:titan_eye_height', eyeHeight);
                                            palladium.setProperty(player, 'phantom_sy:titan_motion', motion);
                                            palladium.setProperty(player, 'phantom_sy:titan_third_person', thirdPerson);
                                            palladium.setProperty(player, 'phantom_sy:titan_health', health);
                                            palladium.setProperty(player, 'phantom_sy:titan_reach', reach);

                                            player.tell(`Set ${player.name.getString()}'s titan scale to (${width}, ${height}, ${eyeHeight}, ${motion}, ${thirdPerson})`);
                                            return 1;
                                        })
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        .then(Commands.literal('max_charge')
            .then(Commands.argument('max', Arguments.INTEGER.create(event))
                .executes(ctx => {
                    const player = ctx.source.player;
                    const max = Arguments.INTEGER.getResult(ctx, 'max');

                    palladium.setProperty(player, 'phantom_sy:max_charge', max);
                    palladium.setProperty(player, 'phantom_sy:charge_glow', 0);
                    player.tell(`Set ${player.name.getString()}'s max shifting charge to ${max}`);
                    return 1;
                })
            )
            .then(Commands.literal('get')
                .executes(ctx => {
                    const player = ctx.source.player;
                    const max = palladium.getProperty(player, 'phantom_sy:max_charge');
                    const add = global.yna.getAdditionalMaxCharge(player);

                    if (add == 0) {
                        player.tell(`${player.name.getString()}'s max shifting charge is ${max}`);
                    } else {
                        player.tell(`${player.name.getString()}'s max shifting charge is ${max + add} (${max} + ${add})`);
                    }
                    return max;
                })
            )
        )
        .then(Commands.literal('attack_damage')
            .then(Commands.argument('damage', Arguments.INTEGER.create(event))
                .executes(ctx => {
                    const player = ctx.source.player;
                    const damage = Arguments.INTEGER.getResult(ctx, 'damage');

                    palladium.setProperty(player, 'phantom_sy:titan_attack_damage', damage);
                    player.tell(`Set ${player.name.getString()}'s titan attack damage to ${damage}`);
                    return 1;
                })
            )
        )
        .then(Commands.literal('eyes')
            .then(Commands.argument('eye_color', Arguments.STRING.create(event))
                .executes(ctx => {
                    const player = ctx.source.player;
                    const color = Arguments.STRING.getResult(ctx, 'eye_color');
                    const old_color = palladium.getProperty(player, 'phantom_sy:titan_eye_color');

                    if (color != old_color) {
                        palladium.setProperty(player, 'phantom_sy:titan_eye_color', color);
                        player.tell(`Set ${player.name.getString()}'s titan eye color to ${color} (was ${old_color})`);
                        return 1;
                    } else {
                        player.tell(`${player.name.getString()}'s titan eye color was already ${old_color}`);
                        return 0;
                    }
                })
            )
            .then(Commands.literal('get')
                .executes(ctx => {
                    const player = ctx.source.player;
                    const color = palladium.getProperty(player, 'phantom_sy:titan_eye_color');

                    player.tell(`${player.name.getString()}'s titan eye color is ${color}`);
                    return 1;
                })
            )
        )
        .then(Commands.literal('hair')
            .then(Commands.argument('hair_color', Arguments.STRING.create(event))
                .executes(ctx => {
                    const player = ctx.source.player;
                    const color = Arguments.STRING.getResult(ctx, 'hair_color');
                    const old_color = palladium.getProperty(player, 'phantom_sy:titan_hair_color');

                    if (color != old_color) {
                        palladium.setProperty(player, 'phantom_sy:titan_hair_color', color);
                        player.tell(`Set ${player.name.getString()}'s titan hair color to ${color} (was ${old_color})`);
                        return 1;
                    } else {
                        player.tell(`${player.name.getString()}'s titan hair color was already ${old_color}`);
                        return 0;
                    }
                })
            )
            .then(Commands.literal('get')
                .executes(ctx => {
                    const player = ctx.source.player;
                    const color = palladium.getProperty(player, 'phantom_sy:titan_hair_color');

                    player.tell(`${player.name.getString()}'s titan hair color is ${color}`);
                    return 1;
                })
            )
        )
        .then(Commands.literal('max_hardening')
            .then(Commands.argument('max', Arguments.INTEGER.create(event))
                .executes(ctx => {
                    const player = ctx.source.player;
                    const max = Arguments.INTEGER.getResult(ctx, 'max');
                    const old_max = palladium.getProperty(player, 'phantom_sy:max_hardening');

                    if (max != old_max) {
                        palladium.setProperty(player, 'phantom_sy:max_hardening', max);
                        player.tell(`Set ${player.name.getString()}'s max titan hardening to ${max} (was ${old_max})`);
                        return 1;
                    } else {
                        player.tell(`${player.name.getString()}'s max titan hardening was already ${old_max}`);
                        return 0;
                    }
                })
            )
            .then(Commands.literal('get')
                .executes(ctx => {
                    const player = ctx.source.player;
                    const max = palladium.getProperty(player, 'phantom_sy:max_hardening');
                    const add = global.yna.getAdditionalMaxHardening(player);

                    if (add == 0) {
                        player.tell(`${player.name.getString()}'s max titan hardening is ${max}`);
                    } else {
                        player.tell(`${player.name.getString()}'s max titan hardening is ${max + add} (${max} + ${add})`);
                    }
                    return max;
                })
            )
        )
        .then(Commands.literal('recharge')
            .executes(ctx => {
                const player = ctx.source.player;
                const marks = palladium.getProperty(player, 'phantom_sy:marks');

                if (marks == 0) {
                    player.tell(`${player.name.getString()}'s titan shifting is already fully charged`);
                    return 0;
                } else {
                    player.tell(`Recharged ${player.name.getString()}'s titan shifting`);
                    palladium.setProperty(player, 'phantom_sy:marks', 0);
                    return 1;
                }
            })
        )
        .then(Commands.literal('explode')
            .executes(ctx => {
                const player = ctx.source.player;

                player.sendData('phantom_sy:vfx_explosion', {type: 'smallExplosion', x: player.x, y: player.y, z: player.z});

                player.tell('boom');
                return 1;
            })
        )
    )
});