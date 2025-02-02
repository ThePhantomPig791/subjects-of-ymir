BlockEvents.rightClicked('minecraft:water_cauldron', event => {
    const { item, block, player } = event;
    const id = item.id;
    if (!block.down.hasTag('phantom_sy:heat_source')) return;
        
    if (player.offHandItem.id != 'phantom_sy:empty_injection' && player.offHandItem.id != 'phantom_sy:titan_injection') return;
    if (id != 'phantom_sy:decayed_titan_spine' && id != 'phantom_sy:titan_spine') return;
    if (player.isCrouching()) return;

    let strands = [];
    let weight = 0;
    let titan = null;
    let eye_color = null;

    if (id == 'phantom_sy:decayed_titan_spine') {
        // player.tell('decayed');
        strands = getRandomStrandList(5);
        weight = getWeight(strands);
        eye_color = getRandomEyeColor();
    } else {
        // not decayed titan spine
        // player.tell('not decayed');
        titan = item.nbt.titan;
        strands = strands.concat(listFromNbt(item.nbt.strands));
        strands = strands.concat(global.titans.getTitanFromId(item.nbt.titan).strands);
        weight -= 10;
        eye_color = item.nbt.eye_color;
    }
    
    if (player.offHandItem.id == 'phantom_sy:titan_injection') {
        // console.log(player.offHandItem.nbt.strands)
        strands = strands.concat(listFromNbt(player.offHandItem.nbt.strands));
        weight += player.offHandItem.nbt.weight;
        weight *= 1.2;
        if (titan == null) titan = player.offHandItem.nbt.titan;

        if (eye_color != null && player.offHandItem.nbt.eye_color != null) {
            eye_color = combineRGB(eye_color, player.offHandItem.nbt.eye_color);
        }
    }

    // weight -= removeRepeats(strands) * 15; // remove 15 weight for each repeated strand
    weight = Math.max(0, weight);
    // console.log(titan + ': ' + strands + ', ' + weight)
    player.offHandItem = Item.of('phantom_sy:titan_injection', {
        strands: strands,
        weight: weight,
        titan: titan,
        eye_color: eye_color
    });

    player.mainHandItem.count -= 1;

    let level = block.getProperties().level - 1;
    if (level > 0) block.set('minecraft:water_cauldron', {level: '' + level});
    else block.set('minecraft:cauldron');

    player.level.sendParticles('minecraft:bubble_pop', block.x + 0.5, block.y + 0.5, block.z + 0.5, /*count*/ 16, 0.125, 0.5, 0.125, /*speed*/ 0.01);
    global.playSoundFromPos(player.level, block.x + 0.5, block.y + 0.5, block.z + 0.5, 8, 'minecraft:block.brewing_stand.brew', 'BLOCKS', 0.8, 1.2 + Math.random() * 0.2);
    global.playSoundFromPos(player.level, block.x + 0.5, block.y + 0.5, block.z + 0.5, 8, 'minecraft:block.amethyst_block.break', 'BLOCKS', 0.2, 1.6 + Math.random() * 0.1);
})

function getRandomStrandList(maxCount) {
    let count = Math.random() * maxCount;
    let groups = global.yna.getStrandGroupList();
    let strands = [].concat(global.yna.strands);
    // console.log('groups: ' + groups);
    // console.log('strands: ' + strands);
    // console.log('global: ' + global.yna.strands)
    let list = []
    for (let i = 0, c = 0; c < 500; i++, c++) {
        if (list.length >= count) break;
        if (i == groups.length) i = 0;

        if (Math.random() < 0.2) {
            let s = getFirstStrandWithGroup(strands, groups[i]);
            if (s != null) list.push(s.id);
            strands.splice(strands.indexOf(s), 1);
        }
    }
    // console.log('random strands list: ' + list)
    return list;
}

function getFirstStrandWithGroup(strandsList, group) {
    for (let i = 0; i < strandsList.length; i++) {
        let s = strandsList[i];
        if (s.group == group) return s;
    }
    return null;
}

function getWeight(strandsList) {
    let weight = 0;
    for (let i = 0; i < strandsList.length; i++) {
        let strand = global.yna.getStrandFromId(strandsList[i]);
        if (strand != null) weight += strand.weight;
    }
    return weight;
}

// be warned this is a list of NbtElements so it's sort of useless but idk
function listFromNbt(nbtList) {
    if (nbtList == null || nbtList == undefined) return null;
    let list = [];
    for (let i = 0; i < nbtList.size(); i++) {
        list.push(nbtList.get(i));
    }
    return list;
}

function removeRepeats(list) {
    let count = 0;
    for (let i = 0; i < list.length; i++) {
        let current = list[i];
        for (let j = i + 1; j < list.length; j++) {
            if (current.equals(list[j])) {
                list.pop(j--);
                count++;
            }
        }
    }
    console.log('repeats: ' + count)
    return count;
}

// https://gist.github.com/vahidk/05184faf3d92a0aa1b46aeaa93b07786
function hslToRgb(h, s, l) {
    let c = (1 - Math.abs(2 * l - 1)) * s;
    let hp = h / 60.0; // h needs to be in degrees
    let x = c * (1 - Math.abs((hp % 2) - 1));
    let rgb1;
    if (isNaN(h)) rgb1 = [0, 0, 0];
    else if (hp <= 1) rgb1 = [c, x, 0];
    else if (hp <= 2) rgb1 = [x, c, 0];
    else if (hp <= 3) rgb1 = [0, c, x];
    else if (hp <= 4) rgb1 = [0, x, c];
    else if (hp <= 5) rgb1 = [x, 0, c];
    else if (hp <= 6) rgb1 = [c, 0, x];
    let m = l - c * 0.5;
    return [
        Math.round(255 * (rgb1[0] + m)),
        Math.round(255 * (rgb1[1] + m)),
        Math.round(255 * (rgb1[2] + m))
    ];
}

// https://martin.ankerl.com/2009/12/09/how-to-create-random-colors-programmatically/
const golden_ratio_conjugate = 0.618033988749895;
function getRandomEyeColor() {
    let h = Math.random();
    h += golden_ratio_conjugate;
    h %= 1;
    h *= 360;
    let rgbArray = hslToRgb(h, 0.5, 0.8);
    return `${rgbArray.map(v => v.toString(16).padStart(2, '0')).join('')}`;
}

// this one is from microsoft copilot
function combineRGB(color1, color2) {
    // Extract RGB components from the first color
    let r1 = parseInt(color1.slice(1, 3), 16);
    let g1 = parseInt(color1.slice(3, 5), 16);
    let b1 = parseInt(color1.slice(5, 7), 16);

    // Extract RGB components from the second color
    let r2 = parseInt(color2.slice(1, 3), 16);
    let g2 = parseInt(color2.slice(3, 5), 16);
    let b2 = parseInt(color2.slice(5, 7), 16);

    // Calculate the average of each component
    let r = Math.round((r1 + r2) / 2);
    let g = Math.round((g1 + g2) / 2);
    let b = Math.round((b1 + b2) / 2);

    // Convert the result back to a hex string
    let combinedColor = `${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`;

    return combinedColor;
}  