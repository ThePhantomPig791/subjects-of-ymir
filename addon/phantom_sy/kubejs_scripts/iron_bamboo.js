let $BlockStateProperties = Java.loadClass('net.minecraft.world.level.block.state.properties.BlockStateProperties');
let $Properties = Java.loadClass('net.minecraft.world.level.block.state.BlockBehaviour$Properties');
let $Blocks = Java.loadClass('net.minecraft.world.level.block.Blocks');
let $BambooStalkBlock = Java.loadClass('net.minecraft.world.level.block.BambooStalkBlock');
let $BambooLeaves = Java.loadClass('net.minecraft.world.level.block.state.properties.BambooLeaves');
let $ItemStack = Java.loadClass("net.minecraft.world.item.ItemStack")

StartupEvents.registry('block', event => {
    event.createCustom('phantom_sy:iron_bamboo',
        () => new $BambooStalkBlock($Properties.copy($Blocks.BAMBOO)
            .strength(1.5, 0.5)
            .sound('minecraft:metal')
        )
    )

})

BlockEvents.modification(e => {
    e.modify('minecraft:iron_ore', block => {
        block.setRandomTickCallback(callback => {
            ironTick(e, block, callback);
        });
    })
    e.modify('minecraft:deepslate_iron_ore', block => {
        block.setRandomTickCallback(callback => {
            ironTick(e, block, callback);
        });
    })
})

function ironTick(event, b, callback) {
    let block = callback.block;
    let bamboo = null;
    for (let i = 1; i < 25; i++) {
        if (block.offset(0, i, 0).id == 'minecraft:bamboo') {
            bamboo = block.offset(0, i, 0);
            break;
        }
        if (block.offset(0, i, 0).hasTag('phantom_sy:iron_bamboo_can_root_through')) continue;
        return;
    }
    if (bamboo == null) return;
    if (bamboo.properties.leaves == 'small') {
        bamboo.popItem(new $ItemStack('phantom_sy:iron_bamboo_leaf', 1 + 2 * Math.random()));
    }
    if (bamboo.properties.leaves == 'large') {
        bamboo.popItem(new $ItemStack('phantom_sy:iron_bamboo_leaf', 1 + 4 * Math.random()));
    }
    bamboo.set('phantom_sy:iron_bamboo', { age: bamboo.properties.age })
    block.level.setBlock(bamboo.pos, bamboo.blockState.cycle($BlockStateProperties.STAGE), 2);
    callback.level.sendParticles(
        'dust 0.95 0.9 1 1.5',
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
        block.id == 'minecraft:deepslate_iron_ore' ? 'minecraft:block.deepslate.break' : 'minecraft:block.stone.break',
        'BLOCKS',
        1,
        0.9
    );
    if (block.id == 'minecraft:deepslate_iron_ore') block.set('minecraft:deepslate');
    else block.set('minecraft:stone');
}