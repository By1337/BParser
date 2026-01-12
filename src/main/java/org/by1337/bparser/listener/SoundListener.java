package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
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
                MutableComponent text = Component.literal("[sound] ");

                String sound = BuiltInRegistries.SOUND_EVENT.getKey(event.getSound().value()).getPath();
                text.append(Component.literal(sound)
                        .withStyle(s -> s
                                .withClickEvent(new ClickEvent.CopyToClipboard(sound))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(sound)))
                        )
                );
                text.append(" volume: ");
                text.append(Component.literal(String.valueOf(event.getVolume()))
                        .withStyle(s -> s
                                .withClickEvent(new ClickEvent.CopyToClipboard(String.valueOf(event.getVolume())))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(String.valueOf(event.getVolume()))))
                        )
                );
                text.append(" pitch: ");
                text.append(Component.literal(String.valueOf(event.getPitch()))
                        .withStyle(s -> s
                                .withClickEvent(new ClickEvent.CopyToClipboard(String.valueOf(event.getPitch())))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(String.valueOf(event.getPitch()))))
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
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.sound.on"));
                    } else {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.sound.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );



    }
}
