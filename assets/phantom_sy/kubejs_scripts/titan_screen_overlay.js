const OVERLAY_TEXTURE = 'phantom_sy:textures/titan_vision.png';

PalladiumEvents.registerGuiOverlays((event) => {
    event.register(
        // ID for the overlay
        'phantom_sy:titan_vision',

        // Function for rendering
        (minecraft, gui, poseStack, partialTick, screenWidth, screenHeight) => {
            if (palladium.getProperty(minecraft.player, 'phantom_sy:is_shifted') || palladium.getProperty(minecraft.player, 'phantom_sy:is_shifting')) {
                // Parameters: Texture, Gui, PoseStack, x, y, U on the texture, V on the texture, width, height
                // guiUtil.blit(OVERLAY_TEXTURE, gui, poseStack, 0, 0, 0, 0, 1080, 720);
            }
        });
});