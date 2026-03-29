package net.phantompig.soy.titan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.phantompig.soy.SubjectsOfYmir;
import net.phantompig.soy.player.SoyPlayerExtension;
import net.phantompig.soy.sound.SoySounds;
import net.phantompig.soy.util.ListUtil;
import net.threetag.palladium.util.PlayerUtil;

import java.util.ArrayList;
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
        HashSet<String> messages = new HashSet<>(historicalMessages); // make a list of all the historic messages
        messages.removeIf(string -> Math.random() < 0.3); // remove 30% of them
        messages.addAll(currentHolderMessages); // add the messages from the current holder (who just lost their titan)
        return new TitanMemoryManager(messages);
    }

    public void onChat(Player player, String raw) {
        if (Math.random() < 0.01 || (Math.random() < 0.2 && player instanceof SoyPlayerExtension ext && ext.getTitanInstance().getProgress() > 0)) {
            this.currentHolderMessages.add("<" + player.getDisplayName().getString() + "> " + raw);
            PlayerUtil.playSound(player, player.getX(), player.getEyeY(), player.getZ(), SoySounds.HEARTBEAT.get(), SoundSource.PLAYERS);
        }
    }

    public void tick(ServerPlayer player) {
        // roughly once every ten minutes
        if (!this.historicalMessages.isEmpty() && Math.random() < (1f / 20 / 60 / 10)) {
            player.sendSystemMessage(Component.literal(ListUtil.getRandom(this.historicalMessages.stream().toList())), false);
            PlayerUtil.playSound(player, player.getX(), player.getEyeY(), player.getZ(), SoySounds.HEARTBEAT.get(), SoundSource.PLAYERS, 1, 0.9f + (float) (0.1 * Math.random()));
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


    public static List<String> ANCIENT_MESSAGES = new ArrayList<>();

    static {
        ANCIENT_MESSAGES.add("We must keep trying");
        ANCIENT_MESSAGES.add("Fight!");
        ANCIENT_MESSAGES.add("Baked potatoes");
        ANCIENT_MESSAGES.add("We're running out of time");
        ANCIENT_MESSAGES.add("Use your Titan!");
        ANCIENT_MESSAGES.add("Surrender.");
        ANCIENT_MESSAGES.add("Never surrender!");
        ANCIENT_MESSAGES.add("Don't give up!");
        ANCIENT_MESSAGES.add("Ha.");
        ANCIENT_MESSAGES.add("It's so over");
        ANCIENT_MESSAGES.add("...");
        ANCIENT_MESSAGES.add("The time is now");
        ANCIENT_MESSAGES.add("We meet at dawn");
        ANCIENT_MESSAGES.add("Draw blood with your dagger, then transform");
        ANCIENT_MESSAGES.add("They're inside the walls");
        ANCIENT_MESSAGES.add("Sleep tight");
        ANCIENT_MESSAGES.add("And fall");
        ANCIENT_MESSAGES.add("Who are you?");
        ANCIENT_MESSAGES.add("Where am I?");
        ANCIENT_MESSAGES.add("Run!");
        ANCIENT_MESSAGES.add("I see");
        ANCIENT_MESSAGES.add("Oh, ok");
        ANCIENT_MESSAGES.add("Like a warm sauna");
        ANCIENT_MESSAGES.add("Don't overuse your Titan");
        ANCIENT_MESSAGES.add("You're the owl?");
        ANCIENT_MESSAGES.add("Make yourself at home");
        ANCIENT_MESSAGES.add("Stay a while");
        ANCIENT_MESSAGES.add("Painful");
        ANCIENT_MESSAGES.add("It hurts");
        ANCIENT_MESSAGES.add("Only 13 years... 13 lives");
        ANCIENT_MESSAGES.add("Spine?");
        ANCIENT_MESSAGES.add("Who's the real enemy?");
        ANCIENT_MESSAGES.add("Face the consequences of your actions.");
        ANCIENT_MESSAGES.add("A recurring nightmare");
        ANCIENT_MESSAGES.add("Give up");
        ANCIENT_MESSAGES.add("They were not");
        ANCIENT_MESSAGES.add("Rage!");
        ANCIENT_MESSAGES.add("Die on the battlefield in glory");
        ANCIENT_MESSAGES.add("I'd kill you no matter what.");
        ANCIENT_MESSAGES.add("Fly somewhere... far away");
        ANCIENT_MESSAGES.add("Race of devils");
        ANCIENT_MESSAGES.add("For Paradis");
        ANCIENT_MESSAGES.add("For Eldia");
        ANCIENT_MESSAGES.add("For Marley");
        ANCIENT_MESSAGES.add("Hear me, all Subjects of Ymir!");
        ANCIENT_MESSAGES.add("Hatred");
        ANCIENT_MESSAGES.add("Rejection");
        ANCIENT_MESSAGES.add("Love");
        ANCIENT_MESSAGES.add("War");
        ANCIENT_MESSAGES.add("Peace");
        ANCIENT_MESSAGES.add("I'm sorry");
        ANCIENT_MESSAGES.add("Innocence");
        ANCIENT_MESSAGES.add("I was born into this world");
        ANCIENT_MESSAGES.add("Falling behind");
        ANCIENT_MESSAGES.add("Vice");
        ANCIENT_MESSAGES.add("Dedicate your hearts!");
        ANCIENT_MESSAGES.add("Soldiers!");
        ANCIENT_MESSAGES.add("Warriors!");
        ANCIENT_MESSAGES.add("Ymir");
        ANCIENT_MESSAGES.add("Rage! Scream! Fight!");
        ANCIENT_MESSAGES.add("Fight. Fight.");
        ANCIENT_MESSAGES.add("We are all free");
        ANCIENT_MESSAGES.add("Kill them all");
        ANCIENT_MESSAGES.add("We're the same");
        ANCIENT_MESSAGES.add("Beauty");
        ANCIENT_MESSAGES.add("Cage");
        ANCIENT_MESSAGES.add("Art");
        ANCIENT_MESSAGES.add("Nerd");
        ANCIENT_MESSAGES.add("Abandon your humanity");
        ANCIENT_MESSAGES.add("Sacrifice everything");
        ANCIENT_MESSAGES.add("Give meaning to their lives!");
        ANCIENT_MESSAGES.add("The world doesn't need another hero");
        ANCIENT_MESSAGES.add("To keep pushing on");
        ANCIENT_MESSAGES.add("Be yourself!");
        ANCIENT_MESSAGES.add("Who am I?");
        ANCIENT_MESSAGES.add("Life a life you can be proud of");
        ANCIENT_MESSAGES.add("See you later");
        ANCIENT_MESSAGES.add("You are free");
        ANCIENT_MESSAGES.add("Complete your mission");
        ANCIENT_MESSAGES.add("Destroy your enemies");
        ANCIENT_MESSAGES.add("Mercy");
        ANCIENT_MESSAGES.add("Curiosity");
        ANCIENT_MESSAGES.add("Suppression");
        ANCIENT_MESSAGES.add("Party of none");
        ANCIENT_MESSAGES.add("Body");
        ANCIENT_MESSAGES.add("Corpse");
        ANCIENT_MESSAGES.add("The cat");
        ANCIENT_MESSAGES.add("The hat");
        ANCIENT_MESSAGES.add("Clean this up");
        ANCIENT_MESSAGES.add("Filth");
        ANCIENT_MESSAGES.add("And just like that, everything changed");
        ANCIENT_MESSAGES.add("Surrender your heart");
        ANCIENT_MESSAGES.add("Humanity, cattle");
        ANCIENT_MESSAGES.add("Hi there, hello");
        ANCIENT_MESSAGES.add("Just mercy");
        ANCIENT_MESSAGES.add("I feel the same");
        ANCIENT_MESSAGES.add("I wished from the bottom of my heart");
        ANCIENT_MESSAGES.add("Surrounded by idiots");
        ANCIENT_MESSAGES.add("Nothing changed.");
        ANCIENT_MESSAGES.add("So many people died");
        ANCIENT_MESSAGES.add("So much death");
        ANCIENT_MESSAGES.add("All destruction");
        ANCIENT_MESSAGES.add("Mountain of corpses");
        ANCIENT_MESSAGES.add("Visit the basement");
        ANCIENT_MESSAGES.add("Line up! Cavalry!");
        ANCIENT_MESSAGES.add("We die fighting.");
        ANCIENT_MESSAGES.add("Meaningless!");
        ANCIENT_MESSAGES.add("Across the sea");
        ANCIENT_MESSAGES.add("Freedom awaits");
        ANCIENT_MESSAGES.add("Tough, tough luck");
        ANCIENT_MESSAGES.add("A declaration of war");
        ANCIENT_MESSAGES.add("Lend me your strength!");
        ANCIENT_MESSAGES.add("Free will");
        ANCIENT_MESSAGES.add("Aurora Borealis?");
        ANCIENT_MESSAGES.add("Always look on the bright side of life");
        ANCIENT_MESSAGES.add("In the gallows");
        ANCIENT_MESSAGES.add("You've suffered enough.");
        ANCIENT_MESSAGES.add("Just go to sleep!");
        ANCIENT_MESSAGES.add("You're not done yet. In case you've forgotten.");
        ANCIENT_MESSAGES.add("Feast, my daughters!");
        ANCIENT_MESSAGES.add("You can make your own choices");
        ANCIENT_MESSAGES.add("The island where I was born");
        ANCIENT_MESSAGES.add("All that remains of humanity");
        ANCIENT_MESSAGES.add("Hate. Hate.");
        ANCIENT_MESSAGES.add("Glad I'm not you");
        ANCIENT_MESSAGES.add("Why not?");
        ANCIENT_MESSAGES.add("Sunny side down");
        ANCIENT_MESSAGES.add("I always could count on you");
        ANCIENT_MESSAGES.add("You should count chickens professionally");
        ANCIENT_MESSAGES.add("Sic semper tyrannis");
        ANCIENT_MESSAGES.add("Goodbye!");
        ANCIENT_MESSAGES.add("Forever!");
        ANCIENT_MESSAGES.add("Because this world is just that cruel");
        ANCIENT_MESSAGES.add("Be careful with that injection");
        ANCIENT_MESSAGES.add("What's in that syringe?");
        ANCIENT_MESSAGES.add("Hello?");
        ANCIENT_MESSAGES.add("Is anyone there?");
        ANCIENT_MESSAGES.add("Let me help you!");
        ANCIENT_MESSAGES.add("Before the fall");
        ANCIENT_MESSAGES.add("Greater power");
        ANCIENT_MESSAGES.add("Worm");
        ANCIENT_MESSAGES.add("Extinction is imminent");
        ANCIENT_MESSAGES.add("Euthanasia is the key");
        ANCIENT_MESSAGES.add("I want a utopia");
        ANCIENT_MESSAGES.add("I've killed so many of them");
        ANCIENT_MESSAGES.add("Crystallize!");
        ANCIENT_MESSAGES.add("Are you my friend?");
        ANCIENT_MESSAGES.add("Why am I going through this?");
        ANCIENT_MESSAGES.add("Can you help me?");
        ANCIENT_MESSAGES.add("Help yourself.");
        ANCIENT_MESSAGES.add("Mother");
        ANCIENT_MESSAGES.add("Where is my family?");
        ANCIENT_MESSAGES.add("My Power of the Titans");
        ANCIENT_MESSAGES.add("For all humanity");
        ANCIENT_MESSAGES.add("You serve me.");
        ANCIENT_MESSAGES.add("Please, don't");
        ANCIENT_MESSAGES.add("Coordinate");
        ANCIENT_MESSAGES.add("Over here!");
        ANCIENT_MESSAGES.add("Leave me, go");
        ANCIENT_MESSAGES.add("Shift! Use your Power!");
        ANCIENT_MESSAGES.add("When you heal, you seem to emit steam.");
        ANCIENT_MESSAGES.add("The blade can't slice the crystal");
        ANCIENT_MESSAGES.add("Time your punches");
        ANCIENT_MESSAGES.add("Roundhouse kick!");
        ANCIENT_MESSAGES.add("I'm at your house");
        ANCIENT_MESSAGES.add("There are monsters nearby");
        ANCIENT_MESSAGES.add("Gross face marks");
        ANCIENT_MESSAGES.add("I know the end");
        ANCIENT_MESSAGES.add("they're really all gone");
        ANCIENT_MESSAGES.add("where did my family go?");
        ANCIENT_MESSAGES.add("will anyone help?");
        ANCIENT_MESSAGES.add("Let me tell you something");
        ANCIENT_MESSAGES.add("are you here to save us?");
        ANCIENT_MESSAGES.add("it's dark in here");
        ANCIENT_MESSAGES.add("how do these powers work?");
        ANCIENT_MESSAGES.add("Titan food");
    }

    public void populateWithAncientMessages(String name) {
        name = name.substring(0, 1).toUpperCase().concat(name.substring(1));
        ArrayList<String> available = new ArrayList<>(ANCIENT_MESSAGES);
        int count = (int) (10 * Math.random() + 5);
        for (int i = 0; i < count && !available.isEmpty(); i++) {
            this.historicalMessages.add("<" + name + "> " + available.remove((int) (available.size() * Math.random())));
        }
    }
}
