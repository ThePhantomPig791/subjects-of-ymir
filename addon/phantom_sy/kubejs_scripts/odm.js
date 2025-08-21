let $CuriosTrinketsUtil = Java.loadClass('net.threetag.palladium.compat.curiostinkets.CuriosTrinketsUtil');

global.odm = {};

global.odm.turbines = {
    // accepts_sheath: true
    // max_gas: 0
    empty: {},
    prototype_1: {
        accepts_sheath: false,
        max_gas: 500
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
    if (turb.max_gas == null) turb.max_gas = 0;
}

global.odm.sheaths = { // these properties are per sheath, not per pair of sheaths
    // max_gas: 1000
    empty: {},
    blade: {
        max_gas: 1000
    },
    gas: {
        max_gas: 2000
    }
}
for (let sheath in global.odm.sheaths) {
    if (sheath.max_gas == null) sheath.max_gas = 1000;
}

global.odm.indexOfOdmPiece = (list, id) => {
    if (id == null) return 0;
    let i = 0;
    for (let el in list) {
        if (el == id) return i;
        i++;
    }
    return 0;
}
global.odm.getOdmPiece = (list, id) => {
    if (id == null) return null;
    for (let el in list) {
        if (el == id) return list[el];
    }
    return null;
}

ItemEvents.modelProperties(event => {
    event.registerAll('phantom_sy:turbine', (item, level, entity, seed) => {
        return global.odm.indexOfOdmPiece(global.odm.turbines, item.nbt?.Odm?.Turbine) / 10;
    })
    event.registerAll('phantom_sy:sheath_left', (item, level, entity, seed) => {
        return global.odm.indexOfOdmPiece(global.odm.sheaths, item.nbt?.Odm?.SheathLeft) / 10;
    })
    event.registerAll('phantom_sy:sheath_right', (item, level, entity, seed) => {
        return global.odm.indexOfOdmPiece(global.odm.sheaths, item.nbt?.Odm?.SheathRight) / 10;
    })
})


global.odm.getTurbineGas = (odmItem) => {
    return odmItem.nbt?.Odm?.TurbineGas ?? 0;
}
global.odm.getSheathLeftGas = (odmItem) => {
    return odmItem.nbt?.Odm?.SheathLeftGas ?? 0;
}
global.odm.getSheathRightGas = (odmItem) => {
    return odmItem.nbt?.Odm?.SheathRightGas ?? 0;
}

global.odm.getMaxTurbineGas = (odmItem) => {
    return global.odm.getOdmPiece(global.odm.turbines, odmItem.nbt?.Odm?.Turbine)?.max_gas ?? 0;
}
global.odm.getMaxSheathLeftGas = (odmItem) => {
    return global.odm.getOdmPiece(global.odm.sheaths, odmItem.nbt?.Odm?.SheathLeft)?.max_gas ?? 0;
}
global.odm.getMaxSheathRightGas = (odmItem) => {
    return global.odm.getOdmPiece(global.odm.sheaths, odmItem.nbt?.Odm?.SheathRight)?.max_gas ?? 0;
}

global.odm.getTotalGas = (odmItem) => {
    return global.odm.getTurbineGas(odmItem) + global.odm.getSheathLeftGas(odmItem) + global.odm.getSheathRightGas(odmItem);
}
global.odm.getTotalMaxGas = (odmItem) => {
    return global.odm.getMaxTurbineGas(odmItem) + global.odm.getMaxSheathLeftGas(odmItem) + global.odm.getMaxSheathRightGas(odmItem);
}

global.odm.getOdm = (entity) => {
    const items = $CuriosTrinketsUtil.getInstance().getItemsInSlot(entity, Platform.isForge() ? 'belt' : 'legs/belt');
    for (let e of items) {
        if (e.id == 'phantom_sy:odm') {
            return e;
        }
    }
    return null;
}


StartupEvents.registry('palladium:abilities', event => {
    event.create('phantom_sy:odm_circuits')
        .documentationDescription('Tick functions for ODM gear')

        .tick((entity, entry, holder, enabled) => {
            if (enabled) {
                let odm = global.odm.getOdm(entity);
                palladium.setProperty(entity, 'phantom_sy:odm.gas', global.odm.getTotalGas(odm));
                palladium.setProperty(entity, 'phantom_sy:odm.max_gas', global.odm.getTotalMaxGas(odm));
            }
        })
})