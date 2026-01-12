package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.world.BossEvent;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.text.ComponentUtil;
import org.by1337.bparser.util.ChatUtil;

import java.util.Objects;
import java.util.UUID;

public class BossBarListener {
    private BossBarData lastBossBar;

    public BossBarListener() {
        NetworkEvent.BOSS_BAR.register(packet -> {
            if (!Config.INSTANCE.bossBarLog)
                return;
            BossBarContent content = new BossBarContent();
            packet.dispatch(content);
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
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.bossbar.on"));
                    } else {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.bossbar.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );
    }

    public static class BossBarContent implements ClientboundBossEventPacket.Handler {
        private Component name;
        private BossEvent.BossBarColor color;
        private BossEvent.BossBarOverlay overlay;

        @Override
        public void add(UUID uUID, Component component, float f, BossEvent.BossBarColor bossBarColor, BossEvent.BossBarOverlay bossBarOverlay, boolean bl, boolean bl2, boolean bl3) {
            name = component;
            color = bossBarColor;
            overlay = bossBarOverlay;
        }

        @Override
        public void updateName(UUID uUID, Component component) {
            name = component;
        }

        @Override
        public void updateStyle(UUID uUID, BossEvent.BossBarColor bossBarColor, BossEvent.BossBarOverlay bossBarOverlay) {
            color = bossBarColor;
            overlay = bossBarOverlay;
        }
    }

    public static class BossBarData {
        private Component name;
        private BossEvent.BossBarColor color;
        private BossEvent.BossBarOverlay overlay;

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

        public Component toText() {
            MutableComponent text = Component.literal("[BossBar] ").withStyle(s -> s.withColor(ChatFormatting.RED));
            if (name != null) {
                text.append(name);
                ChatUtil.addCopyButton(text, ComponentUtil.convert(name));
            }
            if (color != null) {
                ChatUtil.addCopyButton(text, " " + color.getName(), color.getName(), color.getFormatting());
            }
            if (overlay != null) {
                ChatUtil.addCopyButton(text, " " + overlay.getName(), overlay.getName(), ChatFormatting.WHITE);
            }
            return text;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            BossBarData that = (BossBarData) o;
            if (name != null) {
                if (that.name != null) {
                    String j = ComponentUtil.toString(name);
                    String j1 = ComponentUtil.toString(that.name);
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
