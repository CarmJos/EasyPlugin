package cc.carm.tests.easyplugin.papi;

import cc.carm.lib.easyplugin.papi.EasyPlaceholder;
import cc.carm.lib.easyplugin.papi.expansion.SubExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class DemoPlaceholder extends EasyPlaceholder {

    public DemoPlaceholder() {
        super(
                "TestPlugin", true, "TestPlaceholder",
                "TestPlugin", "1.0.0", "Carm"
        );

        handle("version", (player, args) -> "1.0.0", "ver", "v");
        handleSection("game", (section) -> {

            // 即处理 %TestPlaceholder_game_items%
            section.handle("items", ((player, args) -> {
                if (player == null || !player.isOnline() || !(player instanceof Player)) return -1;
                PlayerInventory inv = ((Player) player).getInventory();
                return Arrays.stream(inv.getContents())
                        .filter(Objects::nonNull)
                        .mapToInt(ItemStack::getAmount)
                        .count();
            }));

            // 即处理 %TestPlaceholder_game_location% 或 %TestPlaceholder_game_loc%
            section.handle("location", (player, args) -> {
                if (player == null || !player.isOnline() || !(player instanceof Player)) return "Unknown";
                return ((Player) player).getLocation();
            }, "loc" /* 声明此变量的别称*/);

            // 即处理 %TestPlaceholder_game_time%
            section.handle("time", (player, args) -> System.currentTimeMillis());

            section.handle("random",
                    (player, args) -> {
                        if (args.length == 0) return Math.random();
                        else if (args.length == 1) return Math.random() * Double.parseDouble(args[0]);
                        else if (args.length == 2) {
                            return Math.random() * (Double.parseDouble(args[1]) - Double.parseDouble(args[0])) + Double.parseDouble(args[0]);
                        } else return onErrorParams(player);
                    },
                    (params) -> {
                        params.add("<最小值>_<最大值>");
                        params.add("<基础值(乘随机数)>");
                    });

        }, "g");

    }

    @Override
    public String onErrorParams(@Nullable OfflinePlayer player) {
        return super.onErrorParams(player);
    }

    @Override
    public String onNullResponse(@Nullable OfflinePlayer player) {
        return super.onNullResponse(player);
    }

    @Override
    public String onException(@Nullable OfflinePlayer player, @NotNull SubExpansion<?> expansion, @NotNull Exception exception) {
        return super.onException(player, expansion, exception);
    }

}
