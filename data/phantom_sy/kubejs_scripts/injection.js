ItemEvents.entityInteracted(event => {
    const player = event.player;
    const item = player.mainHandItem;

    if (item == null || item.id != 'phantom_sy:titan_injection') return;
    if (!event.target.isLiving()) return;
    if (player.isCrouching()) return;

    inject(event.target, item);
    clearInjection(player, item);

    player.tell(target.type + ' injected')
})

ItemEvents.rightClicked(event => {
    const {player, item} = event;

    if (item.id != 'phantom_sy:titan_injection') return;
    if (!player.isCrouching()) return;

    inject(player, item);
    clearInjection(player, item);

    event.player.tell('injected self')
});

function inject(target, item) {
    if (!target.isPlayer()) return;
    let titan = global.titans.getTitanFromId(item.nbt.titan);
    if (titan != null) {
        console.log('1');
        palladium.setProperty(target, 'phantom_sy:titan', titan.id);
        palladium.setProperty(target, 'phantom_sy:titan_skin', titan.skins[Math.floor(titan.skins.length * Math.random())]);

        palladium.setProperty(target, 'phantom_sy:titan_width', titan.scale.width);
        palladium.setProperty(target, 'phantom_sy:titan_height', titan.scale.height);
        console.log('2');
        palladium.setProperty(target, 'phantom_sy:titan_eye_height', titan.scale.eye_height);
        console.log('3');
        palladium.setProperty(target, 'phantom_sy:titan_motion', titan.scale.motion);
        console.log('4');
        palladium.setProperty(target, 'phantom_sy:titan_third_person', titan.scale.third_person);
        console.log('5');
        palladium.setProperty(target, 'phantom_sy:titan_health', titan.scale.health);
        console.log('6');
        palladium.setProperty(target, 'phantom_sy:titan_reach', titan.scale.reach);
        console.log('7');

        palladium.setProperty(target, 'phantom_sy:titan_y_displacement', titan.y_displacement);

        palladium.setProperty(target, 'phantom_sy:titan_attack_damage', titan.attack_damage);

        palladium.setProperty(target, 'phantom_sy:titan_eye_color', item.nbt.eye_color);
        palladium.setProperty(target, 'phantom_sy:titan_hair_color', titan.hair_color);

        let strands = listFromNbt(item.nbt.strands);
        console.log(strands);
        if (strands != null) for (let i = 0; i < strands.length; i++) {
            console.log(strands[i].toString())
            target.persistentData.yna_strands.push(strands[i].toString().replace('"', ''));
        }

        palladium.superpowers.addSuperpower(target, 'phantom_sy:titan_shift');

        target.tell('1');
    }
}

function clearInjection(player, item) {
    item.count -= 1;
    let newStack = Item.of('phantom_sy:empty_injection');
    if (!player.inventory.add(newStack)) {
        player.drop(newStack, false);
    }
}