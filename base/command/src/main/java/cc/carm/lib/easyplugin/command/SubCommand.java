package cc.carm.lib.easyplugin.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public abstract class SubCommand<C extends CommandHandler> implements NamedExecutor {

    private final @NotNull C parent;

    private final String name;
    private final List<String> aliases;

    public SubCommand(@NotNull C parent, String name, String... aliases) {
        this.parent = parent;
        this.name = name;
        this.aliases = Arrays.asList(aliases);
    }

    public @NotNull C getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Unmodifiable
    public List<String> getAliases() {
        return this.aliases;
    }

    public abstract Void execute(JavaPlugin plugin, CommandSender sender, String[] args) throws Exception;

    public List<String> tabComplete(JavaPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
