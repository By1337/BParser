package org.by1337.bparser.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.by1337.bparser.mixin.PlayerTabOverlayAccessor;
import org.by1337.bparser.text.ComponentUtil;
import org.by1337.bparser.util.ChatUtil;

import java.util.Objects;

public class TabCopyCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//tab")
                .executes(ctx -> {
                    Minecraft client = Minecraft.getInstance();

                    var tab = (PlayerTabOverlayAccessor) client.gui.getTabList();

                    MutableComponent text = Component.literal("")
                            .append(Objects.requireNonNullElse(tab.getHeader(), Component.empty()))
                            .append(Component.literal("\n"))
                            .append(Objects.requireNonNullElse(tab.getFooter(), Component.empty()));

                    ChatUtil.show(
                            ChatUtil.addCopyButton(
                                    text,
                                    "[copy]",
                                    ComponentUtil.convert(text)
                            )
                    );
                    return 1;
                })
        );
    }
}
