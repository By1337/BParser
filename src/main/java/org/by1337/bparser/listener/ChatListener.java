package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.by1337.bparser.BParser;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.GameMessageS2CPacketAccessor;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.text.RawMessageConvertor;
import org.by1337.bparser.util.ChatUtil;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class ChatListener {
    public ChatListener() {
        NetworkEvent.CHAT_EVENT.register(packet -> {
            if (!Config.INSTANCE.chat.clickToCopy)
                return;
            if (packet.content().getContent() instanceof TextContent) {
                Text msg = packet.content();
                MutableText literalText = Text.literal("").append(msg);

                String raw = Text.Serialization.toJsonString(packet.content());
                String result = RawMessageConvertor.convert(raw);
                ChatUtil.addCopyButton(literalText, " [copy]", result);
                if (Config.INSTANCE.chat.asRaw) {
                    ChatUtil.addCopyButton(literalText, "[raw]", raw);
                }
                ((GameMessageS2CPacketAccessor) (Object) packet).setMessage(literalText);
            }
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//chat_click_to_copy")
                .executes(ctx -> {
                    Config.INSTANCE.chat.clickToCopy = !Config.INSTANCE.chat.clickToCopy;
                    if (Config.INSTANCE.chat.clickToCopy) {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.chat.on"));
                    } else {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.chat.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("raw")
                        .executes(ctx -> {
                            Config.INSTANCE.chat.asRaw = !Config.INSTANCE.chat.asRaw;
                            if (Config.INSTANCE.chat.asRaw) {
                                ctx.getSource().sendFeedback(Text.translatable("lang.bparser.chat.raw.on"));
                            } else {
                                ctx.getSource().sendFeedback(Text.translatable("lang.bparser.chat.raw.off"));
                            }
                            Config.INSTANCE.save();
                            return 1;
                        })
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("gradients")
                        .executes(ctx -> {
                            Config.INSTANCE.chat.gradients = !Config.INSTANCE.chat.gradients;
                            if (Config.INSTANCE.chat.gradients) {
                                ctx.getSource().sendFeedback(Text.translatable("lang.bparser.chat.gradients.on"));
                            } else {
                                ctx.getSource().sendFeedback(Text.translatable("lang.bparser.chat.gradients.off"));
                            }
                            Config.INSTANCE.save();
                            return 1;
                        })
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("saveType")
                        .then(argument("type", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (Config.TextType value : Config.TextType.values()) {
                                        builder.suggest(value.name().toLowerCase());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String input = StringArgumentType.getString(ctx, "type").toLowerCase();
                                    try {
                                        Config.INSTANCE.textType = Config.TextType.valueOf(input.toUpperCase());
                                        Config.INSTANCE.save();
                                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.chat.type", input));
                                    } catch (IllegalArgumentException e) {
                                        ctx.getSource().sendError(Text.translatable("lang.bparser.chat.type.error", input));
                                        return 0;
                                    }
                                    return 1;
                                })
                        )
                )
        );
    }
}
