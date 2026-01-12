package org.by1337.bparser.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.by1337.bparser.cfg.Config;


public class ComponentUtil {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static JsonElement toJson(Component component) {
        return ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, component).getOrThrow();
    }

    public static String toString(Component component) {
        return GSON.toJson(toJson(component));
    }

    public static String convert(Component component) {
        return convert(component, false);
    }
    public static String convert(Component component, boolean noColors) {
        if (Config.INSTANCE.textType == Config.TextType.LEGACY || noColors) {
            return RawMessageConvertor.convert(component, noColors);
        }
        return RawToMM.toMM(component);
    }
}
