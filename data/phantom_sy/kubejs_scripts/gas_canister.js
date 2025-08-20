ItemEvents.rightClicked('phantom_sy:gas_canister', event => {
    if (event.hand == 'OFF_HAND') return;
    const { player, item } = event;
    const iceburst = player.offHandItem;
    if (iceburst.id != 'phantom_sy:raw_iceburst') return;
    if (item.getOrCreateTagElement('Gas').Amount >= 999) return;
    item.nbt.Gas.Amount = (item.nbt?.Gas?.Amount ?? 0) + 250;
    if (!player.isCreative()) iceburst.count--;
})