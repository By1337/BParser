package org.by1337.bparser.text;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ChatColor {
    private final Color color;

    public ChatColor(@NotNull Color color) {
        this.color = color;
    }

    public ChatColor(String hex) {
        hex = colorNameToHex(hex);
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        int red = Integer.parseInt(hex.substring(0, 2), 16);
        int green = Integer.parseInt(hex.substring(2, 4), 16);
        int blue = Integer.parseInt(hex.substring(4, 6), 16);
        color = new Color(red, green, blue);
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

    public int manhattanDistance(ChatColor other){
        return Math.abs(red() -  other.red()) + Math.abs(green() - other.green()) + Math.abs(blue() - other.blue());
    }

    public String toHex() {
        return toHex(color);
    }

    @NotNull
    public Color getColor() {
        return color;
    }

    public static String toHex(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    public int red() {
        return color.getRed();
    }

    public int green() {
        return color.getGreen();
    }

    public int blue() {
        return color.getBlue();
    }

    public int rgb() {
        return color.getRGB();
    }
}
