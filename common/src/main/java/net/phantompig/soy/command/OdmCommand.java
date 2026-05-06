package net.phantompig.soy.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.phantompig.soy.odm.OdmLevelHelper;

public class OdmCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("clear_odm_nodes").requires((player) -> player.hasPermission(2))
                .executes(context -> {
                    var source = context.getSource();

                    OdmLevelHelper.clearNodes(source.getLevel());
                    source.sendSuccess(() -> Component.literal("Cleared all ODM nodes"), true);

                    return 1;
                })
        );
    }
}
