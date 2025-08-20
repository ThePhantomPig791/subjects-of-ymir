let $ItemStack = Java.loadClass("net.minecraft.world.item.ItemStack")

StartupEvents.registry('item', event => {
    event.create('phantom_sy:gas_canister')
        .unstackable()
        .barColor(stack => {
            let p = 255 * Math.min(1, 0.5 + getGas(stack) / 1000);
            return Color.rgba(p, p, p, 255);
        })
        .barWidth(stack => Math.ceil(13 * getGas(stack) / 1000))
})

function getGas(itemStack) {
    return itemStack.nbt?.Gas?.Amount ?? 0;
}


StartupEvents.modifyCreativeTab('phantom_sy:subjects_of_ymir', event => {
	event.addAfter('phantom_sy:blade', 'phantom_sy:gas_canister');
    let filled = new $ItemStack('phantom_sy:gas_canister');
    filled.getOrCreateTagElement('Gas').Amount = 1000;
	event.addAfter('phantom_sy:gas_canister', filled);
})