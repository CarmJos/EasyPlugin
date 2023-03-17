package cc.carm.lib.easyplugin.command.alias;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AliasCommand extends Command {

    protected final AliasCommandManager aliasCommandManager;
    protected final String targetCommand;

    public AliasCommand(String name, AliasCommandManager aliasCommandManager, String targetCommand) {
        super(name);
        this.aliasCommandManager = aliasCommandManager;
        this.targetCommand = targetCommand;
    }

    protected SimpleCommandMap getCommandMap() {
        return this.aliasCommandManager.getCommandMap();
    }

    protected String buildCommand(String[] args) {
        return this.targetCommand + " " + String.join(" ", args);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return getCommandMap().dispatch(sender, buildCommand(args));
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                    @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        return Optional.ofNullable(getCommandMap().tabComplete(sender, buildCommand(args))).orElse(Collections.emptyList());
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }

}