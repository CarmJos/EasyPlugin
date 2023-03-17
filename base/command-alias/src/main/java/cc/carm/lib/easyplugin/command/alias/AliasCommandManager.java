package cc.carm.lib.easyplugin.command.alias;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 指令简化别名(简化映射)管理器
 * <br>支持将插件内复杂的子指令简化为一个单独的指令，方便玩家使用。
 */
public class AliasCommandManager {

    protected final @NotNull JavaPlugin plugin;
    protected final @NotNull String prefix;

    protected final @NotNull SimpleCommandMap commandMap;
    protected final @NotNull Field knownCommandsFiled;

    protected final @NotNull Map<String, AliasCommand> registeredCommands = new HashMap<>();

    public AliasCommandManager(@NotNull JavaPlugin plugin) throws Exception {
        this(plugin, plugin.getName());
    }

    public AliasCommandManager(@NotNull JavaPlugin plugin, @NotNull String prefix) throws Exception {
        this.plugin = plugin;
        this.prefix = prefix.trim();

        SimplePluginManager manager = (SimplePluginManager) Bukkit.getPluginManager();
        Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        this.commandMap = (SimpleCommandMap) commandMapField.get(manager);

        this.knownCommandsFiled = SimpleCommandMap.class.getDeclaredField("knownCommands");
        this.knownCommandsFiled.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Command> getKnownCommands() {
        try {
            return (Map<String, Command>) knownCommandsFiled.get(commandMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    protected @NotNull SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    public String getCommandPrefix() {
        return this.prefix;
    }

    public void register(@NotNull String alias, @NotNull String subCommand) {
        AliasCommand current = this.registeredCommands.get(alias);
        if (current != null) current.unregister(getCommandMap());

        AliasCommand cmd = new AliasCommand(alias, this, getCommandPrefix() + " " + subCommand);
        this.registeredCommands.put(alias, cmd);
        getCommandMap().register(plugin.getName(), cmd);
    }

    public void unregister(@NotNull String alias) {
        AliasCommand current = this.registeredCommands.remove(alias);
        if (current != null) {
            getKnownCommands().remove(alias);
            current.unregister(getCommandMap());
        }
    }

    public void unregisterAll() {
        registeredCommands.forEach((k, v) -> {
            getKnownCommands().remove(k);
            v.unregister(getCommandMap());
        });
        registeredCommands.clear();
    }

}
