package cc.carm.lib.easyplugin.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public abstract class SubCommand implements NamedExecutor {
    private final String name;

    public SubCommand(String name) {
        this.name = name;
    }

    public abstract Void execute(JavaPlugin plugin, CommandSender sender, String[] args);

    public List<String> tabComplete(JavaPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return this.name;
    }

}
