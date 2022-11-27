import cc.carm.lib.easyplugin.utils.ColorParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class GradientTest {


    @Test
    public void test() {
        System.out.println(" ");
        System.out.println(parseGradientColor("&<#AAAAAA>我真的&<#BBBBBB>爱死&<#111111>你&<#FFFFFF>！"));
    }


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
            System.out.println("[" + startHex + "]" + parts[i] + "[" + endHex + "]");
            builder.append(ColorParser.gradientText(parts[i], startHex, endHex));
        }

        return builder.toString();
    }

}
