StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:marks')
        .documentationDescription('Ticks the marks property accordingly')
        
        .tick((entity, entry, holder, enabled) => {
            if (entity.age % 10 != 0) return;
            const marks = palladium.getProperty(entity, 'phantom_sy:marks');
            if (palladium.getProperty(entity, 'phantom_sy:is_shifted')) {
                if (marks < 600) palladium.setProperty(entity, 'phantom_sy:marks', marks + 1);
            } else {
                if (marks > 0) {
                    palladium.setProperty(entity, 'phantom_sy:marks', marks - (1 + getAdditionalRecharge(entity)));
                    entity.level.sendParticles('phantom_sy:steam', entity.x, entity.y + 1.5, entity.z, 10, 0.1, 0.1, 0.1, 0.1);
                }
            }

            const armorInvuln = palladium.getProperty(entity, 'phantom_sy:armor_invuln');
            if (armorInvuln > 0) palladium.setProperty(entity, 'phantom_sy:armor_invuln', armorInvuln - 1);
        });
});

function getAdditionalRecharge(player) {
    var inc = 0;
    abilityUtil['getEntries(net.minecraft.class_1309,net.minecraft.class_2960)'](player, 'phantom_sy:increase_recharge').forEach(entry => {
        if (entry.isEnabled()) inc += entry.getPropertyByName('amount');
    });
    return inc;
}