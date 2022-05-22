package cc.carm.lib.easyplugin.command;

import org.bukkit.permissions.Permissible;

import java.util.List;

public interface NamedExecutor {

    String getName();

    List<String> getAliases();

    default boolean hasPermission(Permissible permissible) {
        return true;
    }

}
