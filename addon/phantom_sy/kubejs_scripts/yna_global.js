global.yna = {};

global.yna.strands = [
    {
        id: 'charge_max_increase_1',
        weight: 10,
        group: 'charge_max_increase'
    },
    {
        id: 'charge_max_increase_3',
        weight: 15,
        group: 'charge_max_increase'
    },
    {
        id: 'charge_max_increase_5',
        weight: 30,
        group: 'charge_max_increase'
    },

    {
        id: 'increase_recharge_1',
        weight: 10,
        group: 'increase_recharge'
    },
    {
        id: 'increase_recharge_2',
        weight: 15,
        group: 'increase_recharge'
    },

    {
        id: 'speed_on_charge_1',
        weight: 15,
        group: 'speed_on_charge'
    },
    {
        id: 'speed_on_charge_2',
        weight: 20,
        group: 'speed_on_charge'
    },

    {
        id: 'steam_healing_disable',
        weight: 5,
        group: 'steam_healing_disable'
    },
    {
        id: 'steam_healing_boost',
        weight: 15,
        group: 'steam_healing_boost'
    },

    {
        id: 'steam_unshift',
        weight: 20,
        group: 'steam_unshift'
    },

    {
        id: 'hardening',
        weight: 30,
        group: 'hardening'
    },
    {
        id: 'increase_max_hardening_1',
        weight: 10,
        group: 'hardening'
    },
    {
        id: 'increase_max_hardening_2',
        weight: 15,
        group: 'hardening'
    },

    {
        id: 'multiply_shift_explosion_1.5',
        weight: 20,
        group: 'multiply_shift_explosion_1.5'
    },
    {
        id: 'multiply_shift_explosion_2',
        weight: 25,
        group: 'multiply_shift_explosion_2'
    },

    {
        id: 'armor_arms',
        weight: 25,
        group: 'armor_arms'
    },
    {
        id: 'armor_legs',
        weight: 15,
        group: 'armor_legs'
    },
    {
        id: 'armor_chest',
        weight: 20,
        group: 'armor_chest'
    },
    {
        id: 'armor_head',
        weight: 15,
        group: 'armor_head'
    }
]
global.yna.getStrandIdList = function() {
    let list = [];
    for (let i = 0; i < global.yna.strands.length; i++) {
        let s = global.yna.strands[i];
        list.push(s.id);
    }
    return list;
}
global.yna.getStrandGroupList = function() {
    let list = [];
    for (let i = 0; i < global.yna.strands.length; i++) {
        let s = global.yna.strands[i];
        if (!list.includes(s.group)) list.push(s.group);
    }
    return list;
}

global.yna.isValidStrand = function(strand) {
    for (var i = 0; i < global.yna.strands.length; i++) {
        if (global.yna.strands[i].id.equals(strand)) return true;
    }
    return false;
}

global.yna.getStrandFromId = function(strandId) {
    for (var i = 0; i < global.yna.strands.length; i++) {
        if (global.yna.strands[i].id.equals(strandId)) return global.yna.strands[i];
    }
    return null;
}

global.yna.hasStrand = function(player, strand) {
    if (player.persistentData.yna_strands == null) {
        player.persistentData.yna_strands = [];
        return false;
    }

    for (var i = 0; i < player.persistentData.yna_strands.length; i++) {
        if (player.persistentData.yna_strands[i] == strand) {
            return true;
        }
    }
    return false;
}


global.yna.onShift = function(player, charge) {
    if (global.yna.hasStrand(player, 'speed_on_charge_1')) {
        player.potionEffects.add('minecraft:speed', charge + 20, 1, true, false);
    }
    if (global.yna.hasStrand(player, 'speed_on_charge_2')) {
        player.potionEffects.add('minecraft:speed', charge + 20, 2, true, false);
    }


    if (global.yna.hasAnyArmorStrand(player)) {
        // armor durability is distributed round-robin
        var armorCharge = (charge * 4) + global.yna.getAdditionalArmor(player);

        var armorDurabilities = [null, null, null, null]; // arms, legs, chest, head
        if (global.yna.hasStrand(player, 'armor_arms')) {
            armorDurabilities[0] = 0;
        }
        if (global.yna.hasStrand(player, 'armor_legs')) {
            armorDurabilities[1] = 0;
        }
        if (global.yna.hasStrand(player, 'armor_chest')) {
            armorDurabilities[2] = 0;
        }
        if (global.yna.hasStrand(player, 'armor_head')) {
            armorDurabilities[3] = 0;
        }

        var i = 0;
        while (armorCharge > 0) {
            if (armorDurabilities[i] != null) {
                armorDurabilities[i] += 1;
                armorCharge--;
            }
            if (++i == 4) i = 0;
        }

        palladium.setProperty(player, 'phantom_sy:armor.arms', armorDurabilities[0]);
        palladium.setProperty(player, 'phantom_sy:armor.legs', armorDurabilities[1]);
        palladium.setProperty(player, 'phantom_sy:armor.chest', armorDurabilities[2]);
        palladium.setProperty(player, 'phantom_sy:armor.head', armorDurabilities[3]);
    }
    palladium.setProperty(player, 'phantom_sy:armor_invuln', 20);
    player.tell('e a v');
}
global.yna.onUnshift = function(player) {
    palladium.setProperty(player, 'phantom_sy:hardening', 0);

    palladium.setProperty(player, 'phantom_sy:armor.arms', 0);
    palladium.setProperty(player, 'phantom_sy:armor.legs', 0);
    palladium.setProperty(player, 'phantom_sy:armor.chest', 0);
    palladium.setProperty(player, 'phantom_sy:armor.head', 0);
}


global.yna.hasAnyArmorStrand = function(player) {
    return global.yna.hasStrand(player, 'armor_arms') ||
        global.yna.hasStrand(player, 'armor_legs') ||
        global.yna.hasStrand(player, 'armor_chest') ||
        global.yna.hasStrand(player, 'armor_head');
}


global.yna.getAdditionalMaxCharge = function(player) {
    var inc = 0;
    abilityUtil['getEntries(net.minecraft.class_1309,net.minecraft.class_2960)'](player, 'phantom_sy:increase_max_charge').forEach(entry => {
        if (entry.isEnabled()) inc += entry.getPropertyByName('amount');
    });
    return inc;
}

global.yna.getAdditionalMaxHardening = function(player) {
    var inc = 0;
    abilityUtil['getEntries(net.minecraft.class_1309,net.minecraft.class_2960)'](player, 'phantom_sy:increase_max_hardening').forEach(entry => {
        if (entry.isEnabled()) inc += entry.getPropertyByName('amount');
    });
    return inc;
}

global.yna.getShiftExplosionMultiplier = function(player) {
    var mult = 0;
    abilityUtil['getEntries(net.minecraft.class_1309,net.minecraft.class_2960)'](player, 'phantom_sy:multiply_shift_explosion').forEach(entry => {
        if (entry.isEnabled()) mult += entry.getPropertyByName('amount');
    });
    return mult;
}

global.yna.getAdditionalArmor = function(player) {
    var inc = 0;
    abilityUtil['getEntries(net.minecraft.class_1309,net.minecraft.class_2960)'](player, 'phantom_sy:increase_armor').forEach(entry => {
        if (entry.isEnabled()) inc += entry.getPropertyByName('amount');
    });
    return inc;
}