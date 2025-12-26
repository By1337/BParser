package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.text.RawMessageConvertor;
import org.by1337.bparser.util.ChatUtil;

import java.util.Objects;
import java.util.UUID;

public class BossBarListener {
    private BossBarData lastBossBar;

    public BossBarListener() {
        NetworkEvent.BOSS_BAR.register(packet -> {
            if (!Config.INSTANCE.bossBarLog )
                return;
            BossBarContent content = new BossBarContent();
            packet.accept(content);
            if (lastBossBar == null) {
                lastBossBar = new BossBarData();
                lastBossBar.load(content);
                ChatUtil.show(lastBossBar.toText());
            } else {
                BossBarData newBar = lastBossBar.merge(content);
                if (!Objects.equals(lastBossBar, newBar)) {
                    lastBossBar.load(content);
                    ChatUtil.show(lastBossBar.toText());
                }
            }
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//bossbar_log")
                .executes(ctx -> {
                    Config.INSTANCE.bossBarLog = !Config.INSTANCE.bossBarLog;
                    if (Config.INSTANCE.bossBarLog) {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.bossbar.on"));
                    } else {
                        ctx.getSource().sendFeedback(Text.translatable("lang.bparser.bossbar.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );
    }

    public static class BossBarContent implements BossBarS2CPacket.Consumer {
        private Text name;
        private BossBar.Color color;
        private BossBar.Style overlay;

        @Override
        public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
            this.name = name;
            this.color = color;
            this.overlay = style;
        }


        @Override
        public void updateName(UUID uuid, Text name) {
            this.name = name;
        }

        @Override
        public void updateStyle(UUID id, BossBar.Color color, BossBar.Style style) {
            this.color = color;
            this.overlay = style;
        }
    }

    public static class BossBarData {
        private Text name;
        private BossBar.Color color;
        private BossBar.Style overlay;

        public void load(BossBarContent packet) {
            name = or(packet.name, name);
            color = or(packet.color, color);
            overlay = or(packet.overlay, overlay);
        }

        public BossBarData merge(BossBarContent packet) {
            BossBarData data = new BossBarData();
            data.name = or(packet.name, name);
            data.color = or(packet.color, color);
            data.overlay = or(packet.overlay, overlay);
            return data;
        }

        public Text toText() {
            MutableText text = Text.literal("[BossBar] ").styled(s -> s.withColor(Formatting.RED));
            if (name != null) {
                text.append(name);
                String raw = Text.Serialization.toJsonString(name);
                ChatUtil.addCopyButton(text, RawMessageConvertor.convert(raw));
            }
            if (color != null) {
                ChatUtil.addCopyButton(text, " " + color.getName(), color.getName(), color.getTextFormat());
            }
            if (overlay != null) {
                ChatUtil.addCopyButton(text, " " + overlay.getName(), overlay.getName(), Formatting.WHITE);
            }
            return text;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            BossBarData that = (BossBarData) o;
            if (name != null) {
                if (that.name != null) {
                    String j = Text.Serialization.toJsonString(name);
                    String j1 = Text.Serialization.toJsonString(that.name);
                    if (!j.equals(j1)) return false;
                } else {
                    return false;
                }
            } else if (that.name != null) {
                return false;
            }
            return color == that.color && overlay == that.overlay;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, color, overlay);
        }

        private static <T> T or(T t, T t1) {
            return t != null ? t : t1;
        }
    }
}
