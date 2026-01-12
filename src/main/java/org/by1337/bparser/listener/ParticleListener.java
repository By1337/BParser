package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

import java.util.UUID;

public class ParticleListener {
    public ParticleListener() {
        NetworkEvent.PARTICLE.register(packet -> {
            if (!Config.INSTANCE.particleLog ) return;
            MutableComponent text = Component.literal("[particle]: ");
            String type = BuiltInRegistries.PARTICLE_TYPE.getKey(packet.getParticle().getType()).getPath();

            appendClickable(text, "type: " + type + " ");
            if (packet.getParticle() instanceof BlockParticleOption block) {
                String b = BuiltInRegistries.BLOCK.getKey(block.getState().getBlock()).getPath();
                appendClickable(text, "dust-param: " + b + " ");
            } else if (packet.getParticle() instanceof DustParticleOptions dust) {
                var vec = dust.getColor();
                int r = (int) (vec.x() * 255.0f);
                int g = (int) (vec.y() * 255.0f);
                int b = (int) (vec.z() * 255.0f);
                String data = "r=" + r + ", g=" + g + ", b=" + b + ", scale=" + dust.getScale();
                appendClickable(text, data + " ");
            } else if (packet.getParticle() instanceof ItemParticleOption itemStackParticleEffect) {
                String item = BuiltInRegistries.ITEM.getKey(itemStackParticleEffect.getItem().getItem()).getPath();
                appendClickable(text, "item: " + item + " ");
            }

            appendClickable(text, "x: " + packet.getX() + " ");
            appendClickable(text, "y: " + packet.getY() + " ");
            appendClickable(text, "z: " + packet.getZ() + " ");
            appendClickable(text, "xDist: " + packet.getXDist() + " ");
            appendClickable(text, "yDist: " + packet.getYDist() + " ");
            appendClickable(text, "zDist: " + packet.getZDist() + " ");
            appendClickable(text, "maxSpeed: " + packet.getMaxSpeed() + " ");
            appendClickable(text, "count: " + packet.getCount() + " ");
            appendClickable(text, "overrideLimiter: " + packet.isOverrideLimiter());

            ChatUtil.show(text);

        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//particle_log")
                .executes(ctx -> {
                    Config.INSTANCE.particleLog = !Config.INSTANCE.particleLog;
                    if (Config.INSTANCE.particleLog) {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.particle.on"));
                    } else {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.particle.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );;
    }

    private MutableComponent appendClickable(MutableComponent source, String text) {
        source.append(Component.literal(text)
                .withStyle(s -> s
                        .withClickEvent(new ClickEvent.CopyToClipboard(text))
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal(text)))
                )
        );
        return source;
    }
}
