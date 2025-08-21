global.odm = {};

global.odm.turbines = {
    // accepts_sheath: true
    empty: {},
    prototype_1: {
        accepts_sheath: false
    },
    prototype_2: {

    },
    prototype_3: {

    },
    version_1: {

    },
    version_2: {

    }
}
for (let turb in global.odm.turbines) {
    if (turb.accepts_sheath == null) turb.accepts_sheath = true;
}

global.odm.sheaths = { // max_gas is per sheath, not pair of sheaths
    // max_gas: 1000
    empty: {},
    blade: {
        max_gas: 1000
    },
    gas: {
        max_gas: 2500
    }
}
for (let sheath in global.odm.sheaths) {
    if (sheath.max_gas == null) sheath.max_gas = 1000;
}

function indexOf(list, id) {
    if (id == null) return 0;
    let i = 0;
    for (let el in list) {
        if (el == id) return i;
        i++;
    }
    return 0;
}

ItemEvents.modelProperties(event => {
    event.registerAll('phantom_sy:turbine', (item, level, entity, seed) => {
        return indexOf(global.odm.turbines, item.nbt?.Odm?.Turbine) / 10;
    })
    event.registerAll('phantom_sy:sheath_left', (item, level, entity, seed) => {
        return indexOf(global.odm.sheaths, item.nbt?.Odm?.SheathLeft) / 10;
    })
    event.registerAll('phantom_sy:sheath_right', (item, level, entity, seed) => {
        return indexOf(global.odm.sheaths, item.nbt?.Odm?.SheathRight) / 10;
    })
})