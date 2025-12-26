package org.by1337.bparser.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.by1337.bparser.mixin.PlayerListHudAccessor;
import org.by1337.bparser.text.RawMessageConvertor;
import org.by1337.bparser.util.ChatUtil;

public class TabCopyCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//tab")
                .executes(ctx -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    PlayerListHudAccessor tab = (PlayerListHudAccessor) client.inGameHud.getPlayerListHud();

                    MutableText text = tab.getHeader().copy().append(Text.literal("\n")).append(tab.getFooter());
                    ChatUtil.show(
                            ChatUtil.addCopyButton(
                                    text,
                                    "[copy]",
                                    RawMessageConvertor.convert(Text.Serialization.toJsonString(text)).replace("<br>", "\n")
                            )
                    );
                    return 1;
                })
        );
    }
}
