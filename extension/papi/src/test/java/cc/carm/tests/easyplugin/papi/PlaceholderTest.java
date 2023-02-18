package cc.carm.tests.easyplugin.papi;

import cc.carm.lib.easyplugin.papi.EasyPlaceholder;
import org.junit.Test;

public class PlaceholderTest {


    @Test
    public void onTest() {
        DemoPlaceholder placeholder = new DemoPlaceholder();

        System.out.println("# Placeholders");
        placeholder.getPlaceholders().forEach(s -> System.out.println(" -> " + s));

        System.out.println();
        System.out.println("# Request tests");

        test(placeholder, "game_items");
        test(placeholder, "g_time");
        test(placeholder, "game_loc");
        test(placeholder, "ver");

        test(placeholder, "game_random");
        test(placeholder, "game_random_0_5");
        test(placeholder, "game_random_100");

        System.out.println("# Error tests");
        test(placeholder, "game_random_try");
    }

    private void test(EasyPlaceholder placeholder, String input) {
        System.out.println(" " + input + " -> " + placeholder.onRequest(null, input));
    }


}
