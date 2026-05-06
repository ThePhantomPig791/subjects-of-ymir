package net.phantompig.soy;

import net.minecraftforge.common.ForgeConfigSpec;

public class SoyConfig {
    public static class Server {
        public static ForgeConfigSpec.BooleanValue CAN_INJECT_OTHERS;
        public static ForgeConfigSpec.BooleanValue CAN_DISPENSE_ICEBURST;

        public static ForgeConfigSpec generateConfig() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            builder.comment("Enables/disables injecting entities other than the player holding the injection.");
            CAN_INJECT_OTHERS = builder.define("general.canInjectOthers", true);
            builder.comment("Enables/disables dispensing raw iceburst from a dispenser.");
            CAN_DISPENSE_ICEBURST = builder.define("general.canDispenseIceburst", true);
            return builder.build();
        }

        public static boolean canInjectOthers() {
            return CAN_INJECT_OTHERS.get();
        }

        public static boolean canDispenseIceburst() {
            return CAN_DISPENSE_ICEBURST.get();
        }
    }
}
