package cc.carm.lib.easyplugin.utils;

import java.util.Arrays;
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

}