package org.by1337.bparser;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.by1337.bparser.commands.ScoreboardCopyCommand;
import org.by1337.bparser.commands.TabCopyCommand;
import org.by1337.bparser.listener.*;
import org.by1337.bparser.render.RenderUtil;
import org.by1337.bparser.schem.SchemSelector;
import org.by1337.bparser.util.ChatUtil;

public class BParser implements ClientModInitializer {
    public static final String MOD_ID = "bparser";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private ChatListener chatListener;
    private ScreenListener screenListener;
    private SoundListener soundListener;
    private ParticleListener particleListener;
    private CooldownListener cooldownListener;
    private SchemSelector schemSelector;
    private VelocityListener velocityListener;
    private EffectListener effectListener;
    private TitleListener titleListener;
    private BossBarListener bossBarListener;
    private ParticleRecorder particleRecorder;

    @Override
    public void onInitializeClient() {
        screenListener = new ScreenListener();
        chatListener = new ChatListener();
        soundListener = new SoundListener();
        particleListener = new ParticleListener();
        cooldownListener = new CooldownListener();
        schemSelector = new SchemSelector();
        velocityListener = new VelocityListener();
        effectListener = new EffectListener();
        titleListener = new TitleListener();
        bossBarListener = new BossBarListener();
        particleRecorder = new ParticleRecorder();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            screenListener.register(dispatcher);
            chatListener.register(dispatcher);
            soundListener.register(dispatcher);
            particleListener.register(dispatcher);
            cooldownListener.register(dispatcher);
            schemSelector.register(dispatcher);
            velocityListener.register(dispatcher);
            effectListener.register(dispatcher);
            titleListener.register(dispatcher);
            bossBarListener.register(dispatcher);
            particleRecorder.register(dispatcher);
            ScoreboardCopyCommand.register(dispatcher);
            TabCopyCommand.register(dispatcher);

            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//distance")
                    .executes(ctx -> {
                        LocalPlayer localPlayer = Minecraft.getInstance().player;
                        for (AbstractClientPlayer player : Minecraft.getInstance().level.players()) {
                            if (player == localPlayer) continue;
                            var distanceTo = localPlayer.position().distanceTo(player.position());
                            var distanceToSqr = localPlayer.position().distanceToSqr(player.position());
                            ChatUtil.show(Component.literal(localPlayer.getName() + " -> " + player.getName() + " = " + distanceTo + "  sqr" + distanceToSqr));
                        }
                        return 1;
                    })
            );
        });
    }
}