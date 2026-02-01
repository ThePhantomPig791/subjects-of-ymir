package net.phantompig.soy.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.titan.Titan;
import net.phantompig.soy.titan.TitanInstance;
import net.phantompig.soy.titan.TitanRegistry;

import java.util.ArrayList;

public class TitanCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("titan").requires((player) -> player.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.argument("titan", ResourceLocationArgument.id()).suggests(SUGGEST_TITANS)
                                        .then(Commands.argument("variant", StringArgumentType.string()).suggests(SUGGEST_VARIANTS)
                                                .executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getEntity(context, "entity");
                                                    var id = ResourceLocationArgument.getId(context, "titan");
                                                    var variant = StringArgumentType.getString(context, "variant");

                                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                                        TitanInstance inst = new TitanInstance((LivingEntity) entity, TitanRegistry.getTitan(id), variant);
                                                        playerExt.setTitanInstance(inst);
                                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.set", entity.getDisplayName(), id), true);
                                                    } else {
                                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                        return 0;
                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .executes(context -> {
                                    var source = context.getSource();
                                    var entity = EntityArgument.getEntity(context, "entity");

                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                        playerExt.setTitanInstance(new TitanInstance((LivingEntity) entity));
                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.remove", entity.getDisplayName()), true);
                                    } else {
                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                        return 0;
                                    }

                                    return 1;
                                })
                        )
                )
        );
    }

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TITANS = (context, builder) -> SharedSuggestionProvider.suggest(TitanRegistry.getTitans().keySet().stream().map(ResourceLocation::toString), builder);
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_VARIANTS = (context, builder) -> {
        Titan titan = TitanRegistry.getTitan(ResourceLocationArgument.getId(context, "titan"));
        ArrayList<String> variants = new ArrayList<>();
        if (titan != null) variants.addAll(titan.variants);
        return SharedSuggestionProvider.suggest(variants, builder);
    };
}
