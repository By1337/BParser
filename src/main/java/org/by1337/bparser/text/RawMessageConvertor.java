package org.by1337.bparser.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.by1337.bparser.cfg.Config;

public class RawMessageConvertor {

    public static String convert(String rawMessage) {
        if (Config.INSTANCE.textType == Config.TextType.LEGACY) {
            return convert(rawMessage, false);
        }
        return RawToMM.toMM(rawMessage);
    }

    public static String convert(String rawMessage, boolean noColors) {
        if (!rawMessage.startsWith("{")) return "";
        JsonObject tag = JsonParser.parseString(rawMessage).getAsJsonObject();
        return toLegacy(tag, new StringBuilder(), noColors).toString();
    }

    private static StringBuilder toLegacy(JsonElement element, StringBuilder sb, boolean noColors) {
        if (element.isJsonObject()) {
            JsonObject raw = element.getAsJsonObject();

            if (raw.has("text")) {
                String text = raw.get("text").getAsString();
                if (!text.isEmpty()) {
                    if (!noColors) {
                        if (raw.has("color")) {
                            String color = raw.get("color").getAsString();
                            for (StringFormatters value : StringFormatters.values()) {
                                if (value.isColor && value.miniMessagesSyntax.equals(color)) {
                                    sb.append("&").append(value.code);
                                    color = null;
                                    break;
                                }
                            }
                            if (color != null) {
                                sb.append("&").append(color);
                            }
                        }

                        for (StringFormatters value : StringFormatters.values()) {
                            if (value.isFormat && raw.has(value.miniMessagesSyntax)) {
                                String flag = raw.get(value.miniMessagesSyntax).getAsString();
                                if (flag.equals("true")) {
                                    sb.append("&").append(value.code);
                                }
                            }
                        }
                    }
                    sb.append(text);
                }
            }

            if (raw.has("extra")) {
                JsonArray extra = raw.getAsJsonArray("extra");
                for (JsonElement extraElement : extra) {
                    toLegacy(extraElement, sb, noColors);
                }
            }
        } else if (element.isJsonArray()) {
            for (JsonElement listElement : element.getAsJsonArray()) {
                toLegacy(listElement, sb, noColors);
            }
        }
        return sb;
    }

    private enum StringFormatters {
        COLOR_BLACK(false, '0', "black", true),
        COLOR_DARK_BLUE(false, '1', "dark_blue", true),
        COLOR_DARK_GREEN(false, '2', "dark_green", true),
        COLOR_DARK_AQUA(false, '3', "dark_aqua", true),
        COLOR_DARK_RED(false, '4', "dark_red", true),
        COLOR_DARK_PURPLE(false, '5', "dark_purple", true),
        COLOR_GOLD(false, '6', "gold", true),
        COLOR_GRAY(false, '7', "gray", true),
        COLOR_DARK_GRAY(false, '8', "dark_gray", true),
        COLOR_BLUE(false, '9', "blue", true),
        COLOR_GREEN(false, 'a', "green", true),
        COLOR_AQUA(false, 'b', "aqua", true),
        COLOR_RED(false, 'c', "red", true),
        COLOR_LIGHT_PURPLE(false, 'd', "light_purple", true),
        COLOR_YELLOW(false, 'e', "yellow", true),
        COLOR_WHITE(false, 'f', "white", true),
        HEX_COLOR(true),
        RESET(false, 'r', "reset", true),
        BOLD(true, 'l', "bold", false),
        UNDERLINE(true, 'n', "underlined", false),
        STRIKETHROUGH(true, 'm', "strikethrough", false),
        ITALIC(true, 'o', "italic", false),
        OBFUSCATED(true, 'k', "obfuscated", false);
        public final boolean isFormat;
        public final char code;
        public final String miniMessagesSyntax;
        public final boolean isColor;

        StringFormatters(boolean isFormat, char code, String miniMessagesSyntax, boolean isColor) {
            this.isFormat = isFormat;
            this.code = code;
            this.miniMessagesSyntax = miniMessagesSyntax;
            this.isColor = isColor;
        }

        StringFormatters(boolean isColor) {
            this.isColor = isColor;
            this.miniMessagesSyntax = "";
            isFormat = false;
            code = '\n';
        }
    }

}
