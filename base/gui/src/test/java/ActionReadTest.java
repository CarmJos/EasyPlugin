import cc.carm.lib.easyplugin.gui.configuration.GUIActionType;
import cc.carm.lib.easyplugin.gui.configuration.GUIConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ActionReadTest {


    @Test
    public void test() {

        List<String> actions = Arrays.asList(
                "[CHAT] 123123",
                "[SHIFT_LEFT:CHAT] /test qwq",
                "[CONSOLE] say hello",
                "[CLOSE]"
        );

        for (String actionString : actions) {
            int prefixStart = actionString.indexOf("[");
            int prefixEnd = actionString.indexOf("]");
            if (prefixStart < 0 || prefixEnd < 0) continue;

            String prefix = actionString.substring(prefixStart + 1, prefixEnd);
            ClickType clickType = null;
            GUIActionType actionType;
            if (prefix.contains(":")) {
                String[] args = prefix.split(":");
                clickType = GUIConfiguration.readClickType(args[0]);
                actionType = GUIActionType.readActionType(args[1]);
            } else {
                actionType = GUIActionType.readActionType(prefix);
            }

            if (actionType == null) {
                System.out.println("# " + actionString);
                System.out.println("- actionType is Null");
                continue;
            }

            System.out.println("# " + actionType.name() + " " + (clickType == null ? "" : clickType.name()));
            System.out.println("- " + actionString.substring(prefixEnd + 1).trim());
        }


    }

}
