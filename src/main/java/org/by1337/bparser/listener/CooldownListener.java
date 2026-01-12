package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

public class CooldownListener {
    public CooldownListener() {
        NetworkEvent.COOLDOWN_UPDATE.register(packet -> {
            if (!Config.INSTANCE.cooldownLog )
                return;
            String material = packet.cooldownGroup().getPath();
            String text = "[cooldown] " + material + " " + packet.duration();

            MutableComponent msg = Component.literal(text);
            ChatUtil.addCopyButton(msg, material + ": " +  packet.duration());
            ChatUtil.show(msg);
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//cooldown_log")
                .executes(ctx -> {
                    Config.INSTANCE.cooldownLog = !Config.INSTANCE.cooldownLog;
                    if (Config.INSTANCE.cooldownLog) {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.cooldown.on"));
                    } else {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.cooldown.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );
    }
}
