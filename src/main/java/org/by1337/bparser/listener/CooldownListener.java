package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

public class CooldownListener {
    public CooldownListener() {
        NetworkEvent.COOLDOWN_UPDATE.register(packet -> {
            if (!Config.INSTANCE.cooldownLog )
                return;
            String material = Registries.ITEM.getKey(packet.getItem()).get().getValue().getPath();
            String text = "[cooldown] " + material + " " + packet.getCooldown();

            MutableText msg = Text.literal(text);
            ChatUtil.addCopyButton(msg, material + ": " +  packet.getCooldown());
            ChatUtil.show(msg);
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//cooldown_log")
                .executes(ctx -> {
                    Config.INSTANCE.cooldownLog = !Config.INSTANCE.cooldownLog;
                    if (Config.INSTANCE.cooldownLog) {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.cooldown.on"));
                    } else {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.cooldown.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );
    }
}
