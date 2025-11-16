const $InventoryScreen = Java.loadClass("net.minecraft.client.gui.screens.inventory.InventoryScreen")

ClientEvents.tick(event => {
    if (Client.currentScreen instanceof $InventoryScreen && palladium.getProperty(event.player, 'phantom_sy:progress') > 0) {
        Client.currentScreen = null;
    } 
})