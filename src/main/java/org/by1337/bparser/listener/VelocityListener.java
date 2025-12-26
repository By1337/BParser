package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

public class VelocityListener {
    public VelocityListener() {
        NetworkEvent.VELOCITY_UPDATE.register(data -> {
            if (!Config.INSTANCE.velocityLog) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && data.id() == mc.player.getId()) {
                ChatUtil.show(Text.of("velocity " + data.x() + " " + data.y() + " " + data.z()));
            }
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//velocity_log")
                .executes(ctx -> {
                    Config.INSTANCE.velocityLog = !Config.INSTANCE.velocityLog;
                    if (Config.INSTANCE.velocityLog) {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.velocity.on"));
                    } else {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.velocity.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );


    }
}
