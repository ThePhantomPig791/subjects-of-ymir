// damaging the arms when damage is dealt
EntityEvents.hurt(event => {
    if (!event.source.getImmediate() || !event.source.getImmediate().isPlayer()) return;
    const player = event.source.getImmediate();
    if (!palladium.getProperty(player, 'phantom_sy:is_shifted')) return;
    if (palladium.getProperty(player, 'phantom_sy:armor.arms') == 0) return;

    damageArmor(player, 'arms', event.damage / 2);

    player.tell(`arms: ${palladium.getProperty(player, 'phantom_sy:armor.arms')}`);

    console.log(event.damage)
    // dealing extra damage is dealt with by an attribute in the titan_shift power
});

// damaging the armor when damage is taken
EntityEvents.hurt(event => {
    // adding armor value is also an attribute in the power
    if(!event.entity.isPlayer()) return;
    const player = event.entity;
    if (!palladium.getProperty(player, 'phantom_sy:is_shifted') || palladium.getProperty(player, 'phantom_sy:armor_invuln') > 0) return;
    
    // round-robin once more
    var dur = event.damage * 3;
    var durAmounts = [null, null, null, null] // arms, legs, chest, head
    if (palladium.getProperty(player, 'phantom_sy:armor.arms') != 0) {
        durAmounts[0] = 0;
    }
    if (palladium.getProperty(player, 'phantom_sy:armor.legs') != 0) {
        durAmounts[1] = 0;
    }
    if (palladium.getProperty(player, 'phantom_sy:armor.chest') != 0) {
        durAmounts[2] = 0;
    }
    if (palladium.getProperty(player, 'phantom_sy:armor.head') != 0) {
        durAmounts[3] = 0;
    }

    if (durAmounts[0] == null && durAmounts[1] == null && durAmounts[2] == null && durAmounts[3] == null) return;

    var i = 0;
    while (dur > 0) {
        if (durAmounts[i] != null) {
            durAmounts[i] += 1;
            dur--;
        }
        if (++i == 4) i = 0;
    }

    damageArmor(player, 'arms', durAmounts[0]);
    damageArmor(player, 'legs', durAmounts[1]);
    damageArmor(player, 'chest', durAmounts[2]);
    damageArmor(player, 'head', durAmounts[3]);

    player.tell(`arms: ${palladium.getProperty(player, 'phantom_sy:armor.arms')}, legs: ${palladium.getProperty(player, 'phantom_sy:armor.legs')}, chest: ${palladium.getProperty(player, 'phantom_sy:armor.chest')}, head: ${palladium.getProperty(player, 'phantom_sy:armor.head')}`)
});

function damageArmor(player, part, amount) {
    var dur = palladium.getProperty(player, 'phantom_sy:armor.' + part);

    if (dur <= 1) {
        dur = 0;
        global.playSoundToAll(player, 32, 'minecraft:entity.item.break', 'PLAYERS', 1, 1.2);
        global.playSoundToAll(player, 32, 'minecraft:entity.item.break', 'PLAYERS', 1, 1.4);
        global.playSoundToAll(player, 32, 'minecraft:entity.item.break', 'PLAYERS', 1, 1.6);
    } else {
        dur -= amount;
        if (dur <= 0) {
            dur = 1;
            global.playSoundToAll(player, 32, 'minecraft:entity.item.break', 'PLAYERS', 1, 1.6);
            global.playSoundToAll(player, 32, 'minecraft:entity.item.break', 'PLAYERS', 1, 1.8);
        }
    }

    palladium.setProperty(player, 'phantom_sy:armor.' + part, dur);
}