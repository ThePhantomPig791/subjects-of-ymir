global.titans = {};

global.titans.titan_shift_max_progress = 15;

global.titans.list = [
    // ['attack', []], // 7.5 7.5 7.5 3 5 2 5 #14E789 #282518
    {
        id: 'attack',
        skins: [
            'eren',
            'kruger'
        ],
        strands: [],
        scale: {
            width: 7.5,
            height: 7.5,
            eye_height: 7.75,
            motion: 3,
            third_person: 5,
            health: 2,
            reach: 5
        },
        attack_damage: 10,
        eye_color: '14E789',
        hair_color: '282518',
        y_displacement: 145
    }
];

global.titans.getTitanFromId = function(titanId) {
    for (var i = 0; i < global.titans.list.length; i++) {
        if (global.titans.list[i].id.equals(titanId)) return global.titans.list[i];
    }
    return null;
}

global.titans.getIndexFromId = function(titanId) {
    for (var i = 0; i < global.titans.list.length; i++) {
        if (global.titans.list[i].id.equals(titanId)) return i;
    }
    return null;
}