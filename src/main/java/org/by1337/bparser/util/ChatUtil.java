package org.by1337.bparser.util;



import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;

import java.util.function.UnaryOperator;

public class ChatUtil {

    public static MutableComponent addCopyButton(MutableComponent text, String msg, String data) {

        return addCopyButton(text, msg, data, ChatFormatting.GREEN);
    }

    public static MutableComponent addCopyButton(MutableComponent text, String msg, String data, ChatFormatting color) {
        return text.append(Component.literal(msg).withStyle(copyText(data, color)));
    }

    public static MutableComponent addCopyButton(MutableComponent text, String data) {
        return addCopyButton(text, " [copy]", data);
    }

    public static UnaryOperator<Style> copyText(String text) {
        return copyText(text, ChatFormatting.GREEN);
    }

    public static UnaryOperator<Style> copyText(String text, ChatFormatting color) {
        return s -> s.withClickEvent(new ClickEvent.CopyToClipboard(text))
                .withHoverEvent(new HoverEvent.ShowText(Component.literal("copy")))
                .withColor(color);
    }

    public static void show(Component text) {
        Minecraft.getInstance().gui.getChat().addMessage(text);
    }
}
