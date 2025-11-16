ItemEvents.rightClicked('phantom_sy:injection', event => {
    if (event.hand == 'OFF_HAND') return;
    const { player, item } = event;
    if (player.getMainHandItem().id != 'phantom_sy:injection') return;
    if (player.getCooldowns().isOnCooldown('phantom_sy:vial')) return;
    const offhandItem = player.getOffhandItem();

    if (offhandItem.id != 'phantom_sy:vial' || getVialLevel(offhandItem) == 0) return;

    incVialLevel(offhandItem, -1);

    player.setMainHandItem('phantom_sy:injection_filled');
    player.addItemCooldown('phantom_sy:vial', 2);

    player.getMainHandItem().addTagElement('SerumData', getSerumData(offhandItem));
    if (getVialLevel(offhandItem) == 0) offhandItem.nbt.SerumData = {};
})

ItemEvents.rightClicked('phantom_sy:injection_filled', event => {
    if (event.hand == 'OFF_HAND') return;
    const { player, item } = event;
    if (player.getMainHandItem().id != 'phantom_sy:injection_filled') return;
    let rayTrace = player.rayTrace();

    if (rayTrace.entity) {
        // inject other
        inject(rayTrace.entity, player, getSerumData(item));
    } else {
        if (player.crouching && canInject(player)) {
            // inject self
            inject(player, player, getSerumData(item));
        } else {
            // put fluid in vial
            if (player.getCooldowns().isOnCooldown('phantom_sy:vial')) return;
            const offhandItem = player.getOffhandItem();

            if (offhandItem.id != 'phantom_sy:vial' || getVialLevel(offhandItem) >= 3) return;

            incVialLevel(offhandItem, 1);
            offhandItem.nbt.SerumData = mergeSerumData(offhandItem.nbt?.SerumData, player.getMainHandItem().nbt?.SerumData);

            player.setMainHandItem('phantom_sy:injection');
            player.addItemCooldown('phantom_sy:vial', 2);
        }
    }
})


function setVialLevel(vial, level) {
    let vialData = vial.getOrCreateTagElement('VialData');
    if (vialData?.Level == null) vialData.Level = 0;
    vialData.Level = level;
    vial.nbt.CustomModelData = level;
    vial.addTagElement('VialData', vialData);
}
function incVialLevel(vial, n) {
    let vialData = vial.getOrCreateTagElement('VialData');
    if (vialData?.Level == null) vialData.Level = 0;
    vialData.Level += n;
    vial.nbt.CustomModelData = vialData.Level;
    vial.addTagElement('VialData', vialData);
}
function getVialLevel(vial) {
    let level = vial.getOrCreateTagElement('VialData')?.Level;
    return level == null ? 0 : level;
}


function getSerumData(item) {
    return (item.nbt?.SerumData == null || item.nbt.SerumData.size() == 0) ? getDefaultSerumData() : item.nbt.SerumData;
}
function getDefaultSerumData() {
    return { Titan: 'test', TitanVariant: 'default' }
}

function mergeSerumData(serum, otherSerum) {
    if (serum == null && otherSerum == null) return getDefaultSerumData();
    if (serum == null) return otherSerum;
    if (otherSerum == null) return serum;
    return Object.assign(serum, otherSerum); // this may need to change for eye colors and stuff idk
}


function inject(target, player, serumData) {
    if (!canInject(target)) return;

    if (serumData?.Titan == null) serumData = getDefaultSerumData();

    palladium.setProperty(target, 'phantom_sy:titan', serumData.Titan);
    palladium.setProperty(target, 'phantom_sy:titan_variant', serumData.TitanVariant);
    palladium.superpowers.addSuperpower(target, `phantom_sy:new_shifter`);

    player.setMainHandItem('phantom_sy:injection');
}

function canInject(target) {
    if (palladium.superpowers.hasSuperpower(target, 'phantom_sy:shifter')) return false;
    return true;
}