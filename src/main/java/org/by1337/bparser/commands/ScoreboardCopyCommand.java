package org.by1337.bparser.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.*;
import org.by1337.bparser.text.ComponentUtil;
import org.by1337.bparser.util.ChatUtil;

import java.util.Comparator;

public class ScoreboardCopyCommand {
    private static final Comparator<PlayerScoreEntry> VANILLA_ORDER = Comparator.comparing(PlayerScoreEntry::value).reversed().thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);


    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//scoreboard")
                .executes(ctx -> {
                    Minecraft mc = Minecraft.getInstance();
                    Scoreboard scoreboard = mc.level.getScoreboard();
                    Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

                    if (objective != null) {
                        Component header = objective.getDisplayName();
                        MutableComponent headerText = Component.literal("Header: ").append(header);
                        ChatUtil.addCopyButton(headerText, ComponentUtil.convert(header));
                        ChatUtil.show(headerText);

                        var scores = scoreboard.listPlayerScores(objective);
                        MutableComponent all = Component.literal("");
                        all.append(header).append("\n");
                        scores
                                .stream()
                                .sorted(VANILLA_ORDER)
                                .forEach(score -> {
                                    String playerName = score.owner();
                                    PlayerTeam team = scoreboard.getPlayersTeam(playerName);
                                    Component lineText;
                                    if (team != null) {
                                        String cleaned = playerName.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
                                        if (cleaned.isEmpty()) {
                                            lineText = team.getFormattedName(Component.literal(""));
                                        } else {
                                            lineText = team.getFormattedName(Component.literal(playerName));
                                        }
                                    } else {
                                        lineText = Component.literal(playerName);
                                    }
                                    String scoreCount;
                                    if (score.value() < 10) {
                                        scoreCount = "0" + score.value() + " ";
                                    } else {
                                        scoreCount = score.value() + " ";
                                    }
                                    ChatUtil.show(
                                            ChatUtil.addCopyButton(
                                                    Component.literal(scoreCount).append(lineText),
                                                    ComponentUtil.convert(lineText)
                                            )
                                    );
                                    all.append(lineText).append("\n");
                                });
                        ChatUtil.show(
                                ChatUtil.addCopyButton(
                                        Component.literal(""),
                                        "[copy all]",
                                        ComponentUtil.convert(all)
                                )
                        );
                    }
                    return 1;
                })
        );
    }
}