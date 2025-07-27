global.titans = {};
global.titans.MAX_SHIFT_PROGRESS = 15;

global.titans.list = {
    test: {
        variants: [
            'default'
        ],
        scale: 8
    }
}

/*
    TO ADD A NEW TITAN:
    (replace <titan> with your unique titan id)

 - Append the <titan> object to global.titans.list with its necessary values

 - Add: 
        skin.geo.json
        muscle.geo.json
        skeleton.geo.json 
    to assets/phantom_sy/geo/titan/<variant>/<titan>/ (make sure that the pivots are correct; see the 8x scale template with the 8x animation).
    Additionally, add the new scale animation to assets/phantom_sy/animations/titan_scale_<scale>.animation.json
    (Unless the titan has a generic model and a scale model and animation set already exists for it)

 - Add: 
        skin.png
        muscle.png
        eyes.png
        hair.png
        marks.png
    to assets/phantom_sy/textures/models/titans/<titan>/

 - Add:
        titan_<titan>.json
    to data/phantom_sy/palladium/powers/ and place the necessary unshifting powers and such in it (look through and copy an existing titan's power).
    Add a generic titan scale render layer or create a new one for custom properties or scale

*/