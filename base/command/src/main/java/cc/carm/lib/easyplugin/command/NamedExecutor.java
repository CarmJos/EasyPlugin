package cc.carm.lib.easyplugin.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface NamedExecutor {

    @NotNull String getIdentifier();

    @NotNull List<String> getAliases();

    default boolean hasPermission(@NotNull CommandSender sender) {
        return true;
    }

    default Void sendMessage(@NotNull CommandSender sender, @NotNull String... messages) {
        return sendMessage(sender, Function.identity(), messages);
    }

    default Void sendMessage(@NotNull CommandSender sender,
                             @Nullable Function<String, String> parser,
                             @NotNull String... messages) {
        if (messages == null || messages.length == 0) return null;
        Function<String, String> finalParser = Optional.ofNullable(parser).orElse(Function.identity());
        Arrays.stream(messages).map(finalParser).forEach(sender::sendMessage);
        return null;
    }

}
