package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

public class EffectListener {
    public EffectListener() {
        NetworkEvent.MOB_EFFECT.register(packet -> {
            if (!Config.INSTANCE.effectLog ) return;
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null && packet.getEntityId() == mc.player.getId()) {
                var effect = packet.getEffect();
                StringBuilder sb = new StringBuilder();
                String effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect.value()).getPath();
                String amplifier = Integer.toString(packet.getEffectAmplifier());
                String duration = Integer.toString(packet.getEffectDurationTicks());
                sb.append("effect: ").append(effectId);
                sb.append(" amplifier: ").append(amplifier);
                sb.append(" duration: ").append(duration);
                sb.append(" showParticles: ").append(packet.isEffectVisible());
                sb.append(" ambient: ").append(packet.isEffectAmbient());
                sb.append(" showIcon: ").append(packet.effectShowsIcon());


                MutableComponent text = Component.literal("effect: ")
                        .append(Component.literal(effectId).withStyle(ChatUtil.copyText(effectId)))
                        .append(", amplifier: ").append(Component.literal(amplifier).withStyle(ChatUtil.copyText(amplifier)))
                        .append(", duration: ").append(Component.literal(duration).withStyle(ChatUtil.copyText(duration)));

                ChatUtil.addCopyButton(text, sb.toString());

                ChatUtil.show(text);
            }
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//effect_log")
                .executes(ctx -> {
                    Config.INSTANCE.effectLog = !Config.INSTANCE.effectLog;
                    if (Config.INSTANCE.effectLog) {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.effect.on"));
                    } else {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.effect.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );
    }
}
