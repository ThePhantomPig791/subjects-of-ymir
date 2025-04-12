StartupEvents.registry('mob_effect', event => {
    const glowingEffect = event.create('phantom_sy:glowing')
    glowingEffect.displayName(Component.white('Glowing'))
    glowingEffect.color(0xFFFFFF)
    glowingEffect.beneficial()
});