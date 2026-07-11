package net.phantompig.soy;

import net.minecraftforge.common.ForgeConfigSpec;

public class SoyConfig {
    public static class Server {
        public static ForgeConfigSpec.BooleanValue CAN_INJECT_OTHERS;
        public static ForgeConfigSpec.BooleanValue CAN_DISPENSE_ICEBURST;
        public static ForgeConfigSpec.BooleanValue EXPLODE_ON_SHIFT, EXPLODE_ON_FALL, EXPLODE_ON_ATTACK;

        public static ForgeConfigSpec generateConfig() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.comment("Enables/disables injecting entities other than the player holding the injection");
            CAN_INJECT_OTHERS = builder.define("general.canInjectOthers", true);

            builder.comment("Enables/disables dispensing raw iceburst from a dispenser.");
            CAN_DISPENSE_ICEBURST = builder.define("general.canDispenseIceburst", true);

            builder.comment("Enables/disables titans exploding blocks upon shifting. Be warned, disabling this may lead to shifters suffocating in the ceiling immediately after shifting");
            EXPLODE_ON_SHIFT = builder.define("titan.explodeBlocksOnShift", true);

            builder.comment("Enables/disables titans exploding blocks upon falling from a height");
            EXPLODE_ON_FALL = builder.define("titan.explodeBlocksOnFall", true);

            builder.comment("Enables/disables titans exploding blocks upon attacking");
            EXPLODE_ON_ATTACK = builder.define("titan.explodeBlocksOnAttack", true);

            return builder.build();
        }

        public static boolean canInjectOthers() {
            return CAN_INJECT_OTHERS.get();
        }

        public static boolean canDispenseIceburst() {
            return CAN_DISPENSE_ICEBURST.get();
        }

        public static boolean shouldTitansExplodeBlocksOnShift() {
            return EXPLODE_ON_SHIFT.get();
        }
        public static boolean shouldTitansExplodeBlocksOnFall() {
            return EXPLODE_ON_FALL.get();
        }
        public static boolean shouldTitansExplodeBlocksOnAttack() {
            return EXPLODE_ON_ATTACK.get();
        }
    }

    public static class Client {
        public static ForgeConfigSpec.IntValue RIGHT_HOOK_KEYCODE, LEFT_HOOK_KEYCODE;
        public static ForgeConfigSpec.DoubleValue ODM_SCREEN_TILT_STRENGTH;

        public static ForgeConfigSpec generateConfig() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.comment("The keycode for shooting the right ODM hook. If the keycode conflicts with a keybind (from the Controls menu), that keybind will be cancelled");
            RIGHT_HOOK_KEYCODE = builder.defineInRange("client.keycodes.rightHookKeycode", 69, 0, 127);

            builder.comment("The keycode for shooting the left ODM hook. If the keycode conflicts with a keybind (from the Controls menu), that keybind will be cancelled");
            LEFT_HOOK_KEYCODE = builder.defineInRange("client.keycodes.leftHookKeycode", 81, 0, 127);

            builder.comment("Determines the screen tilt intensity when using ODM gear");
            ODM_SCREEN_TILT_STRENGTH = builder.defineInRange("client.screenTiltStrength", 1d, 0, 1);

            return builder.build();
        }

        public static int getRightHookKeycode() {
            return RIGHT_HOOK_KEYCODE.get();
        }
        public static int getLeftHookKeycode() {
            return LEFT_HOOK_KEYCODE.get();
        }

        public static double getOdmScreenTiltStrength() {
            return ODM_SCREEN_TILT_STRENGTH.get();
        }
    }
}
