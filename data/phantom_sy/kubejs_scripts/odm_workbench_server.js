let PlayerInventoryWidget = Java.loadClass('com.lowdragmc.lowdraglib.gui.widget.custom.PlayerInventoryWidget')

BlockEvents.rightClicked('phantom_sy:odm_workbench', event => {
    BlockUIFactory.INSTANCE.openUI(event.player, event.block.pos, 'phantom_sy:odm_workbench');
})

function createUI(blockUIEvent) {
    let root = new WidgetGroup();
    root.setSize(100, 100);
    root.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

    // create a label and a button
    let label = new LabelWidget();
    label.setSelfPosition(20, 20);
    label.setText("Hello, World!");
    let button = new ButtonWidget();
    button.setSelfPosition(20, 40);
    button.setSize(60, 20);

    // prepare button textures
    let backgroundImage = ResourceBorderTexture.BUTTON_COMMON;
    let hoverImage = backgroundImage.copy().setColor(ColorPattern.CYAN.color);
    let textAbove = new TextTexture("Click me!");
    button.setButtonTexture(backgroundImage, textAbove);
    button.setClickedTexture(hoverImage, textAbove);

    let slot = new SlotWidget();
    slot.setSelfPosition(50 - slot.getSizeWidth() / 2, 70);
    slot.setContainerSlot(blockUIEvent.block.entity.inventory, 1);
    slot.setCanTakeItems(true);
    slot.setCanPutItems(true);

    let playerInventory = new PlayerInventoryWidget();
    playerInventory.setSelfPosition(root.getPositionX() - playerInventory.getSizeWidth() / 4 - 2, 100)
    playerInventory.setPlayer(blockUIEvent.player);
    playerInventory.setActive(true);
    playerInventory.setVisible(true);

    // add widgets
    root.addWidgets(label, button, slot, playerInventory);
    return root;
}

LDLibUI.block('phantom_sy:odm_workbench', e => {
    var ui = createUI(e);
    e.success(ui);
})