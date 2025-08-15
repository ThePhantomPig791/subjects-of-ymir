let Mth = Java.loadClass('net.minecraft.util.Mth');

PalladiumEvents.registerAnimations((event) => {
    event.register('phantom_sy/titan_y_displacement', 10, (builder) => {
        const pro = palladium.getProperty(builder.getPlayer(), 'phantom_sy:progress')
        let progress = Mth.lerp(builder.getPartialTicks(), pro - 1, pro) / global.titans.MAX_SHIFT_PROGRESS;

        if (progress > 0.0) {
            if (progress > 0.9) progress = 1;
            if (!builder.isFirstPerson()) {
                let modelScale = global.titans.list[palladium.getProperty(builder.getPlayer(), 'phantom_sy:titan')].scale;
                builder.get('body')
                    .setY(modelScale * 12 + modelScale * 6.5)
                    .animate('OutSine', progress);
            }
        }
    });

    event.register('phantom_sy/bite', 10, (builder) => {
        const progress = animationUtil.getAnimationTimerAbilityValue(builder.getPlayer(), 'phantom_sy:shifter', 'bite_anim', builder.getPartialTicks());

        if (progress > 0) {
            const armType = builder.getPlayer().getMainArm().toString().toLowerCase();
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

    event.register('phantom_sy/self_stab', 10, (builder) => {
        const progress = animationUtil.getAnimationTimerAbilityValue(builder.getPlayer(), 'phantom_sy:dagger', 'self_stab_anim', builder.getPartialTicks());

        if (progress > 0) {
            const armType = builder.getPlayer().getHeldItem('MAIN_HAND').id == 'phantom_sy:dagger' ? builder.getPlayer().getMainArm().toString().toLowerCase() : builder.getPlayer().getMainArm().getOpposite().toString().toLowerCase();
            if (armType === 'left') {
                if (!builder.isFirstPerson()) {
                    builder.get('left_arm')
                        .setXRotDegrees(-85)
                        .setYRotDegrees(65)
                        .setZRotDegrees(-60)
                        .animate('InSine', progress);
                    builder.get('right_arm')
                        .setXRotDegrees(-30)
                        .animate('InSine', progress);
                }
            } else {
                if (!builder.isFirstPerson()) {
                    builder.get('right_arm')
                        .setXRotDegrees(-85)
                        .setYRotDegrees(-65)
                        .setZRotDegrees(60)
                        .animate('InSine', progress);
                    builder.get('left_arm')
                        .setXRotDegrees(-30)
                        .animate('InSine', progress);
                }
            }
        }
    });
})