package org.by1337.bparser.text;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ChatColor {
    private Color color;

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
        switch (name) {
            case "black":
                return "#000000";
            case "dark_blue":
                return "#0000AA";
            case "dark_green":
                return "#00AA00";
            case "dark_aqua":
                return "#00AAAA";
            case "dark_red":
                return "#AA0000";
            case "dark_purple":
                return "#AA00AA";
            case "gold":
                return "#FFAA00";
            case "gray":
                return "#AAAAAA";
            case "dark_gray":
                return "#555555";
            case "blue":
                return "#5555FF";
            case "green":
                return "#55FF55";
            case "aqua":
                return "#55FFFF";
            case "red":
                return "#FF5555";
            case "light_purple":
                return "#FF55FF";
            case "yellow":
                return "#FFFF55";
            case "white":
                return "#FFFFFF";
        }
        ;
        return name;
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
