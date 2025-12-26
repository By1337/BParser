package org.by1337.bparser.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.by1337.bparser.cfg.Config;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RawToMM {

    public static String toMM(String raw) {
        StringBuilder out = new StringBuilder();
        if (!raw.startsWith("{")) return "";
        JsonObject tag = JsonParser.parseString(raw).getAsJsonObject();
        toMM(tag, out, new TextDecorator(out));
        return out.toString();
    }


    private static void toMM(JsonObject raw, StringBuilder out, TextDecorator decorator) {
        String text = raw.has("text") ? raw.get("text").getAsString() : "";
        if (text.length() == 1) {
            if (raw.has("color")) {
                String color = raw.get("color").getAsString();
                raw.addProperty("color", colorNameToHex(color));
            }
        }
        int index = out.length();
        decorator.accept(raw);

        if (raw.has("clickEvent")) {
            JsonObject clickEvent = raw.getAsJsonObject("clickEvent");
            String action = clickEvent.get("action").getAsString();
            String value = clickEvent.get("value").getAsString();
            out.append("<click:").append(action).append(":'").append(value).append("'>");
        }

        if (raw.has("hoverEvent")) {
            JsonObject hoverEvent = raw.getAsJsonObject("hoverEvent");
            String action = hoverEvent.get("action").getAsString();
            out.append("<hover:").append(action).append(":'");

            JsonElement contents = hoverEvent.get("contents");
            if (contents.isJsonObject()) {
                toMM(contents.getAsJsonObject(), out, new TextDecorator(out));
            } else {
                out.append(contents.getAsString());
            }
            out.append("'>");
        }

        if (text.isEmpty() && !raw.has("hoverEvent") && !raw.has("clickEvent")&& !raw.has("extra")){
            out.setLength(index);
            return;
        }
        out.append(text.replace("\n", "<br>"));
        Decoration stackColor = decorator.currentColor.get();
        if (stackColor != null && stackColor.asString().contains("gradient")) {
            out.append("</gradient>");
            decorator.currentColor.set(null);
        }

        if (raw.has("hoverEvent")) {
            out.append("</hover>");
            decorator.clearStack();
        }
        if (raw.has("clickEvent")) {
            out.append("</click>");
            decorator.clearStack();
        }

        if (raw.has("extra")) {
            if (Config.INSTANCE.chat.gradients) {
                JsonArray extra = raw.getAsJsonArray("extra");
                raw.add("extra", findGradients(extra));
            }
            JsonArray extra = raw.getAsJsonArray("extra");
            for (JsonElement elem : extra) {
                if (elem.isJsonObject()) {
                    toMM(elem.getAsJsonObject(), out, decorator.overlap());
                } else {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("text", elem.getAsString());
                    toMM(obj, out, decorator.overlap());
                }
            }
        }
    }

    private static String colorNameToHex(String name) {
        return switch (name) {
            case "black" -> "#000000";
            case "dark_blue" -> "#0000AA";
            case "dark_green" -> "#00AA00";
            case "dark_aqua" -> "#00AAAA";
            case "dark_red" -> "#AA0000";
            case "dark_purple" -> "#AA00AA";
            case "gold" -> "#FFAA00";
            case "gray" -> "#AAAAAA";
            case "dark_gray" -> "#555555";
            case "blue" -> "#5555FF";
            case "green" -> "#55FF55";
            case "aqua" -> "#55FFFF";
            case "red" -> "#FF5555";
            case "light_purple" -> "#FF55FF";
            case "yellow" -> "#FFFF55";
            case "white" -> "#FFFFFF";
            default -> name;
        };
    }
    private static String colorHexToName(String name) {
        return switch (name) {
            case "#000000" -> "black";
            case "#0000AA" -> "dark_blue";
            case "#00AA00" -> "dark_green";
            case "#00AAAA" -> "dark_aqua";
            case "#AA0000" -> "dark_red";
            case "#AA00AA" -> "dark_purple";
            case "#FFAA00" -> "gold";
            case "#AAAAAA" -> "gray";
            case "#555555" -> "dark_gray";
            case "#5555FF" -> "blue";
            case "#55FF55" -> "green";
            case "#55FFFF" -> "aqua";
            case "#FF5555" -> "red";
            case "#FF55FF" -> "light_purple";
            case "#FFFF55" -> "yellow";
            case "#FFFFFF" -> "white";
            default -> name;
        };
    }


    private static class TextDecorator {
        public final StringBuilder out;
        public final Map<TextDecoration, Decoration> current;
        public final LinkedList<Decoration> stack;
        public final AtomicReference<Decoration> currentColor;

        private TextDecorator(StringBuilder out) {
            this.out = out;
            current = new HashMap<>();
            stack = new LinkedList<>();
            currentColor = new AtomicReference<>();
        }

        public TextDecorator(StringBuilder out, Map<TextDecoration, Decoration> current, LinkedList<Decoration> stack, AtomicReference<Decoration> currentColor) {
            this.out = out;
            this.current = current;
            this.stack = stack;
            this.currentColor = currentColor;
        }

        public TextDecorator overlap() {
            return new TextDecorator(out, new HashMap<>(current), stack, currentColor);
        }

        public void clearStack() {
            stack.clear();
        }

        public void accept(JsonObject raw) {
            if (raw.has("color")) {
                String color = raw.get("color").getAsString();
                current.put(TextDecoration.COLOR, new Decoration(color, TextDecoration.COLOR));
            }
            for (TextDecoration format : TextDecoration.values()) {
                if (!format.isFormat) continue;
                if (!raw.has(format.vanillaName)) continue; // наследуем
                Decoration decoration = new Decoration(raw.get(format.vanillaName).getAsBoolean(), format);
                current.put(format, decoration);
            }
            boolean closeAll = true;
            for (Decoration value : current.values()) {
                if (value.token.isFormat && value.asBool()) {
                    closeAll = false;
                    break;
                }
            }
            if (closeAll && stack.size() > 1) {
                out.append("<reset>");
                stack.clear();
                currentColor.set(null);
            } else {
                closeAll = false;
            }
            List<Decoration> closed = new ArrayList<>();
            List<Decoration> opened = new ArrayList<>();
            current.forEach((key, value) -> {
                if (key.isFormat) {
                    if (value.asBool()) {
                        opened.add(value);
                    } else {
                        closed.add(value);
                    }
                }
            });

            for (Decoration decoration : closed) {
                ListIterator<Decoration> it = stack.listIterator(stack.size());
                if (stack.stream().noneMatch(p -> p.token == decoration.token)) {
                    continue;
                }
                while (it.hasPrevious()) {
                    Decoration dec = it.previous();
                    out.append("</").append(dec.token.shortName).append(">");
                    it.remove();
                    if (dec.token == decoration.token) {
                        break;
                    }
                }
            }
            loop:
            for (Decoration decoration : opened) {
                for (Decoration decoration1 : stack) {
                    if (decoration.token == decoration1.token) {
                        continue loop;
                    }
                }
                out.append("<").append(decoration.token.shortName).append(">");
                stack.push(decoration);
            }

            Decoration color = current.get(TextDecoration.COLOR);
            Decoration stackColor = currentColor.get();
            if (color == null && stackColor != null && !isWhite(stackColor.asString())) {
                if (!closeAll) { // если нет токена <reset>, то красим в белый
                    out.append("<white>");
                    currentColor.set(new Decoration("white", TextDecoration.COLOR));
                }
            }
            if (color != null && !color.isEqualsColors(stackColor)) {
                out.append("<").append(colorHexToName(color.asString())).append(">");
                currentColor.set(color);
            }
        }
    }
    private static boolean isWhite(String text) {
        return text.equals("white") || text.equals("#FFFFFF");
    }

    private static class Decoration {
        public final Object data;
        public final TextDecoration token;

        public Decoration(Object data, TextDecoration token) {
            this.data = data;
            this.token = token;
        }

        public boolean asBool() {
            return (boolean) data;
        }

        public String asString() {
            return String.valueOf(data);
        }
        public boolean isEqualsColors(Decoration other){
            if (other == null) return false;
            if (other.token != token) return false;
            return colorNameToHex(asString()).equals(colorNameToHex(other.asString()));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Decoration that = (Decoration) o;
            return Objects.equals(data, that.data) && token == that.token;
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, token);
        }
    }

    private enum TextDecoration {
        COLOR_BLACK(false, "black", true),
        COLOR_DARK_BLUE(false, "dark_blue", true),
        COLOR_DARK_GREEN(false, "dark_green", true),
        COLOR_DARK_AQUA(false, "dark_aqua", true),
        COLOR_DARK_RED(false, "dark_red", true),
        COLOR_DARK_PURPLE(false, "dark_purple", true),
        COLOR_GOLD(false, "gold", true),
        COLOR_GRAY(false, "gray", true),
        COLOR_DARK_GRAY(false, "dark_gray", true),
        COLOR_BLUE(false, "blue", true),
        COLOR_GREEN(false, "green", true),
        COLOR_AQUA(false, "aqua", true),
        COLOR_RED(false, "red", true),
        COLOR_LIGHT_PURPLE(false, "light_purple", true),
        COLOR_YELLOW(false, "yellow", true),
        COLOR_WHITE(false, "white", true),

        COLOR(false, "", true),

        RESET(false, "reset", "reset", true),
        BOLD(true, "b", "bold", false),
        UNDERLINE(true, "u", "underlined", false),
        STRIKETHROUGH(true, "st", "strikethrough", false),
        ITALIC(true, "em", "italic", false),
        OBFUSCATED(true, "obf", "obfuscated", false);
        public final boolean isFormat;
        public final String shortName;
        public final String vanillaName;
        public final boolean isColor;

        TextDecoration(boolean isFormat, String vanillaName, boolean isColor) {
            this(isFormat, vanillaName, vanillaName, isColor);
        }

        TextDecoration(boolean isFormat, String shortName, String vanillaName, boolean isColor) {
            this.isFormat = isFormat;
            this.shortName = shortName;
            this.vanillaName = vanillaName;
            this.isColor = isColor;
        }
    }

    public static JsonArray findGradients(JsonArray extra) {
        List<JsonObject> components = new ArrayList<>();
        for (JsonElement jsonElement : extra) {
            if (jsonElement.isJsonObject()) {
                components.add(jsonElement.getAsJsonObject());
            }
        }
        List<List<JsonObject>> flat = new ArrayList<>();

        List<JsonObject> flush = new ArrayList<>();
        JsonObject lastData = null;
        for (JsonObject component : components) {
            JsonObject property = component.deepCopy();
            property.remove("color");
            property.remove("text");
            if (lastData == null) {
                lastData = property;
                flush.add(component);
            } else {
                if (lastData.equals(property)) {
                    flush.add(component);
                } else {
                    flat.add(flush);
                    flush = new ArrayList<>();
                    lastData = property;
                    flush.add(component);
                }
            }
        }
        flat.add(flush);

        JsonArray result = new JsonArray();
        for (List<JsonObject> list : flat) {
            if (list.isEmpty()) continue;
            if (list.size() < 2) {
                list.forEach(result::add);
            } else {
                GradientBuilder gradient = new GradientBuilder();
                for (JsonObject component : list) {
                    String text = component.getAsJsonPrimitive("text").getAsString();
                    if (text.replace(" ", "").length() > 1) {
                        gradient.fallback(result);
                        result.add(component);
                    } else {
                        if (!gradient.tryAdd(component)) {
                            gradient.fallback(result);
                            result.add(component);
                        }
                    }
                }
                JsonObject o = gradient.build();
                if (o != null) {
                    result.add(o);
                } else {
                    gradient.valid.forEach(result::add);
                }
                gradient.reset();
            }
        }
        return result;
    }

    private static class GradientBuilder {
        private final List<JsonObject> valid = new ArrayList<>();
        private final List<ChatColor> colors = new ArrayList<>();
        private ChatColor lastColor = null;
        private int lastDistance = -1;
        private final StringBuilder buffer = new StringBuilder();

        public boolean tryAdd(JsonObject component) {
            if (!component.has("color")) return false;
            ChatColor color = new ChatColor(component.getAsJsonPrimitive("color").getAsString());
            String text = component.getAsJsonPrimitive("text").getAsString();
            if (text.replace(" ", "").length() > 1) return false;
            if (lastColor != null) {
                int dist = color.manhattanDistance(lastColor);
                colors.add(color);
                String data = GradientCompressor.toMiniMessage(colors);
                colors.remove(colors.size() - 1);
                if (data == null) return false;
                lastDistance = dist;
            }
            lastColor = color;
            valid.add(component);
            colors.add(color);
            buffer.append(text);
            return true;
        }

        public void fallback(JsonArray out) {
            JsonObject o = build();
            if (o != null) {
                out.add(o);
            } else {
                valid.forEach(out::add);
            }
            reset();
        }

        public void reset() {
            valid.clear();
            colors.clear();
            lastColor = null;
            lastDistance = -1;
            buffer.setLength(0);
        }

        public @Nullable JsonObject build() {
            if (valid.size() <= 2) return null;
            String data = GradientCompressor.toMiniMessage(colors);
            if (data == null) return null;
            JsonObject base = valid.get(0).deepCopy();
            base.addProperty("text", buffer.toString());
            base.addProperty("color", data);
            return base;
        }
    }

    public static class GradientCompressor {

        private static final int TOLERANCE = 12;

        public static String toMiniMessage(List<ChatColor> colors) {
            List<ChatColor> controlPoints = new ArrayList<>();
            controlPoints.add(colors.get(0));

            int n = colors.size() - 1;

            for (int i = 1; i < n; i++) {
                ChatColor expected = lerp(colors.get(0), colors.get(n), (float) i / n);
                if (!closeEnough(colors.get(i), expected)) {
                    controlPoints.add(colors.get(i));
                }
            }

            controlPoints.add(colors.get(n));
            if (controlPoints.size() > 2) {
                return null;
            }
            StringBuilder res = new StringBuilder("gradient:");
            for (ChatColor controlPoint : controlPoints) {
                res.append(controlPoint.toHex()).append(":");
            }
            res.setLength(res.length() - 1);
            return res.toString();
        }

        private static boolean closeEnough(ChatColor a, ChatColor b) {
            return Math.abs(a.red() - b.red()) <= TOLERANCE &&
                    Math.abs(a.green() - b.green()) <= TOLERANCE &&
                    Math.abs(a.blue() - b.blue()) <= TOLERANCE;
        }

        private static ChatColor lerp(ChatColor c0, ChatColor c1, float t) {
            int r = (int) (c0.red() + (c1.red() - c0.red()) * t);
            int g = (int) (c0.green() + (c1.green() - c0.green()) * t);
            int b = (int) (c0.blue() + (c1.blue() - c0.blue()) * t);
            return new ChatColor(new Color(r, g, b));
        }

        public static class Data {
            public final String first;
            public final String second;

            public Data(String first, String second) {
                this.first = first;
                this.second = second;
            }
        }
    }
}
