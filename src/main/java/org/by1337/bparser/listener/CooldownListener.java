package org.by1337.bparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

public class CooldownListener {

    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//cooldown_log")
                .executes(ctx -> {
                    Config.INSTANCE.cooldownLog = !Config.INSTANCE.cooldownLog;
                    if (Config.INSTANCE.cooldownLog) {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.cooldown.on"));
                    } else {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.cooldown.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );

        NetworkEvent.COOLDOWN_UPDATE.register(packet -> {
            if (!Config.INSTANCE.cooldownLog || !Thread.currentThread().getName().contains("Netty Client IO"))
                return;
            String material = Registry.ITEM.getKey(packet.getItem()).get().getValue().getPath();
            String text = "[cooldown] " + material + " " + packet.getCooldown();

            LiteralText msg = new LiteralText(text);
            ChatUtil.addCopyButton(msg, material + ": " +  packet.getCooldown());
            ChatUtil.show(msg);
        });
    }
}
