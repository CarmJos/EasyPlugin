package cc.carm.lib.easyplugin.command;

import cc.carm.lib.mineconfiguration.bukkit.value.ConfiguredMessage;
import cc.carm.lib.mineconfiguration.bukkit.value.ConfiguredMessageList;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface NamedExecutor {

    String getName();

    List<String> getAliases();

    default boolean hasPermission(Permissible permissible) {
        return true;
    }

    default Void sendMessage(@NotNull CommandSender sender, @NotNull String... messages) {
        return sendMessage(sender, (Function<String, String>) null, messages);
    }

    default Void sendMessage(@NotNull CommandSender sender,
                             @Nullable Function<String, String> parser,
                             @NotNull String... messages) {
        if (messages == null || messages.length == 0) return null;
        Function<String, String> finalParser = Optional.ofNullable(parser).orElse(Function.identity());
        Arrays.stream(messages).map(finalParser).forEach(sender::sendMessage);
        return null;
    }

    default Void sendMessage(@NotNull CommandSender sender,
                             @NotNull ConfiguredMessageList<?> messages,
                             @Nullable Object... params) {
        messages.send(sender, params);
        return null;
    }

    default Void sendMessage(@NotNull CommandSender sender,
                             @NotNull ConfiguredMessage<?> message,
                             @Nullable Object... params) {
        message.send(sender, params);
        return null;
    }


}
