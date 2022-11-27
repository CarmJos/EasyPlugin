package cc.carm.lib.easyplugin.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 颜色解析器。
 * <br> 普通颜色 格式 {@code &+颜色代码 }，如 {@literal &c} 、{@literal &a}
 * <br> RGB颜色(版本需要≥1.14) 格式 {@code &(#XXXXXX) }，如 {@literal &(#aaaaaa)}
 * <br> 渐变RBG颜色(版本需要≥1.14) 格式 {@code &<#XXXXXX>FOOBAR&<#XXXXXX> }
 * <p> 注意：当使用渐变RGB颜色时，普通颜色代码与RGB颜色代码将失效。
 */
public class ColorParser {

    public static final Pattern HEX_PATTERN = Pattern.compile("&\\(&?#([\\da-fA-F]{6})\\)");
    public static final Pattern GRADIENT_PATTERN = Pattern.compile("&<&?#([\\da-fA-F]{6})>");

    public static String parse(String text) {
        return parseBaseColor(parseHexColor(parseGradientColor(text)));
    }

    public static String[] parse(String... texts) {
        return parse(Arrays.asList(texts)).toArray(new String[0]);
    }

    public static List<String> parse(List<String> texts) {
        return texts.stream().map(ColorParser::parse).collect(Collectors.toList());
    }

    /**
     * 解析消息中的基本颜色代码格式 {@code &+颜色代码 }，如 {@literal &c} 、{@literal &a}
     *
     * @param text 消息内容
     * @return RGB处理后的消息
     * @see net.md_5.bungee.api.ChatColor
     */
    public static String parseBaseColor(final String text) {
        return text.replaceAll("&", "§").replace("§§", "&");
    }

    /**
     * 解析消息中的RGB颜色代码(版本需要≥1.14) 格式 {@code &(#XXXXXX) }，如 {@literal &(#aaaaaa)}
     *
     * @param text 消息内容
     * @return RGB处理后的消息
     */
    public static String parseHexColor(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            text = matcher.replaceFirst(buildHexColor(matcher.group(1)).toLowerCase());
            matcher.reset(text);
        }
        return text;
    }

    /**
     * 对一条消息进行RGB渐变处理(版本需要≥1.14)，格式 {@code &<#XXXXXX>FOOBAR&<#XXXXXX> }。
     *
     * @param text 消息内容
     * @return RGB渐变处理后的消息
     */
    public static String parseGradientColor(String text) {
        List<String> colors = new ArrayList<>();

        Matcher matcher = ColorParser.GRADIENT_PATTERN.matcher(text);
        while (matcher.find()) colors.add(matcher.group(1));

        if (colors.isEmpty()) return text; // 无渐变颜色，直接跳出

        String[] parts = ColorParser.GRADIENT_PATTERN.split(text);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String startHex = i - 1 >= 0 && colors.size() > i - 1 ? colors.get(i - 1) : null; // 本条消息的起始颜色
            String endHex = colors.size() > i ? colors.get(i) : null; // 本条消息的结束颜色
            builder.append(gradientText(parts[i], startHex, endHex));
        }

        return builder.toString();
    }

    public static String gradientText(@NotNull String text, @Nullable String startHex, @Nullable String endHex) {
        if (startHex == null || endHex == null) return gradientText(text, (Color) null, null);
        return gradientText(text, Color.decode("0x" + startHex), Color.decode("0x" + endHex));
    }

    public static String gradientText(@NotNull String text, @Nullable Color startColor, @Nullable Color endColor) {
        if (startColor == null || endColor == null || text.isEmpty()) return text; // 有任一为空，则不进行渐变上色
        if (text.length() == 1) return buildHexColor(colorToHex(startColor)) + text;// 只有一个字符，无需渐变

        String[] characters = text.split("");
        int step = characters.length; // 变换次数

        // 决定每种颜色变换的方向
        int rDirection = startColor.getRed() < endColor.getRed() ? 1 : -1;
        int gDirection = startColor.getGreen() < endColor.getGreen() ? 1 : -1;
        int bDirection = startColor.getBlue() < endColor.getBlue() ? 1 : -1;

        int rStep = Math.abs(startColor.getRed() - endColor.getRed()) / (step - 1);
        int gStep = Math.abs(startColor.getGreen() - endColor.getGreen()) / (step - 1);
        int bStep = Math.abs(startColor.getBlue() - endColor.getBlue()) / (step - 1);

        String[] hexes = IntStream.range(0, step).mapToObj(i -> colorToHex(
                startColor.getRed() + rStep * i * rDirection,
                startColor.getGreen() + gStep * i * gDirection,
                startColor.getBlue() + bStep * i * bDirection
        )).toArray(String[]::new);

        return IntStream.range(0, characters.length)
                .mapToObj(i -> buildHexColor(hexes[i]) + characters[i])
                .collect(Collectors.joining());
    }

    protected static String colorToHex(Color color) {
        return colorToHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    protected static String colorToHex(int r, int g, int b) {
        // 将R、G、B转换为16进制(若非2位则补0)输出
        return String.format("%02X%02X%02X", r, g, b);
    }

    protected static String buildHexColor(String hexCode) {
        return Arrays.stream(hexCode.split("")).map(s -> '§' + s)
                .collect(Collectors.joining("", '§' + "x", ""));
    }

}