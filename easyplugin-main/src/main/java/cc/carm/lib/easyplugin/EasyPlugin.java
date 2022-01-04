package cc.carm.lib.easyplugin;

import cc.carm.lib.easyplugin.utils.ColorParser;
import cc.carm.lib.easyplugin.utils.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class EasyPlugin extends JavaPlugin {


	private SchedulerUtils scheduler;
	private boolean initialized = false;

	@Override
	public void onLoad() {
		scheduler = new SchedulerUtils(this);

		if (!isOverride("load")) return;

		log(getName() + " " + getDescription().getVersion() + " &7开始加载...");
		long startTime = System.currentTimeMillis();
		load();
		log("加载完成 ，共耗时 " + (System.currentTimeMillis() - startTime) + " ms 。");
	}

	@Override
	public void onEnable() {

		log(getName() + " " + getDescription().getVersion() + " &7开始启动...");
		long startTime = System.currentTimeMillis();

		this.initialized = initialize();

		if (!isInitialized()) {
			setEnabled(false);
			return;
		}

		log("启用完成 ，共耗时 " + (System.currentTimeMillis() - startTime) + " ms 。");
	}


	@Override
	public void onDisable() {
		if (!isOverride("shutdown")) return;

		log(getName() + " " + getDescription().getVersion() + " 开始卸载...");
		long startTime = System.currentTimeMillis();
		shutdown();
		log("卸载完成 ，共耗时 " + (System.currentTimeMillis() - startTime) + " ms 。");
	}

	public void load() {
	}

	public abstract boolean initialize();

	public void shutdown() {
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean isDebugging() {
		return false;
	}

	public SchedulerUtils getScheduler() {
		return scheduler;
	}

	public void regListener(@NotNull Listener... listeners) {
		Arrays.stream(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
	}

	public void registerCommand(String commandName,
								@NotNull CommandExecutor executor) {
		registerCommand(commandName, executor, executor instanceof TabCompleter ? (TabCompleter) executor : null);
	}

	public void registerCommand(String commandName,
								@NotNull CommandExecutor executor,
								@Nullable TabCompleter tabCompleter) {
		PluginCommand command = Bukkit.getPluginCommand(commandName);
		if (command == null) return;
		command.setExecutor(executor);
		if (tabCompleter != null) command.setTabCompleter(tabCompleter);
	}


	public void log(@Nullable String... messages) {
		print(null, messages);
	}

	public void print(@Nullable String prefix, @Nullable String... messages) {
		Arrays.stream(messages)
				.map(message -> "[" + getName() + "] " + (prefix == null ? "" : prefix) + message)
				.map(ColorParser::parse)
				.forEach(message -> Bukkit.getConsoleSender().sendMessage(message));
	}

	public void error(String... messages) {
		print("&c[ERROR] &r", messages);
	}

	public void debug(@Nullable String... messages) {
		if (isDebugging()) print("&7[DEBUG] &r", messages);
	}


	private boolean isOverride(String methodName) {
		Map<Method, Method> methodMap = new HashMap<>();
		Arrays.stream(EasyPlugin.class.getDeclaredMethods())
				.filter(method -> method.getName().equals(methodName))
				.forEach(method -> Arrays.stream(getClass().getDeclaredMethods())
						.filter(extend -> extend.getName().equals(methodName))
						.filter(extend -> extend.getReturnType().equals(method.getReturnType()))
						.filter(extend -> extend.getParameterTypes().length == method.getParameterTypes().length)
						.findFirst().ifPresent(extendMethod -> methodMap.put(method, extendMethod))
				);
		return !methodMap.isEmpty();
	}
}
