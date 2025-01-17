import cc.carm.lib.easyplugin.utils.ColorParser;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;

import static cc.carm.lib.easyplugin.utils.ColorParser.*;

public class ColorParseTest {


    @Test
    public void test() {
        System.out.println(" ");
        System.out.println(parseGradientColor("&<#AAAAAA>我真的&<#BBBBBB>爱死&<#111111>你&<#FFFFFF>！"));

        // 测试穿插
        System.out.println(parse("&<#AAAAAA>&l&m我真的&o尊的&r真的&<#BBBBBB>&o爱死&<#111111>你&<#FFFFFF>了&r！"));
        System.out.println(parse("&<#AAAAAA>&l我&r真&(#666666)的&<#BBBBBB>&o爱死&<#111111>你&<#FFFFFF>了&r！"));
        System.out.println(parse("&r正常的颜色理应&c&l不受影响&r。"));

        System.out.println(clear("&f&l测试&<#AAAAAA>清理颜色代码&<#111111> &&这样应该&(#666666)不被影响 &f。"));
    }

}
