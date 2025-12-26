package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.util.ChatUtil;

import java.util.UUID;

public class ParticleListener {
    public ParticleListener() {
        NetworkEvent.PARTICLE.register(packet -> {
            if (!Config.INSTANCE.cooldownLog ) return;
            MutableText text = Text.literal("[particle]: ");
            String type = Registries.PARTICLE_TYPE.getId(packet.getParameters().getType()).toString();

            appendClickable(text, "type: " + type + " ");
            if (packet.getParameters() instanceof BlockStateParticleEffect block) {
                String b = Registries.BLOCK.getId(block.getBlockState().getBlock()).toString();
                appendClickable(text, "dust-param: " + b + " ");
            } else if (packet.getParameters() instanceof DustParticleEffect dust) {
                var vec = dust.getColor();
                int r = (int) (vec.x() * 255.0f);
                int g = (int) (vec.y() * 255.0f);
                int b = (int) (vec.z() * 255.0f);
                String data = "r=" + r + ", g=" + g + ", b=" + b + ", scale=" + dust.getScale();
                appendClickable(text, data + " ");
            } else if (packet.getParameters() instanceof ItemStackParticleEffect itemStackParticleEffect) {
                String item = Registries.ITEM.getId(itemStackParticleEffect.getItemStack().getItem()).toString();
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

            ChatUtil.show(text);

        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//particle_log")
                .executes(ctx -> {
                    Config.INSTANCE.particleLog = !Config.INSTANCE.cooldownLog;
                    if (Config.INSTANCE.cooldownLog) {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.particle.on"));
                    } else {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.particle.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );;
    }

    private MutableText appendClickable(MutableText source, String text) {
        source.append(Text.literal(text)
                .styled(s -> s
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(text)))
                )
        );
        return source;
    }
}
