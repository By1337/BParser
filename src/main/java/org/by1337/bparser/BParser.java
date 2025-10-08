package org.by1337.bparser;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.by1337.bparser.commands.ScoreboardCopyCommand;
import org.by1337.bparser.commands.TabCopyCommand;
import org.by1337.bparser.listener.*;
import org.by1337.bparser.schem.SchemSelector;

public class BParser implements ClientModInitializer {

    public static final String MOD_ID = "bparser";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        new ScreenListener().register();
        new ChatListener().register();
        new SoundListener().register();
        new ParticleListener().register();
        new CooldownListener().register();
        new SchemSelector().register();
        new VelocityListener().register();
        new EffectListener().register();
        new TitleListener().register();
        new BossBarListener().register();
        ScoreboardCopyCommand.register();
        TabCopyCommand.register();
    }
}