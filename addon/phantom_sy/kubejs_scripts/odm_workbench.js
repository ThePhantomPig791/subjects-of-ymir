StartupEvents.registry('block', event => {
    event.create('phantom_sy:odm_workbench')
        .soundType('minecraft:wood')
        .blockEntity(blockEntityInfo => {
            blockEntityInfo.inventory()
        })
})