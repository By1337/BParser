package org.by1337.bparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.text.RawMessageConvertor;
import org.by1337.bparser.util.ChatUtil;

import java.util.Objects;

public class BossBarListener {
    private BossBarData lastBossBar;

    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//bossbar_log")
                .executes(ctx -> {
                    Config.INSTANCE.bossBarLog = !Config.INSTANCE.bossBarLog;
                    if (Config.INSTANCE.bossBarLog) {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.bossbar.on"));
                    } else {
                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.bossbar.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );

        NetworkEvent.BOSS_BAR.register(packet -> {
            if (!Config.INSTANCE.bossBarLog || !Thread.currentThread().getName().contains("Netty Client IO"))
                return;
            if (lastBossBar == null) {
                lastBossBar = new BossBarData();
                lastBossBar.load(packet);
                ChatUtil.show(lastBossBar.toText());
            } else {
                BossBarData newBar = lastBossBar.merge(packet);
                if (!Objects.equals(lastBossBar, newBar)) {
                    lastBossBar.load(packet);
                    ChatUtil.show(lastBossBar.toText());
                }
            }
        });


    }

    public static class BossBarData {
        private Text name;
        private BossBar.Color color;
        private BossBar.Style overlay;

        public void load(BossBarS2CPacket packet) {
            name = or(packet.getName(), name);
            color = or(packet.getColor(), color);
            overlay = or(packet.getOverlay(), overlay);
        }

        public BossBarData merge(BossBarS2CPacket packet) {
            BossBarData data = new BossBarData();
            data.name = or(packet.getName(), name);
            data.color = or(packet.getColor(), color);
            data.overlay = or(packet.getOverlay(), overlay);
            return data;
        }

        public Text toText() {
            MutableText text = new LiteralText("[BossBar]").styled(s -> s.withColor(Formatting.RED));
            if (name != null) {
                text.append(new LiteralText(" ")).append(name);
                String raw = Text.Serializer.toJson(name);
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
            return Objects.equals(name, that.name) && color == that.color && overlay == that.overlay;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, color, overlay);
        }

        private static <T> T or(T t, T t1) {
            return t != null ? t : t1;
        }
    }


    public enum Type {
        ADD,
        REMOVE,
        UPDATE_PCT,
        UPDATE_NAME,
        UPDATE_STYLE,
        UPDATE_PROPERTIES;
        private static Type[] values = Type.values();
    }
}
