package net.phantompig.soy;

import net.minecraftforge.common.ForgeConfigSpec;

public class SoyConfig {
    public static class Server {
        public static ForgeConfigSpec.BooleanValue CAN_INJECT_OTHERS;
        public static ForgeConfigSpec.BooleanValue CAN_DISPENSE_ICEBURST;

        public static ForgeConfigSpec.BooleanValue EXPLODE_ON_SHIFT, EXPLODE_ON_FALL, EXPLODE_ON_ATTACK;

        public static ForgeConfigSpec generateConfig() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            builder.comment("Enables/disables injecting entities other than the player holding the injection.");
            CAN_INJECT_OTHERS = builder.define("general.canInjectOthers", true);
            builder.comment("Enables/disables dispensing raw iceburst from a dispenser.");
            CAN_DISPENSE_ICEBURST = builder.define("general.canDispenseIceburst", true);
            builder.comment("Enables/disables titans exploding blocks upon shifting");
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
}
