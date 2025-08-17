BlockEvents.modification(e => {
    e.modify('minecraft:raw_iron_block', block => {
        block.setRandomTickCallback(callback => {
            global.bam(e, block, callback);
        });
    })
})

global.bam = function (e, b, callback) {
    let d = callback.random.nextDouble();
    if (d > 0.4) return;
    let block = callback.block;
    for (let i = 1; i < 5; i++) {
        if (block.offset(0, i, 0).id == 'minecraft:bamboo') break;
        if (block.offset(0, i, 0).hasTag('phantom_sy:ultrahard_steel_can_grow_through')) continue;
        return;
    }
    let coals = [];
    for (let x = -2; x <= 2; x++) {
        for (let y = -2; y <= 2; y++) {
            for (let z = -2; z <= 2; z++) {
                if (coals.length >= 2) break;
                let offset = block.offset(x, y, z);
                if (offset.hasTag('phantom_sy:required_for_ultrahard_steel')) coals = coals.concat(offset);
            }
        }
    }
    if (coals.length < 2) return;
    block.set('phantom_sy:ultrahard_steel_ore');
    callback.level.sendParticles(
        'dust 0.8 0.85 1 1.5',
        block.x + 0.5,
        block.y + 0.5,
        block.z + 0.5,
        /*count*/ 10,
        0.5,
        0.5,
        0.5,
        /*speed*/ 0
    );
    global.playSoundFromPos(
        callback.level,
        block.x + 0.5,
        block.y + 0.5,
        block.z + 0.5,
        16,
        'minecraft:block.stone.break',
        'BLOCKS',
        1,
        0.9
    );

    for (let coal of coals) {
        if (coal.id == 'minecraft:deepslate_coal_ore') coal.set('minecraft:deepslate');
        else coal.set('minecraft:stone');
        callback.level.sendParticles(
            'dust 0.3 0.3 0.35 1',
            coal.x + 0.5,
            coal.y + 0.5,
            coal.z + 0.5,
        /*count*/ 10,
            0.5,
            0.5,
            0.5,
        /*speed*/ 0
        );
    }
}