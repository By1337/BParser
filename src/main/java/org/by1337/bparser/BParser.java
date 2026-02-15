package org.by1337.bparser;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.by1337.bparser.commands.ScoreboardCopyCommand;
import org.by1337.bparser.commands.TabCopyCommand;
import org.by1337.bparser.listener.*;
import org.by1337.bparser.schem.SchemSelector;

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
            ScoreboardCopyCommand.register(dispatcher);
            TabCopyCommand.register(dispatcher);
        });

    }
}