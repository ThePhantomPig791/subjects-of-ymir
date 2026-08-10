package net.phantompig.soy;

import net.minecraftforge.common.ForgeConfigSpec;

public class SoyConfig {
    public static class Server {
        public static ForgeConfigSpec.BooleanValue CAN_INJECT_OTHERS;
        public static ForgeConfigSpec.BooleanValue CAN_DISPENSE_ICEBURST;
        public static ForgeConfigSpec.BooleanValue EXPLODE_ON_SHIFT, EXPLODE_ON_FALL, EXPLODE_ON_ATTACK;
        public static ForgeConfigSpec.BooleanValue SAVE_CURIOS_TRINKETS_INVENTORY;
        public static ForgeConfigSpec.BooleanValue SCARE_VILLAGERS, AGGRAVATE_IRON_GOLEMS;
        public static ForgeConfigSpec.IntValue CURSE_OF_YMIR_DEATHS;
        public static ForgeConfigSpec.DoubleValue BLOCK_PERCENTAGE;

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

            builder.comment("Enables/disables saving/loading curios and trinkets to/from NBT when shifting into a titan. I would not recommend enabling this; I couldn't get this to properly function (but didn't want to delete the code in case I got it to work in the future). Basically, ignore this because Titans already cannot take items from Curios or Trinkets slots.");
            SAVE_CURIOS_TRINKETS_INVENTORY = builder.define("titan.saveCuriosTrinketsInventory", false);

            builder.comment("A Titan Shifter will drop their Titan in the form of a Spine Item when they die this number of times. Set to 1 to make every Shifter always lose their Titan upon dying. Set to the maximum integer to disable the Curse of Ymir.");
            CURSE_OF_YMIR_DEATHS = builder.defineInRange("titan.curseOfYmirDeaths", 13, 1, Integer.MAX_VALUE);

            builder.comment("The percentage of damage that is blocked when a Titan blocks an attack. 0.8 means that 80% of incoming damage will be blocked.");
            BLOCK_PERCENTAGE = builder.defineInRange("titan.blockPercentage", 0.8, 0, 1);

            builder.comment("If true, Villagers will run away from Titans and Titan Corpses (requires world restart to take effect).");
            builder.worldRestart();
            SCARE_VILLAGERS = builder.define("titan.scareVillagers", true);

            builder.comment("If true, Iron Golems will attack Titans and Titan Corpses on sight (requires world restart to take effect).");
            builder.worldRestart();
            AGGRAVATE_IRON_GOLEMS = builder.define("titan.aggroGolems", true);

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

        public static boolean shouldSaveCuriosTrinketsInventory() {
            return SAVE_CURIOS_TRINKETS_INVENTORY.get();
        }

        public static int getCurseOfYmirDeaths() {
            return CURSE_OF_YMIR_DEATHS.get();
        }

        public static float getBlockPercentage() {
            return BLOCK_PERCENTAGE.get().floatValue();
        }

        public static boolean shouldVillagersRunAway() {
            return SCARE_VILLAGERS.get();
        }
        public static boolean shouldIronGolemsAttackOnTitans() { // see what i did there
            return AGGRAVATE_IRON_GOLEMS.get();
        }
    }

    public static class Client {
        public static ForgeConfigSpec.IntValue RIGHT_HOOK_KEYCODE, LEFT_HOOK_KEYCODE;
        public static ForgeConfigSpec.DoubleValue ODM_SCREEN_TILT_STRENGTH, SPARK_PARTICLE_SCALE;
        public static ForgeConfigSpec.BooleanValue WORLD_TINT, ODM_ANIMATIONS, SHIFTING_SPHERE, OVERRIDE_SPHERE_COLOR;

        public static ForgeConfigSpec generateConfig() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.comment("The keycode for shooting the right ODM hook. If the keycode conflicts with a keybind (from the Controls menu), that keybind will be cancelled");
            RIGHT_HOOK_KEYCODE = builder.defineInRange("client.keycodes.rightHookKeycode", 69, 0, 127);

            builder.comment("The keycode for shooting the left ODM hook. If the keycode conflicts with a keybind (from the Controls menu), that keybind will be cancelled");
            LEFT_HOOK_KEYCODE = builder.defineInRange("client.keycodes.leftHookKeycode", 81, 0, 127);

            builder.comment("Determines the screen tilt intensity when using ODM gear");
            ODM_SCREEN_TILT_STRENGTH = builder.defineInRange("client.screenTiltStrength", 1d, 0, 1);

            builder.comment("Determines if the world tints yellow-green when someone shifts into a Titan");
            WORLD_TINT = builder.define("client.worldTint", true);

            builder.comment("Determines if the player model is animated for players using ODM gear");
            ODM_ANIMATIONS = builder.define("client.ODMAnimations", true);

            builder.comment("Determines the scale for the particles that display when someone shifts into a Titan");
            SPARK_PARTICLE_SCALE = builder.defineInRange("client.sparkParticleScale", 1d, 0, 2);

            builder.comment("If false, the lightning sphere around someone shifting into a Titan will not appear");
            SHIFTING_SPHERE = builder.define("client.shiftSphere", true);

            builder.comment("If true, the color of the lightning sphere will be overridden with a less-intense tan/white, matching the color of the particles");
            OVERRIDE_SPHERE_COLOR = builder.define("client.overrideSphereColor", false);

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

        public static boolean shouldWorldTintOnShift() {
            return WORLD_TINT.get();
        }

        public static boolean shouldAnimateOdm() {
            return ODM_ANIMATIONS.get();
        }

        public static double getSparkParticleScale() {
            return SPARK_PARTICLE_SCALE.get();
        }

        public static boolean shouldShiftRenderSphere() {
            return SHIFTING_SPHERE.get();
        }
        public static boolean shouldOverrideSphereColor() {
            return OVERRIDE_SPHERE_COLOR.get();
        }
    }
}
