package org.by1337.bparser.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.function.UnaryOperator;

public class ChatUtil {

    public static MutableText addCopyButton(MutableText text, String msg, String data) {
        return addCopyButton(text, msg, data, Formatting.GREEN);
    }

    public static MutableText addCopyButton(MutableText text, String msg, String data, Formatting color) {
        return text.append(Text.literal(msg).styled(copyText(data, color)));
    }

    public static MutableText addCopyButton(MutableText text, String data) {
        return addCopyButton(text, " [copy]", data);
    }

    public static UnaryOperator<Style> copyText(String text) {
        return copyText(text, Formatting.GREEN);
    }

    public static UnaryOperator<Style> copyText(String text, Formatting color) {
        return s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("copy")))
                .withColor(color);
    }

    public static void show(Text text) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
    }
}
