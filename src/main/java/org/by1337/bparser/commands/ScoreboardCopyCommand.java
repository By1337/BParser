package org.by1337.bparser.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.by1337.bparser.text.RawMessageConvertor;
import org.by1337.bparser.util.ChatUtil;

import java.util.Collection;

public class ScoreboardCopyCommand {

    public static void register(){
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//scoreboard")
                .executes(ctx -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    Scoreboard scoreboard = client.world.getScoreboard();

                    ScoreboardObjective objective = scoreboard.getObjectiveForSlot(1);
                    if (objective != null) {
                        Text header = objective.getDisplayName();
                        MutableText headerText = new LiteralText("Header: ").append(header);
                        ChatUtil.addCopyButton(headerText, RawMessageConvertor.convert(Text.Serializer.toJson(header)));
                        ChatUtil.show(headerText);

                        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(objective);
                        LiteralText all = new LiteralText("");
                        all.append(header).append("\n");
                        scores
                                .stream()
                                .sorted(ScoreboardPlayerScore.COMPARATOR.reversed())
                                .forEach(score -> {
                                    String playerName = score.getPlayerName();
                                    Team team = scoreboard.getPlayerTeam(playerName);

                                    Text lineText;
                                    if (team != null) {
                                        String cleaned = playerName.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
                                        if (cleaned.isEmpty()) {
                                            lineText = team.decorateName(new LiteralText(""));
                                        } else {
                                            lineText = team.decorateName(new LiteralText(playerName));
                                        }
                                    } else {
                                        lineText = new LiteralText(playerName);
                                    }

                                    String scoreCount;
                                    if (score.getScore() < 10) {
                                        scoreCount = "0" + score.getScore() + " ";
                                    } else {
                                        scoreCount = score.getScore() + " ";
                                    }

                                    ChatUtil.show(
                                            ChatUtil.addCopyButton(
                                                    new LiteralText(scoreCount).append(lineText),
                                                    RawMessageConvertor.convert(Text.Serializer.toJson(lineText))
                                            )
                                    );
                                    all.append(lineText).append("\n");
                                });
                        ChatUtil.show(
                                ChatUtil.addCopyButton(
                                        new LiteralText(""),
                                        "[copy all]",
                                        RawMessageConvertor.convert(Text.Serializer.toJson(all)).replace("<br>", "\n")
                                )
                        );
                    }


                    return 1;
                })
        );
    }
}
