
package cc.carm.lib.easyplugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CommandHandler implements TabExecutor, NamedExecutor {

    protected final @NotNull JavaPlugin plugin;
    protected final @NotNull String cmd;

    protected final @NotNull Map<String, SubCommand> registeredCommands = new HashMap<>();
    protected final @NotNull Map<String, CommandHandler> registeredHandlers = new HashMap<>();

    protected final @NotNull Map<String, String> aliasesMap = new HashMap<>();

    public CommandHandler(@NotNull JavaPlugin plugin) {
        this(plugin, plugin.getName());
    }

    public CommandHandler(@NotNull JavaPlugin plugin, @NotNull String cmd) {
        this.plugin = plugin;
        this.cmd = cmd;
    }

    public abstract void noArgs(CommandSender sender);

    public abstract void unknownCommand(CommandSender sender, String[] args);

    public abstract void noPermission(CommandSender sender);

    @Override
    public String getName() {
        return this.cmd;
    }

    public void registerSubCommand(SubCommand command) {
        String name = command.getName().toLowerCase();
        this.registeredCommands.put(name, command);
        command.getAliases().forEach(alias -> this.aliasesMap.put(alias.toLowerCase(), name));
    }

    public void registerHandler(CommandHandler handler) {
        String name = handler.getName().toLowerCase();
        this.registeredHandlers.put(name, handler);
        handler.getAliases().forEach(alias -> this.aliasesMap.put(alias.toLowerCase(), name));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            this.noArgs(sender);
        } else {
            String input = args[0].toLowerCase();

            CommandHandler handler = getHandler(input);
            if (handler != null) {
                if (!handler.hasPermission(sender)) {
                    this.noPermission(sender);
                } else {
                    handler.onCommand(sender, command, label, this.shortenArgs(args));
                }
            }

            SubCommand subCommand = getSubCommand(input);
            if (subCommand == null) {
                this.unknownCommand(sender, args);
            } else if (!subCommand.hasPermission(sender)) {
                this.noPermission(sender);
            } else {
                try {
                    subCommand.execute(this.plugin, sender, this.shortenArgs(args));
                } catch (ArrayIndexOutOfBoundsException var9) {
                    this.unknownCommand(sender, args);
                }
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 0) return Collections.emptyList();

        String input = args[0].toLowerCase();
        if (args.length == 1) {
            return getExecutors().stream()
                    .filter(e -> e.hasPermission(sender))
                    .map(NamedExecutor::getName)
                    .filter(s -> StringUtil.startsWithIgnoreCase(s, input))
                    .collect(Collectors.toList());
        } else {

            CommandHandler handler = getHandler(input);
            if (handler != null && handler.hasPermission(sender)) {
                return handler.onTabComplete(sender, command, alias, this.shortenArgs(args));
            }

            SubCommand subCommand = getSubCommand(input);
            if (subCommand != null && subCommand.hasPermission(sender)) {
                return subCommand.tabComplete(this.plugin, sender, this.shortenArgs(args));
            }

            return Collections.emptyList();
        }
    }

    public List<NamedExecutor> getExecutors() {
        Set<NamedExecutor> executors = new HashSet<>();
        executors.addAll(this.registeredHandlers.values());
        executors.addAll(this.registeredCommands.values());
        List<NamedExecutor> sortedExecutors = new ArrayList<>(executors);
        sortedExecutors.sort(Comparator.comparing(NamedExecutor::getName));
        return sortedExecutors;
    }

    protected @Nullable CommandHandler getHandler(@NotNull String name) {
        CommandHandler fromName = this.registeredHandlers.get(name);
        if (fromName != null) return fromName;

        String nameFromAlias = this.aliasesMap.get(name);
        if (nameFromAlias == null) return null;
        else return this.registeredHandlers.get(nameFromAlias);
    }

    protected @Nullable SubCommand getSubCommand(@NotNull String name) {
        SubCommand fromName = this.registeredCommands.get(name);
        if (fromName != null) return fromName;

        String nameFromAlias = this.aliasesMap.get(name);
        if (nameFromAlias == null) return null;
        else return this.registeredCommands.get(nameFromAlias);
    }

    protected String[] shortenArgs(String[] args) {
        if (args.length == 0) {
            return args;
        } else {
            List<String> argList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
            return argList.toArray(new String[0]);
        }
    }

}
