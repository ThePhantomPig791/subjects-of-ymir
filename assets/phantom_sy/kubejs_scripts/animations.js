const Mth = Java.loadClass('net.minecraft.util.Mth');

PalladiumEvents.registerAnimations((event) => {
    event.register('phantom_sy/titan_y_displacement', 10, (builder) => {
        const pro = palladium.getProperty(builder.getPlayer(), 'phantom_sy:shift_progress')
        var progress = Mth.lerp(builder.getPartialTicks(), pro - 1, pro) / global.titans.titan_shift_max_progress;
        if (progress > 0.9) progress = 1;

        if (progress > 0.0) {
            if (!builder.isFirstPerson()) {
                builder.get('body')
                    .setY(palladium.getProperty(builder.getPlayer(), 'phantom_sy:titan_y_displacement'))
                    .animate('OutSine', progress);
            }
        }
    });

    event.register('phantom_sy/bite', 10, (builder) => {
        const player = builder.getPlayer();
        const armType = builder.getPlayer().getMainArm().toString().toLowerCase();

        const progress = animationUtil.getAnimationTimerAbilityValue(builder.getPlayer(), 'phantom_sy:titan_shift', 'bite_anim', builder.getPartialTicks());

        if (progress > 0) {
            if (armType === 'left') {
                builder.get('right_arm')
                    .setXRotDegrees(-94.3289)
                    .setYRotDegrees(-58.0287)
                    .setZRotDegrees(-2.104)
                    .animate('InOutBack', progress);
                
                if (builder.isFirstPerson()) {
                    builder.get('right_arm')
                        .setZ(25)
                        .scaleX(4)
                        .scaleY(4)
                        .scaleZ(4)
                        .animate('InOutBack', progress);

                    builder.get('left_arm')
                        .setZRotDegrees(180)
                        .animate('InOutBack', progress);
                }
            } else {
                builder.get('left_arm')
                    .setXRotDegrees(-76.8811)
                    .setYRotDegrees(54.0353)
                    .setZRotDegrees(23.7164)
                    .animate('InOutBack', progress);
                
                if (builder.isFirstPerson()) {
                    builder.get('left_arm')
                        .setZ(25)
                        .scaleX(4)
                        .scaleY(4)
                        .scaleZ(4)
                        .animate('InOutBack', progress);

                    builder.get('right_arm')
                        .setZRotDegrees(-180)
                        .animate('InOutBack', progress);
                }
            }
        }
    });


    event.register('phantom_sy/odm_sheath_grab', 10, (builder) => {
        const player = builder.getPlayer();
        const progress = animationUtil.getAnimationTimerAbilityValue(builder.getPlayer(), 'phantom_sy:odm', 'grab_sheath_animation', builder.getPartialTicks());

        if (progress > 0) {
            if (builder.isFirstPerson()) {
                builder.get('right_arm')
                    .setX(-2)
                    .setY(5)
                    .setZ(0)
                    .setYRotDegrees(-5)
                    .setZRotDegrees(-135)
                    .animate('InBack', progress);
                    
                builder.get('left_arm')
                    .setX(2)
                    .setY(5)
                    .setZ(0)
                    .setYRotDegrees(5)
                    .setZRotDegrees(135)
                    .animate('InBack', progress);
            } else {
                if (!player.isCrouching()) {
                    builder.get('head')
                        .setX(0)
                        .setY(1)
                        .setZ(-6)
                        .setXRotDegrees(22.5)
                        .animate('InBack', progress);
    
                    builder.get('chest')
                        .setX(0)
                        .setY(1)
                        .setZ(-6)
                        .setXRotDegrees(27.5)
                        .animate('InBack', progress);
    
                    builder.get('right_arm')
                        .setX(-2)
                        .setY(5)
                        .setZ(-6)
                        .setYRotDegrees(-25)
                        .setZRotDegrees(-35)
                        .animate('InBack', progress);
                    
                    builder.get('left_arm')
                        .setX(2)
                        .setY(5)
                        .setZ(-6)
                        .setYRotDegrees(25)
                        .setZRotDegrees(35)
                        .animate('InBack', progress);
                } else {
                    builder.get('head')
                        .setX(0)
                        .setY(1)
                        .setXRotDegrees(22.5)
                        .animate('InBack', progress);
    
                    builder.get('chest')
                        .setX(0)
                        .setY(1)
                        .setXRotDegrees(27.5)
                        .animate('InBack', progress);
    
                    builder.get('right_arm')
                        .setX(0)
                        .setY(5)
                        .setZ(-4)
                        .setYRotDegrees(-25)
                        .setZRotDegrees(-35)
                        .animate('InBack', progress);
                    
                    builder.get('left_arm')
                        .setX(0)
                        .setY(5)
                        .setZ(-4)
                        .setYRotDegrees(25)
                        .setZRotDegrees(35)
                        .animate('InBack', progress);
                }
            }
        }
    })

    event.register('phantom_sy/rotate_arms', 9, (builder) => {
        if (palladium.abilities.isEnabled(builder.getPlayer(), 'phantom_sy:odm', 'handles')) {
            if (builder.isFirstPerson()) {
                builder.get('right_arm')
                    .setXRotDegrees(-10)
                    .setYRotDegrees(45)
                    .setZRotDegrees(-15)
                    
                builder.get('left_arm')
                    .setXRotDegrees(-10)
                    .setYRotDegrees(-45)
                    .setZRotDegrees(15)
            }
        }
    })
});




ClientEvents.tick(event => {
    if (Client.player.isCrouching()) {
        // global.sy_vfx.explosions.smallExplosion(Client.level, Client.player.x, Client.player.y + 1, Client.player.z)
    }
})