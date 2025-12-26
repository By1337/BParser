package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.text.RawMessageConvertor;
import org.by1337.bparser.util.ChatUtil;

public class TitleListener {
    public TitleListener() {
        NetworkEvent.TITLE.register(packet -> {
            if (!Config.INSTANCE.titleLog )
                return;

            MutableText text = Text.literal("[TITLE]");

            String raw = Text.Serialization.toJsonString(packet.getTitle());
            ChatUtil.addCopyButton(text, " [copy]",
                    RawMessageConvertor.convert(raw));
            ChatUtil.show(text);
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//title_log")
                .executes(ctx -> {
                    Config.INSTANCE.titleLog = !Config.INSTANCE.titleLog;
                    if (Config.INSTANCE.titleLog) {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.title.on"));
                    } else {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.title.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );
    }

}
