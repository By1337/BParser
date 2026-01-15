package org.by1337.bparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.text.RawMessageConvertor;
import org.by1337.bparser.util.ChatUtil;

import java.util.UUID;

public class TitleListener {
    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//title_log")
                .executes(ctx -> {
                    Config.INSTANCE.titleLog = !Config.INSTANCE.titleLog;
                    if (Config.INSTANCE.titleLog) {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.title.on"));
                    } else {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.title.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );

        NetworkEvent.TITLE.register(packet -> {
            if (!Config.INSTANCE.titleLog || !MinecraftClient.getInstance().isOnThread())
                return;
            LiteralText text = new LiteralText(
                    "[TITLE: " + Actions.values[packet.getAction().ordinal()] + "]"
            );
            if (packet.getAction() == TitleS2CPacket.Action.TIMES) {
                text.append(new LiteralText(" in: " + packet.getFadeInTicks()).styled(ChatUtil.copyText(String.valueOf(packet.getFadeInTicks()))));
                text.append(new LiteralText(" stay: " + packet.getStayTicks()).styled(ChatUtil.copyText(String.valueOf(packet.getStayTicks()))));
                text.append(new LiteralText(" out: " + packet.getFadeOutTicks()).styled(ChatUtil.copyText(String.valueOf(packet.getFadeOutTicks()))));
            }
            if (packet.getText() != null) {
                text.append(new LiteralText(" text: "))
                        .append(packet.getText());
                String raw = Text.Serializer.toJson(packet.getText());
                ChatUtil.addCopyButton(text, " [copy]",
                        RawMessageConvertor.convert(raw));
            }
            MinecraftClient.getInstance().inGameHud.addChatMessage(net.minecraft.network.MessageType.CHAT,text , UUID.randomUUID());
        });
    }

    public enum Actions {
        TITLE,
        SUBTITLE,
        ACTIONBAR,
        TIMES,
        CLEAR,
        RESET;
        private static Actions[] values = Actions.values();
    }

}
