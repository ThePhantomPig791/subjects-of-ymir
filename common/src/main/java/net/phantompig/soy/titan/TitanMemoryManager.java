package net.phantompig.soy.titan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.util.ListUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TitanMemoryManager {
    public HashSet<String> currentHolderMessages;
    public HashSet<String> historicalMessages;

    public TitanMemoryManager() {
        this(new HashSet<>(), new HashSet<>());
    }
    public TitanMemoryManager(HashSet<String> historicalMessages) {
        this(new HashSet<>(), historicalMessages);
    }
    public TitanMemoryManager(HashSet<String> currentHolderMessages, HashSet<String> historicalMessages) {
        this.currentHolderMessages = currentHolderMessages;
        this.historicalMessages = historicalMessages;
    }

    public TitanMemoryManager inherited() {
        HashSet<String> messages = new HashSet<>(historicalMessages);
        messages.removeIf(string -> Math.random() < 0.3);
        messages.addAll(currentHolderMessages);
        return new TitanMemoryManager(messages);
    }

    public void onChat(Player player, String raw) {
        if (Math.random() < 0.01) {
            this.currentHolderMessages.add("<" + player.getDisplayName().getString() + "> " + raw);
        }
    }

    public void tick(ServerPlayer player) {
        // roughly once every ten minutes
        if (!this.historicalMessages.isEmpty() && Math.random() < (1f / 20 / 60 / 10)) {
            player.sendSystemMessage(Component.literal(ListUtil.getRandom(this.historicalMessages.stream().toList())), false);
        }
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        list.addAll(currentHolderMessages.stream().map(StringTag::valueOf).toList());
        tag.put("CurrentHolderMessages", list);
        list = new ListTag();
        list.addAll(historicalMessages.stream().map(StringTag::valueOf).toList());
        tag.put("HistoricalMessages", list);
        return tag;
    }

    public static TitanMemoryManager fromTag(CompoundTag tag) {
        SubjectsOfYmir.LOGGER.info(tag.toString());
        return new TitanMemoryManager(
                new HashSet<>(getStringList(tag, "CurrentHolderMessages")),
                new HashSet<>(getStringList(tag, "HistoricalMessages"))
        );
    }

    private static List<String> getStringList(CompoundTag tag, String key) {
        if (tag.contains(key)) return tag.getList(key, CompoundTag.TAG_STRING).stream().map(Tag::getAsString).toList();
        return Collections.emptyList();
    }
}
