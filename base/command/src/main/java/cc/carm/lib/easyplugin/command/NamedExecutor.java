package cc.carm.lib.easyplugin.command;

import org.bukkit.permissions.Permissible;

import java.util.Collections;
import java.util.List;

public interface NamedExecutor {

    String getName();

    default List<String> getAliases() {
        return Collections.singletonList(getName());
    }

    default boolean hasPermission(Permissible permissible) {
        return true;
    }

}
