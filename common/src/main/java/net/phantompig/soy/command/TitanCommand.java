package net.phantompig.soy.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.property.SoyProperties;
import net.phantompig.soy.titan.Titan;
import net.phantompig.soy.titan.TitanInstance;
import net.phantompig.soy.titan.TitanRegistry;

import java.awt.*;
import java.util.ArrayList;

public class TitanCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("titan").requires((player) -> player.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("entity", EntityArgument.player())
                                .then(Commands.argument("titan", ResourceLocationArgument.id()).suggests(SUGGEST_TITANS)
                                        .then(Commands.argument("variant", StringArgumentType.string()).suggests(SUGGEST_VARIANTS)
                                                .executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getPlayer(context, "entity");
                                                    var id = ResourceLocationArgument.getId(context, "titan");
                                                    if (!TitanRegistry.titanExists(id)) {
                                                        source.sendFailure(Component.translatable("commands.titan.error.noSuchTitan", id));
                                                        return 0;
                                                    }
                                                    var variant = StringArgumentType.getString(context, "variant");
                                                    var titan = TitanRegistry.getTitan(id);
                                                    if (!titan.variants.contains(variant)) {
                                                        source.sendFailure(Component.translatable("commands.titan.error.noSuchVariant", id, variant));
                                                        return 0;
                                                    }

                                                    return setTitanAndVariant(entity, id, variant, source);
                                                })
                                        )
                                        .executes(context -> {
                                            var source = context.getSource();
                                            var entity = EntityArgument.getPlayer(context, "entity");
                                            var id = ResourceLocationArgument.getId(context, "titan");
                                            if (!TitanRegistry.titanExists(id)) {
                                                source.sendFailure(Component.translatable("commands.titan.error.noSuchTitan", id));
                                                return 0;
                                            }
                                            var variant = TitanRegistry.getRandom(TitanRegistry.getTitan(id).variants);

                                            return setTitanAndVariant(entity, id, variant, source);
                                        })
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("entity", EntityArgument.player())
                                .executes(context -> {
                                    var source = context.getSource();
                                    var entity = EntityArgument.getPlayer(context, "entity");

                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                        if (playerExt.getTitanInstance().getProgress() > 0) {
                                            source.sendFailure(Component.translatable("commands.titan.error.shifted", entity.getDisplayName()));
                                            return 0;
                                        }
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
                .then(Commands.literal("eyes")
                        .then(Commands.literal("set")
                                .then(Commands.argument("entity", EntityArgument.player())
                                        .then(Commands.argument("color", StringArgumentType.string()).suggests((ctx, builder) -> {
                                                            if (EntityArgument.getPlayer(ctx, "entity") instanceof SoyPlayerExtension ext && ext.getTitanInstance().titan != null && ext.getTitanInstance().titan.baseEyeColor != null) return builder.suggest("\"#" + Integer.toHexString(ext.getTitanInstance().titan.baseEyeColor.getRGB()).substring(2) + "\"").buildFuture();
                                                            return builder.suggest("\"#ffffff\"").buildFuture();
                                                }).executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getPlayer(context, "entity");
                                                    var color = StringArgumentType.getString(context, "color");

                                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                                        var newColor = Color.decode(color);
                                                        playerExt.getTitanInstance().setEyeColor(newColor);
                                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.color.set", entity.getDisplayName(), Integer.toHexString(newColor.getRGB()).substring(2)), true);
                                                    } else {
                                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                        return 0;
                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("randomize")
                                .then(Commands.argument("entity", EntityArgument.player())
                                        .executes(context -> {
                                            var source = context.getSource();
                                            var entity = EntityArgument.getPlayer(context, "entity");

                                            if (entity instanceof SoyPlayerExtension playerExt) {
                                                playerExt.getTitanInstance().randomizeEyeColor();
                                                source.sendSuccess(() -> Component.translatable("commands.titan.success.color.randomize", entity.getDisplayName(), Integer.toHexString(playerExt.getTitanInstance().getEyeColor().getRGB()).substring(2)), true);
                                            } else {
                                                source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                return 0;
                                            }

                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("unshift")
                        .then(Commands.argument("entity", EntityArgument.player())
                                .executes(context -> {
                                    var source = context.getSource();
                                    var entity = EntityArgument.getPlayer(context, "entity");

                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                        if (playerExt.getTitanInstance().titan == null) {
                                            source.sendFailure(Component.translatable("commands.titan.error.noTitan", entity.getDisplayName()));
                                            return 0;
                                        }
                                        if (playerExt.getTitanInstance().getProgress() == 0) {
                                            source.sendFailure(Component.translatable("commands.titan.error.notShifted", entity.getDisplayName()));
                                            return 0;
                                        }
                                        playerExt.getTitanInstance().forceUnshift = true;
                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.unshift", entity.getDisplayName()), true);
                                    } else {
                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                        return 0;
                                    }

                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("points")
                        .then(Commands.literal("get")
                                .then(Commands.argument("entity", EntityArgument.player())
                                        .executes(context -> {
                                            var source = context.getSource();
                                            var entity = EntityArgument.getPlayer(context, "entity");

                                            var points = SoyProperties.PATH_POINTS.get(entity);
                                            source.sendSuccess(() -> Component.translatable("commands.titan.success.points.get", entity.getDisplayName(), points), true);

                                            return points;
                                        })
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("entity", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getPlayer(context, "entity");
                                                    var amount = IntegerArgumentType.getInteger(context, "amount");

                                                    var points = SoyProperties.PATH_POINTS.get(entity);
                                                    SoyProperties.PATH_POINTS.set(entity, amount);
                                                    source.sendSuccess(() -> Component.translatable("commands.titan.success.points.set", entity.getDisplayName(), amount, points), true);

                                                    return points;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("entity", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getPlayer(context, "entity");
                                                    var amount = IntegerArgumentType.getInteger(context, "amount");

                                                    var points = SoyProperties.PATH_POINTS.get(entity);
                                                    SoyProperties.PATH_POINTS.set(entity, amount + points);
                                                    if (amount >= 0) {
                                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.points.add", amount, entity.getDisplayName(), amount + points), true);
                                                    } else {
                                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.points.subtract", -amount, entity.getDisplayName(), amount + points), true);
                                                    }


                                                    return points;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("randomize")
                        .then(Commands.argument("entity", EntityArgument.player())
                                .executes(context -> {
                                    var source = context.getSource();
                                    var entity = EntityArgument.getPlayer(context, "entity");

                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                        if (playerExt.getTitanInstance().getProgress() > 0) {
                                            source.sendFailure(Component.translatable("commands.titan.error.shifted", entity.getDisplayName()));
                                            return 0;
                                        }
                                        Tuple<Titan, String> randomTitanAndVariant = TitanInstance.randomizeFor(entity);
                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.randomize", entity.getDisplayName(), randomTitanAndVariant.getA().id, randomTitanAndVariant.getB()), true);
                                    } else {
                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                        return 0;
                                    }

                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("get")
                        .then(Commands.argument("entity", EntityArgument.player())
                                .executes(context -> {
                                    var source = context.getSource();
                                    var entity = EntityArgument.getPlayer(context, "entity");

                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                        if (playerExt.getTitanInstance().titan == null) {
                                            source.sendSuccess(() -> Component.translatable("commands.titan.success.noTitan", entity.getDisplayName()), true);
                                            return 0;
                                        }
                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.get", entity.getDisplayName(), playerExt.getTitanInstance().titan.id, playerExt.getTitanInstance().variant), true);
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

    private static int setTitanAndVariant(LivingEntity entity, ResourceLocation id, String variant, CommandSourceStack source) {
        if (entity instanceof SoyPlayerExtension playerExt) {
            if (playerExt.getTitanInstance().getProgress() > 0) {
                source.sendFailure(Component.translatable("commands.titan.error.shifted", entity.getDisplayName()));
                return 0;
            }
            TitanInstance inst = new TitanInstance(entity, TitanRegistry.getTitan(id), variant);
            playerExt.setTitanInstance(inst);
            playerExt.getTitanInstance().setProgress(0);
            playerExt.getTitanInstance().setCharge(0);
            playerExt.getTitanInstance().setDecay(TitanInstance.START_CORPSE_DECAY);
            source.sendSuccess(() -> Component.translatable("commands.titan.success.set", entity.getDisplayName(), id, variant), true);
        } else {
            source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
            return 0;
        }

        return 1;
    }
}
