package org.by1337.bparser.cfg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;
import org.by1337.bparser.BParser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Config {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("BParser.json");
    public static final Config INSTANCE = load();

    public TextType textType = TextType.MM;
    public ChatConfig chat = new ChatConfig();
    public boolean cooldownLog;
    public boolean particleLog;
    public boolean soundLog;
    public boolean menuCopy = true;
    public boolean velocityLog;
    public boolean effectLog;
    public boolean titleLog;
    public boolean bossBarLog;


    private static Config load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                return GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                BParser.LOGGER.error("Could not load config file!", e);
            }
        }
        Config c = new Config();
        c.save();
        return c;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            BParser.LOGGER.error("Could not save config file!", e);
        }
    }


    public enum TextType {
        @SerializedName("legacy")
        LEGACY,
        @SerializedName("mm")
        MM,
    }

    public static class ChatConfig {
        public boolean clickToCopy = true;
        public boolean asRaw = false;
        public boolean gradients = true;
    }
}
