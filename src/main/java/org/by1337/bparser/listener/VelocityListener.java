package org.by1337.bparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;

import java.util.UUID;

public class VelocityListener {
    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//velocity_log")
                .executes(ctx -> {
                    Config.INSTANCE.velocityLog = !Config.INSTANCE.velocityLog;
                    if (Config.INSTANCE.velocityLog) {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.velocity.on"));
                    } else {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.velocity.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );

        NetworkEvent.VELOCITY_UPDATE.register(data -> {
            if (!Config.INSTANCE.velocityLog || !Thread.currentThread().getName().contains("Netty Client IO")) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && data.id() == mc.player.getEntityId()) {
                MinecraftClient.getInstance().inGameHud.addChatMessage(net.minecraft.network.MessageType.CHAT, Text.of("велосити " + data.x() + " " + data.y() + " " + data.z()), UUID.randomUUID());
            }
        });

    }
}
