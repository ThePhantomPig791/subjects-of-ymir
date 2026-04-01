package net.phantompig.soy.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class TitanCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("titan").requires((player) -> player.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.player())
                        .executes(context -> {
                            var source = context.getSource();
                            var entity = EntityArgument.getPlayer(context, "entity");

                            source.sendFailure(Component.translatable("commands.titan", entity.getDisplayName()));
                            return 0;
                        })
                )
        );
    }
}
