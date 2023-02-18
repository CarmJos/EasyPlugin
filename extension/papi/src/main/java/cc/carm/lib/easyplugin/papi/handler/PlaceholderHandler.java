package cc.carm.lib.easyplugin.papi.handler;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlaceholderHandler {

    @Nullable Object handle(@Nullable OfflinePlayer player, @NotNull String[] args) throws Exception;

}
