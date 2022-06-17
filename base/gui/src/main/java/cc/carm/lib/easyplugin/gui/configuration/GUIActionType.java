package cc.carm.lib.easyplugin.gui.configuration;

import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiConsumer;

public enum GUIActionType {

    /**
     * 以玩家聊天的形式执行
     * 若内容以 “/" 开头，则会以玩家身份执行命令。
     */
    CHAT((player, string) -> {
        if (string == null) return;
        MessageUtils.setPlaceholders(player, Collections.singletonList(string)).forEach(player::chat);
    }),

    /**
     * 以后台的形式执行指令
     * 指令内容不需要以“/”开头。
     */
    CONSOLE((player, string) -> {
        if (string == null) return;
        MessageUtils.setPlaceholders(player, Collections.singletonList(string))
                .forEach(message -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message));
    }),

    /**
     * 向玩家发送消息。
     */
    MESSAGE(MessageUtils::send),

    /**
     * 向玩家发送声音。
     * 允许配置音量与音调
     * <ul>
     *   <li>SOUND_NAME</li>
     *   <li>SOUND_NAME:VOLUME</li>
     *   <li>SOUND_NAME:VOLUME:PITCH</li>
     * </ul>
     */
    SOUND((player, string) -> {
        if (string == null) return;
        try {
            String[] args = string.contains(":") ? string.split(":") : new String[]{string};
            Sound sound = Arrays.stream(Sound.values())
                    .filter(s -> s.name().equals(args[0]))
                    .findFirst().orElse(null);

            if (sound == null) return;
            float volume = args.length > 1 ? Float.parseFloat(args[1]) : 1F;
            float pitch = args.length > 2 ? Float.parseFloat(args[2]) : 1F;

            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (Exception ignored) {
        }
    }),

    /**
     * 为玩家关闭GUI。
     */
    CLOSE((player, string) -> player.closeInventory());

    BiConsumer<@NotNull Player, @Nullable String> executor;


    GUIActionType(BiConsumer<@NotNull Player, @Nullable String> executor) {
        this.executor = executor;
    }

    public BiConsumer<@NotNull Player, @Nullable String> getExecutor() {
        return executor;
    }

    public static GUIActionType readActionType(String string) {
        return Arrays.stream(GUIActionType.values())
                .filter(action -> action.name().equalsIgnoreCase(string))
                .findFirst().orElse(null);
    }

}