package org.by1337.bparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;

import java.util.UUID;

public class ParticleListener {

    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//particle_log")
                .executes(ctx -> {
                    Config.INSTANCE.particleLog = !Config.INSTANCE.particleLog;
                    if (Config.INSTANCE.particleLog) {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.particle.on"));
                    } else {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.particle.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );

        NetworkEvent.PARTICLE.register(packet -> {
            if (!Config.INSTANCE.particleLog || !Thread.currentThread().getName().contains("Netty Client IO")) return;
            LiteralText text = new LiteralText("[particle]: ");
            String type = Registry.PARTICLE_TYPE.getId(packet.getParameters().getType()).toString();

            appendClickable(text, "type: " + type + " ");
            if (packet.getParameters() instanceof BlockStateParticleEffect) {
                BlockStateParticleEffect block = (BlockStateParticleEffect) packet.getParameters();
                String b = Registry.BLOCK.getId(block.getBlockState().getBlock()).toString();
                appendClickable(text, "dust-param: " + b + " ");
            } else if (packet.getParameters() instanceof DustParticleEffect) {
                DustParticleEffect dust = (DustParticleEffect) packet.getParameters();
                String data = "r=" + dust.getRed() + ", g=" + dust.getGreen() + ", b=" + dust.getBlue() + ", scale=" + dust.getScale();
                appendClickable(text, data + " ");
            } else if (packet.getParameters() instanceof ItemStackParticleEffect) {
                ItemStackParticleEffect itemStackParticleEffect = (ItemStackParticleEffect) packet.getParameters();
                String item = Registry.ITEM.getId(itemStackParticleEffect.getItemStack().getItem()).toString();
                appendClickable(text, "item: " + item + " ");
            }

            appendClickable(text, "x: " + packet.getX() + " ");
            appendClickable(text, "y: " + packet.getY() + " ");
            appendClickable(text, "z: " + packet.getZ() + " ");
            appendClickable(text, "offsetX: " + packet.getOffsetX() + " ");
            appendClickable(text, "offsetY: " + packet.getOffsetY() + " ");
            appendClickable(text, "offsetZ: " + packet.getOffsetZ() + " ");
            appendClickable(text, "speed: " + packet.getSpeed() + " ");
            appendClickable(text, "count: " + packet.getCount() + " ");
            appendClickable(text, "longDistance: " + packet.isLongDistance());

            MinecraftClient.getInstance().inGameHud.addChatMessage(net.minecraft.network.MessageType.CHAT, text, UUID.randomUUID());

        });
    }

    private LiteralText appendClickable(LiteralText source, String text) {
        source.append(new LiteralText(text)
                .styled(s -> s
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(text)))
                )
        );
        return source;
    }
}
