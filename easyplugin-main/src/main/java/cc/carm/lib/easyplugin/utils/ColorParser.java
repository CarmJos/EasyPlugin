
package cc.carm.lib.easyplugin.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorParser {

    public static String parse(String text) {
        text = parseHexColor(text);
        return parseColor(text);
    }

    public static String[] parse(String... texts) {
        return parse(Arrays.asList(texts)).toArray(new String[0]);
    }

    public static List<String> parse(List<String> texts) {
        return texts.stream().map(ColorParser::parse).collect(Collectors.toList());
    }

    public static String parseColor(final String text) {
        return text.replaceAll("&", "§").replace("§§", "&");
    }

    public static String parseHexColor(String text) {
        Pattern pattern = Pattern.compile("&\\((&?#[0-9a-fA-F]{6})\\)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String hexColor = text.substring(matcher.start() + 2, matcher.end() - 1);
            hexColor = hexColor.replace("&", "");
            StringBuilder bukkitColorCode = new StringBuilder('§' + "x");
            for (int i = 1; i < hexColor.length(); i++) {
                bukkitColorCode.append('§').append(hexColor.charAt(i));
            }
            text = text.replaceAll("&\\(" + hexColor + "\\)", bukkitColorCode.toString().toLowerCase());
            matcher.reset(text);
        }
        return text;
    }
}
