package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.event.SoundEvent;
import org.by1337.bparser.event.SoundEventListener;
import org.by1337.bparser.util.ChatUtil;

import java.util.UUID;

public class SoundListener {

    public SoundListener() {
        NetworkEvent.SOUND_EVENT.register(new SoundEventListener() {
            @Override
            public void on(SoundEvent event) {
                if (!Config.INSTANCE.soundLog ) return;
                MutableText text = Text.literal("[sound] ");
                String sound = event.getSound().getKey().get().getValue().getPath();
                text.append(Text.literal(sound)
                        .styled(s -> s
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                                        sound
                                ))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(sound)))
                        )
                );
                text.append(" volume: ");
                text.append(Text.literal(String.valueOf(event.getVolume()))
                        .styled(s -> s
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(event.getVolume())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(String.valueOf(event.getVolume()))))
                        )
                );
                text.append(" pitch: ");
                text.append(Text.literal(String.valueOf(event.getPitch()))
                        .styled(s -> s
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(event.getPitch())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(String.valueOf(event.getPitch()))))
                        )
                );
                ChatUtil.show(text);
            }
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//sound_log")
                .executes(ctx -> {
                    Config.INSTANCE.soundLog = !Config.INSTANCE.soundLog;
                    if (Config.INSTANCE.soundLog) {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.sound.on"));
                    } else {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.sound.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );



    }
}
