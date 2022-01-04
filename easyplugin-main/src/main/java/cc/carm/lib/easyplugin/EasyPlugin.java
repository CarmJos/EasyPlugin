package cc.carm.lib.easyplugin;

import cc.carm.lib.easyplugin.i18n.EasyPluginMessageProvider;
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

	protected EasyPluginMessageProvider messageProvider;

	public EasyPlugin() {
		this(new EasyPluginMessageProvider.en_US());
	}

	public EasyPlugin(EasyPluginMessageProvider messageProvider) {
		this.messageProvider = messageProvider;
	}

	private SchedulerUtils scheduler;
	private boolean initialized = false;

	@Override
	public final void onLoad() {
		scheduler = new SchedulerUtils(this);
		if (!hasOverride("load")) return;

		long startTime = System.currentTimeMillis();

		log(messageProvider.loading(this));
		load();
		log(messageProvider.loaded(this, startTime));
	}

	@Override
	public final void onEnable() {
		outputInfo();

		log(messageProvider.enabling(this));
		long startTime = System.currentTimeMillis();

		if (!(this.initialized = initialize())) {
			setEnabled(false);
			log(messageProvider.enableFailure(this, startTime));
			return;
		}

		log(messageProvider.enableSuccess(this, startTime));
	}


	@Override
	public final void onDisable() {
		if (!hasOverride("shutdown") || !isInitialized()) return;
		outputInfo();

		log(messageProvider.disabling(this));
		long startTime = System.currentTimeMillis();
		shutdown();
		log(messageProvider.disabled(this, startTime));
	}

	public void load() {
	}

	public abstract boolean initialize();

	public void shutdown() {
	}

	/**
	 * 重写以展示插件的相关信息，如插件横幅、下载地址等。
	 */
	public void outputInfo() {
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

	public void print(@Nullable String prefix, @Nullable String... messages) {
		messageProvider.print(this, prefix, messages);
	}

	public void log(@Nullable String... messages) {
		print(null, messages);
	}

	public void error(String... messages) {
		print("&c[ERROR] &r", messages);
	}

	public void debug(@Nullable String... messages) {
		if (isDebugging()) print("&8[DEBUG] &r", messages);
	}


	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean hasOverride(String methodName) {
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
