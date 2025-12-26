package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

public class EffectListener {
    public EffectListener() {
        NetworkEvent.MOB_EFFECT.register(packet -> {
            if (!Config.INSTANCE.effectLog ) return;
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc.player != null && packet.getEntityId() == mc.player.getId()) {
                StatusEffect effect = packet.getEffectId();
                StringBuilder sb = new StringBuilder();
                String effectId = Registries.STATUS_EFFECT.getId(effect).getPath();
                String amplifier = Integer.toString(Byte.toUnsignedInt(packet.getAmplifier()));
                String duration = Integer.toString(packet.getDuration());
                sb.append("effect: ").append(effectId);
                sb.append(" amplifier: ").append(amplifier);
                sb.append(" duration: ").append(duration);
                sb.append(" showParticles: ").append(packet.shouldShowParticles());
                sb.append(" ambient: ").append(packet.isAmbient());
                sb.append(" showIcon: ").append(packet.shouldShowIcon());


                MutableText text = Text.literal("effect: ")
                        .append(Text.literal(effectId).styled(ChatUtil.copyText(effectId)))
                        .append(", amplifier: ").append(Text.literal(amplifier).styled(ChatUtil.copyText(amplifier)))
                        .append(", duration: ").append(Text.literal(duration).styled(ChatUtil.copyText(duration)));

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
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.effect.on"));
                    } else {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.effect.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );
    }
}
