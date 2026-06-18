package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.event.SoundEvent;
import org.by1337.bparser.event.SoundEventListener;
import org.by1337.bparser.util.ChatUtil;

import java.text.DecimalFormat;

public class SoundListener {

    private final DecimalFormat df = new DecimalFormat("#.####");
    public SoundListener() {
        NetworkEvent.SOUND_EVENT.register(event -> {
            if (!Config.INSTANCE.soundLog) return;
            String sound = BuiltInRegistries.SOUND_EVENT.getKey(event.getSound().value()).getPath();
            String out = String.format("sound: %s %s %s", sound, df.format(event.getVolume()), df.format(event.getPitch()));

            ChatUtil.showCopiable(out);
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
