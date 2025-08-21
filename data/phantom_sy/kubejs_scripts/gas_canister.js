ItemEvents.rightClicked('phantom_sy:gas_canister', event => {
    if (event.hand == 'OFF_HAND') return;
    const { player, item } = event;
    if (player.getCooldowns().isOnCooldown(item)) return;

    const offhand = player.offHandItem;
    if (offhand.id == 'phantom_sy:raw_iceburst') {
        if (item.getOrCreateTagElement('Gas').Amount >= 999) return;
        item.nbt.Gas.Amount = (item.nbt?.Gas?.Amount ?? 0) + 250;
        if (!player.isCreative()) offhand.count--;
        global.playSoundToAll(player, 16, 'phantom_sy:gas', 'PLAYERS', 0.2, 1.6);
        player.addItemCooldown(item, 2);
        return;
    }

    let odmItem = global.odm.getOdm(player);
    if (odmItem != null) {
        if ((item.nbt?.Gas?.Amount ?? 0) == 0 || (global.odm.getTotalGas(odmItem) == global.odm.getTotalMaxGas(odmItem) && item.nbt?.Gas?.Amount != 1000)) {
            if (global.odm.getTurbineGas(odmItem) > 0) {
                transferGasToCanister(odmItem, item, global.odm.getTurbineGas(odmItem), item.nbt?.Gas?.Amount ?? 0, 1000, 'TurbineGas');
                global.playSoundToAll(player, 16, 'phantom_sy:gas', 'PLAYERS', 0.4, 1.7);
            }
            if (global.odm.getSheathRightGas(odmItem) > 0) {
                transferGasToCanister(odmItem, item, global.odm.getSheathRightGas(odmItem), item.nbt?.Gas?.Amount ?? 0, 1000, 'SheathRightGas');
                global.playSoundToAll(player, 16, 'phantom_sy:gas', 'PLAYERS', 0.4, 1.8);
            }
            if (global.odm.getSheathLeftGas(odmItem) > 0) {
                transferGasToCanister(odmItem, item, global.odm.getSheathLeftGas(odmItem), item.nbt?.Gas?.Amount ?? 0, 1000, 'SheathLeftGas');
                global.playSoundToAll(player, 16, 'phantom_sy:gas', 'PLAYERS', 0.4, 1.8);
            }
        } else {
            if (global.odm.getTurbineGas(odmItem) < global.odm.getMaxTurbineGas(odmItem)) {
                transferGas(odmItem, item, global.odm.getTurbineGas(odmItem), global.odm.getMaxTurbineGas(odmItem), 'TurbineGas');
                global.playSoundToAll(player, 16, 'phantom_sy:gas', 'PLAYERS', 0.4, 1.4);
            }
            if (global.odm.getSheathRightGas(odmItem) < global.odm.getMaxSheathRightGas(odmItem)) {
                transferGas(odmItem, item, global.odm.getSheathRightGas(odmItem), global.odm.getMaxSheathRightGas(odmItem), 'SheathRightGas');
                global.playSoundToAll(player, 16, 'phantom_sy:gas', 'PLAYERS', 0.4, 1.6);
            }
            if (global.odm.getSheathLeftGas(odmItem) < global.odm.getMaxSheathLeftGas(odmItem)) {
                transferGas(odmItem, item, global.odm.getSheathLeftGas(odmItem), global.odm.getMaxSheathLeftGas(odmItem), 'SheathLeftGas');
                global.playSoundToAll(player, 16, 'phantom_sy:gas', 'PLAYERS', 0.4, 1.6);
            }
        }

    }
})


function transferGas(odmItem, canisterItem, gas, maxGas, odmGasProperty) {
    let needed = maxGas - gas;
    let available = canisterItem.nbt?.Gas?.Amount ?? 0;
    let leftover = available - needed;
    let transfer = (leftover < 0 ? available : needed);
    odmItem.nbt.Odm[odmGasProperty] = gas + transfer;
    canisterItem.nbt.Gas.Amount -= transfer;
}
function transferGasToCanister(odmItem, canisterItem, gas, canisterGas, maxGas, odmGasProperty) {
    let needed = maxGas - canisterGas;
    let leftover = gas - needed;
    let transfer = (leftover < 0 ? gas : needed);
    odmItem.nbt.Odm[odmGasProperty] = gas - transfer;
    canisterItem.getOrCreateTagElement('Gas').Amount = (canisterItem.nbt?.Gas?.Amount ?? 0) + transfer;
}