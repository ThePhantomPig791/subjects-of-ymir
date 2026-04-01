package net.phantompig.soy.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.threetag.palladium.client.renderer.WatcherRenderer;
import net.threetag.palladium.util.PlayerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InjectionItem extends SpinalFluidHoldingItem {
    public InjectionItem(Properties properties) {
        super(properties, 100);
    }

    @NotNull
    @Override
    public ItemStack getDefaultInstance() {
        var stack = super.getDefaultInstance();
        set(stack, 100);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) {
            if (WatcherRenderer.INSTANCE.getVisibility(1) > 0) sendMessage(player);
            return InteractionResultHolder.pass(stack);
        }
        if (inject(stack, player)) return InteractionResultHolder.consume(stack);
        // TODO injecting others
        return InteractionResultHolder.pass(stack);
    }

    private static boolean inject(ItemStack stack, Player player) {
        if (stack.getItem() instanceof InjectionItem inj && inj.get(stack) > 0) {
            injectSound(player.level(), player.getX(), player.getY(), player.getZ());
            inj.set(stack, 0);
            if (player instanceof ServerPlayer sp) CriteriaTriggers.USING_ITEM.trigger(sp, stack);
            return true;
        }
        return false;
    }

    private static void injectSound(Level level, double x, double y, double z) {
        // TODO better sound effect
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.HONEY_BLOCK_STEP, SoundSource.PLAYERS, 0.5f, 2);
        PlayerUtil.playSoundToAll(level, x, y, z, 16, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5f, 1.9f);
    }

    // thanks lucas! happy april fools everyone. thank you for all of your support!
    private static void sendMessage(Player player) {
        final String playerName = player.getDisplayName().getString();
        final List<String> messages = getMessages(playerName);
        RandomSource random = RandomSource.create();
        Component name = Component.literal("The Watcher").withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("I'm up here"))));
        Component message = Component.literal(String.format(messages.get(random.nextInt(messages.size())), playerName));
        player.displayClientMessage(Component.translatable("chat.type.text", name, message), false);
    }

    private static ArrayList<String> getMessages(String playerName) {
        final ArrayList<String> messages = new ArrayList<>(List.of("Ah, always the delusional one, %s.", "Since when were you a descendant of Ymir?", "After an observation of %s's blood, I have found zero traces of Ymir ancestry.", "You seem to be confused. Injections only affect Subjects of Ymir.", "How dumb... are you?", "You're not the protagonist, %s.", "%s. You're not him.", "You think you're Eren Jaeger?", "Only a Subject of Ymir will be affected by the serum. You know that, %s. I thought you'd be smarter.", "Did %s really think Ymir would choose them?", "In all my observations across every possible universe, I've never witnessed a more idiotic blunder than what %s just did.", "Obviously you're not a Subject of Ymir, %s!"));
        switch (playerName) {
            case "W3ndigoisreal" -> messages.add("I disagree. A Wendigo is an fictional creature; Wendigos are not real.");
            case "Cypherz1" -> messages.add("My observations indicate that you are, despite your \"bio\", chronically online.");
            case "DawnBreaker" -> messages.add("Ah, a fellow Davigo enjoyer.");
            case "ShadowLegacy557" -> messages.add("Owen. I thought you'd be smarter than that.");
            case "Flame_boy8" -> messages.add("Inferno. You really thought?");
            case "Robertm6" -> messages.add("Long time no see, Robert.");
            case "JolyRatio" -> messages.add("Ratio... so brave of you to show your face.");
            case "user__NULL" -> messages.add("Null. We've been awaiting your return. Welcome back.");
            default -> {}
        }
        return messages;
    }
}
