package cc.carm.lib.easyplugin.utils;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorParser {

    public static final Pattern HEX_PATTERN = Pattern.compile("&\\(&?#([\\da-fA-F]{6})\\)");

    public static String parse(String text) {
        return parseBaseColor(parseHexColor(text));
    }

    public static String[] parse(String... texts) {
        return parse(Arrays.asList(texts)).toArray(new String[0]);
    }

    public static List<String> parse(List<String> texts) {
        return texts.stream().map(ColorParser::parse).collect(Collectors.toList());
    }

    public static String parseBaseColor(final String text) {
        return text.replaceAll("&", "§").replace("§§", "&");
    }

    public static String parseHexColor(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            text = matcher.replaceFirst(buildHexColor(matcher.group(1)).toLowerCase());
            matcher.reset(text);
        }
        return text;
    }

    private static String buildHexColor(String hexCode) {
        return Arrays.stream(hexCode.split(""))
                .map(s -> '§' + s)
                .collect(Collectors.joining("", '§' + "x", ""));
    }


    private static final String RAW_GRADIENT_HEX_REGEX = "<\\$#[A-Fa-f0-9]{6}>";

    public static String gradient(String legacyMsg) {
        List<String> hexes = new ArrayList<>();
        Matcher matcher = Pattern.compile(RAW_GRADIENT_HEX_REGEX).matcher(legacyMsg);
        while (matcher.find()) {
            hexes.add(matcher.group().replace("<$", "").replace(">", ""));
        }
        int hexIndex = 0;
        List<String> texts = new LinkedList<>(Arrays.asList(legacyMsg.split(RAW_GRADIENT_HEX_REGEX)));
        StringBuilder finalMsg = new StringBuilder();
        for (String text : texts) {
            if (texts.get(0).equalsIgnoreCase(text)) {
                finalMsg.append(text);
                continue;
            }
            if (text.length() == 0) continue;
            if (hexIndex + 1 >= hexes.size()) {
                if (!finalMsg.toString().contains(text)) finalMsg.append(text);
                continue;
            }
            String fromHex = hexes.get(hexIndex);
            String toHex = hexes.get(hexIndex + 1);
            finalMsg.append(insertFades(text, fromHex, toHex, text.contains("&l"), text.contains("&o"), text.contains("&n"), text.contains("&m"), text.contains("&k")));
            hexIndex++;
        }
        return finalMsg.toString().replace("&","§");
    }

    private static String insertFades(String msg, String fromHex, String toHex, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean magic) {
        msg = msg.replaceAll("&k", "");
        msg = msg.replaceAll("&l", "");
        msg = msg.replaceAll("&m", "");
        msg = msg.replaceAll("&n", "");
        msg = msg.replaceAll("&o", "");
        int length = msg.length();
        Color fromRGB = Color.decode(fromHex);
        Color toRGB = Color.decode(toHex);
        double rStep = Math.abs((double) (fromRGB.getRed() - toRGB.getRed()) / length);
        double gStep = Math.abs((double) (fromRGB.getGreen() - toRGB.getGreen()) / length);
        double bStep = Math.abs((double) (fromRGB.getBlue() - toRGB.getBlue()) / length);
        if (fromRGB.getRed() > toRGB.getRed()) rStep = -rStep; //200, 100
        if (fromRGB.getGreen() > toRGB.getGreen()) gStep = -gStep; //200, 100
        if (fromRGB.getBlue() > toRGB.getBlue()) bStep = -bStep; //200, 100
        Color finalColor = new Color(fromRGB.getRGB());
        msg = msg.replaceAll(RAW_GRADIENT_HEX_REGEX, "");
        msg = msg.replace("", "<$>");
        for (int index = 0; index <= length; index++) {
            int red = (int) Math.round(finalColor.getRed() + rStep);
            int green = (int) Math.round(finalColor.getGreen() + gStep);
            int blue = (int) Math.round(finalColor.getBlue() + bStep);
            if (red > 255) red = 255; if (red < 0) red = 0;
            if (green > 255) green = 255; if (green < 0) green = 0;
            if (blue > 255) blue = 255; if (blue < 0) blue = 0;
            finalColor = new Color(red, green, blue);
            String hex = "#" + Integer.toHexString(finalColor.getRGB()).substring(2);
            String formats = "";
            if (bold) formats += "l";
            if (italic) formats += "o";
            if (underlined) formats += "n";
            if (strikethrough) formats += "m";
            if (magic) formats += "k";
            // Todo ChatColor 能替换掉就好了
            msg = msg.replaceFirst("<\\$>", "" + ChatColor.stripColor(hex) + formats);
        }
        return msg;
    }
}