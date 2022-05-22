package cc.carm.lib.easyplugin.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public abstract class SubCommand implements NamedExecutor {

    private final String name;
    private final String[] aliases;

    public SubCommand(String name) {
        this(name, new String[0]);
    }

    public SubCommand(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(this.aliases);
    }

    public abstract Void execute(JavaPlugin plugin, CommandSender sender, String[] args);

    public List<String> tabComplete(JavaPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }


}
