package org.by1337.bparser.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.by1337.bparser.BParser;
import org.by1337.bparser.text.RawMessageConvertor;
import org.by1337.bparser.util.ChatUtil;

import java.util.Collection;
import java.util.Comparator;

public class ScoreboardCopyCommand {
    private static final Comparator<ScoreboardEntry> VANILLA_ORDER = Comparator.comparing(ScoreboardEntry::value).reversed().thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER);


    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//scoreboard")
                .executes(ctx -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    Scoreboard scoreboard = client.world.getScoreboard();

                    ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);

                    if (objective != null) {
                        Text header = objective.getDisplayName();
                        MutableText headerText = Text.literal("Header: ").append(header);
                        ChatUtil.addCopyButton(headerText, RawMessageConvertor.convert(Text.Serialization.toJsonString(header)));
                        ChatUtil.show(headerText);

                        Collection<ScoreboardEntry> scores = scoreboard.getScoreboardEntries(objective);
                        MutableText all = Text.literal("");
                        all.append(header).append("\n");
                        scores
                                .stream()
                                .sorted(VANILLA_ORDER)
                                .forEach(score -> {
                                    BParser.LOGGER.info(score);
                                    String playerName = score.owner();
                                    Team team = scoreboard.getScoreHolderTeam(playerName);

                                    Text lineText;
                                    if (team != null) {
                                        String cleaned = playerName.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
                                        if (cleaned.isEmpty()) {
                                            lineText = team.decorateName(Text.literal(""));
                                        } else {
                                            lineText = team.decorateName(Text.literal(playerName));
                                        }
                                    } else {
                                        lineText = Text.literal(playerName);
                                    }

                                    String scoreCount;
                                    if (score.value() < 10) {
                                        scoreCount = "0" + score.value() + " ";
                                    } else {
                                        scoreCount = score.value() + " ";
                                    }

                                    ChatUtil.show(
                                            ChatUtil.addCopyButton(
                                                    Text.literal(scoreCount).append(lineText),
                                                    RawMessageConvertor.convert(Text.Serialization.toJsonString(lineText))
                                            )
                                    );
                                    all.append(lineText).append("\n");
                                });
                        ChatUtil.show(
                                ChatUtil.addCopyButton(
                                        Text.literal(""),
                                        "[copy all]",
                                        RawMessageConvertor.convert(Text.Serialization.toJsonString(all)).replace("<br>", "\n")
                                )
                        );
                    }


                    return 1;
                })
        );
    }
}
