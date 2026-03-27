package net.phantompig.soy.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.phantompig.soy.util.ListUtil;

import java.awt.*;
import java.util.ArrayList;

public class TitanCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("titan").requires((player) -> player.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.player())
                        .then(Commands.literal("set")
                                .then(Commands.argument("titan", ResourceLocationArgument.id()).suggests(SUGGEST_TITANS)
                                        .then(Commands.argument("variant", StringArgumentType.string()).suggests(SUGGEST_VARIANTS)
                                                .executes(context -> setTitanAndVariant(EntityArgument.getPlayer(context, "entity"), ResourceLocationArgument.getId(context, "titan"), StringArgumentType.getString(context, "variant"), context.getSource()))
                                        )
                                        .executes(context -> setTitan(EntityArgument.getPlayer(context, "entity"), ResourceLocationArgument.getId(context, "titan"), context.getSource()))
                                )
                        )
                        .then(Commands.literal("remove")
                                .executes(context -> removeTitan(context, false))
                                .then(Commands.argument("pop_spine", BoolArgumentType.bool()).suggests((ctx, builder) -> builder.suggest("false").suggest("true").buildFuture())
                                        .executes(context -> removeTitan(context, BoolArgumentType.getBool(context, "pop_spine")))
                                )
                        )
                        .then(Commands.literal("eyes")
                                .then(Commands.literal("set")
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
                                .then(Commands.literal("randomize")
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
                        .then(Commands.literal("unshift")
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
                        .then(Commands.literal("points")
                                .then(Commands.literal("get")
                                        .executes(context -> {
                                            var source = context.getSource();
                                            var entity = EntityArgument.getPlayer(context, "entity");

                                            var points = SoyProperties.PATH_POINTS.get(entity);
                                            source.sendSuccess(() -> Component.translatable("commands.titan.success.points.get", entity.getDisplayName(), points), true);

                                            return points;
                                        })
                                )
                                .then(Commands.literal("set")
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
                                .then(Commands.literal("add")
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
                        .then(Commands.literal("randomize")
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
                        .then(Commands.literal("get")
                                .executes(context -> {
                                    var source = context.getSource();
                                    var entity = EntityArgument.getPlayer(context, "entity");

                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                        if (playerExt.getTitanInstance().titan == null) {
                                            source.sendSuccess(() -> Component.translatable("commands.titan.success.noTitan", entity.getDisplayName()), true);
                                            return 0;
                                        }
                                        ResourceLocation id = playerExt.getTitanInstance().titan.id;
                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.get", entity.getDisplayName(), id, playerExt.getTitanInstance().variant), true);
                                        return TitanRegistry.indexOf(id) + 1;
                                    } else {
                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                        return 0;
                                    }
                                })
                        )
                        .then(Commands.literal("stamina")
                                .then(Commands.literal("get")
                                        .executes(context -> {
                                            var source = context.getSource();
                                            var entity = EntityArgument.getPlayer(context, "entity");

                                            if (entity instanceof SoyPlayerExtension playerExt) {
                                                source.sendSuccess(() -> Component.translatable("commands.titan.success.stamina.get", entity.getDisplayName(), playerExt.getTitanInstance().getStamina(), playerExt.getTitanInstance().getMaxStamina()), true);
                                                return playerExt.getTitanInstance().getStamina();
                                            } else {
                                                source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                return 0;
                                            }
                                        })
                                )
                                .then(Commands.literal("set_max")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getPlayer(context, "entity");
                                                    var amount = IntegerArgumentType.getInteger(context, "amount");

                                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                                        int old = playerExt.getTitanInstance().getMaxStamina();
                                                        playerExt.getTitanInstance().setMaxStamina(amount);
                                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.stamina.set_max", entity.getDisplayName(), amount, old), true);
                                                    } else {
                                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                        return 0;
                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getPlayer(context, "entity");
                                                    var amount = IntegerArgumentType.getInteger(context, "amount");

                                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                                        int old = playerExt.getTitanInstance().getStamina();
                                                        playerExt.getTitanInstance().setStamina(amount);
                                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.stamina.set", entity.getDisplayName(), amount, old), true);
                                                    } else {
                                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                        return 0;
                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getPlayer(context, "entity");
                                                    var amount = IntegerArgumentType.getInteger(context, "amount");

                                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                                        if (amount > 0 && playerExt.getTitanInstance().getStamina() == playerExt.getTitanInstance().getMaxStamina()) {
                                                            source.sendFailure(Component.translatable("commands.titan.error.maxStamina", entity.getDisplayName()));
                                                            return 0;
                                                        }
                                                        playerExt.getTitanInstance().regainStamina(amount);
                                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.stamina.add", amount, entity.getDisplayName(), playerExt.getTitanInstance().getStamina()), true);
                                                    } else {
                                                        source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                        return 0;
                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("max")
                                        .executes(context -> {
                                            var source = context.getSource();
                                            var entity = EntityArgument.getPlayer(context, "entity");

                                            if (entity instanceof SoyPlayerExtension playerExt) {
                                                if (playerExt.getTitanInstance().getStamina() == playerExt.getTitanInstance().getMaxStamina()) {
                                                    source.sendFailure(Component.translatable("commands.titan.error.maxStamina", entity.getDisplayName()));
                                                    return 0;
                                                }
                                                int old = playerExt.getTitanInstance().getStamina();
                                                playerExt.getTitanInstance().setStamina(playerExt.getTitanInstance().getMaxStamina());
                                                source.sendSuccess(() -> Component.translatable("commands.titan.success.stamina.set", entity.getDisplayName(), playerExt.getTitanInstance().getStamina(), old), true);
                                            } else {
                                                source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                return 0;
                                            }

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("deaths")
                                .then(Commands.literal("get")
                                        .executes(context -> {
                                            var source = context.getSource();
                                            var entity = EntityArgument.getPlayer(context, "entity");

                                            if (entity instanceof SoyPlayerExtension playerExt) {
                                                source.sendSuccess(() -> Component.translatable("commands.titan.success.deaths.get", entity.getDisplayName(), playerExt.getTitanInstance().deaths), true);
                                            } else {
                                                source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
                                                return 0;
                                            }

                                            return 1;
                                        })
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    var source = context.getSource();
                                                    var entity = EntityArgument.getPlayer(context, "entity");
                                                    var amount = IntegerArgumentType.getInteger(context, "amount");

                                                    if (entity instanceof SoyPlayerExtension playerExt) {
                                                        int old = playerExt.getTitanInstance().deaths;
                                                        playerExt.getTitanInstance().deaths = amount;
                                                        source.sendSuccess(() -> Component.translatable("commands.titan.success.deaths.set", entity.getDisplayName(), amount, old), true);
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
        );
    }

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TITANS = (context, builder) -> SharedSuggestionProvider.suggest(TitanRegistry.getTitans().keySet().stream().map(ResourceLocation::toString), builder);
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_VARIANTS = (context, builder) -> {
        Titan titan = TitanRegistry.getTitan(ResourceLocationArgument.getId(context, "titan"));
        ArrayList<String> variants = new ArrayList<>();
        if (titan != null) variants.addAll(titan.variants);
        return SharedSuggestionProvider.suggest(variants, builder);
    };

    private static int setTitan(LivingEntity entity, ResourceLocation id, CommandSourceStack source) {
        if (!TitanRegistry.titanExists(id)) {
            source.sendFailure(Component.translatable("commands.titan.error.noSuchTitan", id));
            return 0;
        }
        return setTitan(entity, TitanRegistry.getTitan(id), source);
    }
    private static int setTitan(LivingEntity entity, Titan titan, CommandSourceStack source) {
        return setTitanAndVariant(entity, titan, ListUtil.getRandom(titan.variants), source);
    }
    private static int setTitanAndVariant(LivingEntity entity, ResourceLocation id, String variant, CommandSourceStack source) {
        if (!TitanRegistry.titanExists(id)) {
            source.sendFailure(Component.translatable("commands.titan.error.noSuchTitan", id));
            return 0;
        }
        return setTitanAndVariant(entity, TitanRegistry.getTitan(id), variant, source);
    }
    private static int setTitanAndVariant(LivingEntity entity, Titan titan, String variant, CommandSourceStack source) {
        if (entity instanceof SoyPlayerExtension playerExt) {
            if (!titan.variants.contains(variant)) {
                source.sendFailure(Component.translatable("commands.titan.error.noSuchVariant", titan.id, variant));
                return 0;
            }
            if (playerExt.getTitanInstance().getProgress() > 0) {
                source.sendFailure(Component.translatable("commands.titan.error.shifted", entity.getDisplayName()));
                return 0;
            }
            TitanInstance.setFor(entity, new Tuple<>(titan, variant));
            source.sendSuccess(() -> Component.translatable("commands.titan.success.set", entity.getDisplayName(), titan.id, variant), true);
        } else {
            source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
            return 0;
        }

        return 1;
    }

    private static int removeTitan(CommandContext<CommandSourceStack> context, boolean popSpine) throws CommandSyntaxException {
        var source = context.getSource();
        var entity = EntityArgument.getPlayer(context, "entity");

        if (entity instanceof SoyPlayerExtension playerExt) {
            if (playerExt.getTitanInstance().getProgress() > 0) {
                source.sendFailure(Component.translatable("commands.titan.error.shifted", entity.getDisplayName()));
                return 0;
            }
            if (popSpine) {
                entity.drop(TitanInstance.toSpineIem(playerExt.getTitanInstance()), false, true);
                source.sendSuccess(() -> Component.translatable("commands.titan.success.remove_pop", entity.getDisplayName()), true);
            } else {
                source.sendSuccess(() -> Component.translatable("commands.titan.success.remove", entity.getDisplayName()), true);
            }
            TitanInstance.clearTitanFor(entity);
        } else {
            source.sendFailure(Component.translatable("commands.titan.error.notPlayer"));
            return 0;
        }

        return 1;
    }
}
