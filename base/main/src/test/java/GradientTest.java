import cc.carm.lib.easyplugin.utils.ColorParser;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;

import static cc.carm.lib.easyplugin.utils.ColorParser.parse;
import static cc.carm.lib.easyplugin.utils.ColorParser.parseGradientColor;

public class GradientTest {


    @Test
    public void test() {
        System.out.println(" ");
        System.out.println(parseGradientColor("&<#AAAAAA>我真的&<#BBBBBB>爱死&<#111111>你&<#FFFFFF>！"));

        // 测试穿插
        System.out.println(parse("&<#AAAAAA>&l我&r真&b的&<#BBBBBB>&o爱死&<#111111>你&<#FFFFFF>了&r！"));
        System.out.println(parse("&<#AAAAAA>&l我&r真&(#666666)的&<#BBBBBB>&o爱死&<#111111>你&<#FFFFFF>了&r！"));
        System.out.println(parse("&r正常的颜色理应&c&l不受影响&r。"));

    }

    @Test
    public void formatReadTest() {
        LinkedHashMap<Integer, String> formats = new LinkedHashMap<>();
        String text = "&k&l &m&1我&k爱你爱你爱你&o吗？";
        Matcher matcher = ColorParser.FORMAT_PATTERN.matcher(text);
        while (matcher.find()) {
            String code = matcher.group();
            formats.put(matcher.start(), code);
            text = matcher.replaceFirst("");
            matcher.reset(text);
        }

        formats.forEach((index, code) -> System.out.println(index + " -> " + code));

        String[] parts = text.split("");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String format = formats.get(i);
            if (format != null) builder.append(ColorParser.parseBaseColor(format));
            builder.append(parts[i]);
        }

        System.out.println(builder);
    }

}
