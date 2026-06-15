package net.phantompig.soy.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil {
    public static final List<Character> EXTRA_WIDTH_CHARACTERS = List.of(
            'm', 'w', 'M', 'W', '%'
    ), WIDE_CHARACTERS = List.of(
            'A', 'D', 'G', 'H', 'O', 'Q', 'U', 'V', 'X', 'Y', 'Z', 'O', '@', '#'
    ), NORMAL_WIDTH_CHARACTERS = List.of(
            'a', 'b', 'c', 'd', 'e', 'g', 'h', 'k', 'n', 'o', 'p', 'q', 'r', 's', 'u', 'v', 'x', 'z',
            'B', 'C', 'E', 'F', 'K', 'N', 'P', 'R', 'S', 'T', 'U',
            '0', '2', '3', '4', '5', '6', '7', '8', '9',
            '$', '&', '+', '=', '<', '>', '?', '^', '_', '{', '}'
    ), NARROW_WIDTH_CHARACTERS = List.of(
            'f', 't', 'I', 'J', 'L', '1',
            '/', '\\', '*', '-', '[', ']', '(', ')'
    ), EXTRA_NARROW_CHARACTERS = List.of(
            'i', 'j', 'l', '!', '.', ',', ':', ';', '\'', '"', '`', '|', '~'
    );
    public static final List<List<Character>> CHARACTER_WIDTHS = List.of(EXTRA_WIDTH_CHARACTERS, WIDE_CHARACTERS, NORMAL_WIDTH_CHARACTERS, NARROW_WIDTH_CHARACTERS, EXTRA_NARROW_CHARACTERS);

    public static Component gibberishify(Component message) {
        return Component.literal(gibbberishify(message.getString())).withStyle(ChatFormatting.OBFUSCATED);
    }

    public static String gibbberishify(String string) {
        StringBuilder builder = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == ' ') { // frequent edge case where i can skip the iterating
                builder.append(' ');
                continue;
            }
            boolean replaced = false;
            for (List<Character> list : CHARACTER_WIDTHS) {
                if (list.contains(c)) {
                    List<Character> options = new ArrayList<>(list);
                    options.remove(options.indexOf(c));
                    builder.append(options.get((int) (options.size() * Math.random())));
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
