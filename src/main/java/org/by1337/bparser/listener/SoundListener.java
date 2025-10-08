package org.by1337.bparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.event.SoundEvent;
import org.by1337.bparser.event.SoundEventListener;

import java.util.UUID;

public class SoundListener {



    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//sound_log")
                .executes(ctx -> {
                    Config.INSTANCE.soundLog = !Config.INSTANCE.soundLog;
                    if (Config.INSTANCE.soundLog) {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.sound.on"));
                    } else {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.sound.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );

        NetworkEvent.SOUND_EVENT.register(new SoundEventListener() {
            @Override
            public void on(SoundEvent event) {
                if (!Config.INSTANCE.soundLog || !Thread.currentThread().getName().contains("Netty Client IO")) return;
                LiteralText text = new LiteralText("[sound] ");
                String sound = event.getSound().getId().getPath();
                text.append(new LiteralText(sound)
                        .styled(s -> s
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                                        sound
                                ))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(sound)))
                        )
                );
                text.append(" volume: ");
                text.append(new LiteralText(String.valueOf(event.getVolume()))
                        .styled(s -> s
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(event.getVolume())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(String.valueOf(event.getVolume()))))
                        )
                );
                text.append(" pitch: ");
                text.append(new LiteralText(String.valueOf(event.getPitch()))
                        .styled(s -> s
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(event.getPitch())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(String.valueOf(event.getPitch()))))
                        )
                );
                MinecraftClient.getInstance().inGameHud.addChatMessage(net.minecraft.network.MessageType.CHAT, text, UUID.randomUUID());
            }
        });

    }
}
