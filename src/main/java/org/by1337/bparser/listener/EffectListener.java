package org.by1337.bparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

public class EffectListener {
    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//effect_log")
                .executes(ctx -> {
                    Config.INSTANCE.effectLog = !Config.INSTANCE.effectLog;
                    if (Config.INSTANCE.effectLog) {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.effect.on"));
                    } else {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.effect.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );

        NetworkEvent.MOB_EFFECT.register(packet -> {
            if (!Config.INSTANCE.effectLog || !MinecraftClient.getInstance().isOnThread()) return;
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc.player != null && packet.getEntityId() == mc.player.getEntityId()) {
                StatusEffect effect = StatusEffect.byRawId(Byte.toUnsignedInt(packet.getEffectId()));
                StringBuilder sb = new StringBuilder();
                String effectId = Registry.STATUS_EFFECT.getId(effect).getPath();
                String amplifier = Integer.toString(Byte.toUnsignedInt(packet.getAmplifier()));
                String duration = Integer.toString(packet.getDuration());
                sb.append("effect: ").append(effectId);
                sb.append(" amplifier: ").append(amplifier);
                sb.append(" duration: ").append(duration);
                sb.append(" showParticles: ").append(packet.shouldShowParticles());
                sb.append(" ambient: ").append(packet.isAmbient());
                sb.append(" showIcon: ").append(packet.shouldShowIcon());


                MutableText text = new LiteralText("effect: ")
                        .append(new LiteralText(effectId).styled(ChatUtil.copyText(effectId)))
                        .append(", amplifier: ").append(new LiteralText(amplifier).styled(ChatUtil.copyText(amplifier)))
                        .append(", duration: ").append(new LiteralText(duration).styled(ChatUtil.copyText(duration)));

                ChatUtil.addCopyButton(text, sb.toString());

                ChatUtil.show(text);
            }
        });

    }
}
