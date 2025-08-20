StartupEvents.registry('block', event => {
    event.create('phantom_sy:odm_workbench')
        .soundType('wood')
        .blockEntity(blockEntityInfo => {
            blockEntityInfo.inventory(9, 1);
            blockEntityInfo.enableSync();
        })
})