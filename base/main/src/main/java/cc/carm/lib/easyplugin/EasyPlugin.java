package cc.carm.lib.easyplugin;

import cc.carm.lib.easyplugin.i18n.EasyPluginMessageProvider;
import cc.carm.lib.easyplugin.utils.JarResourceUtils;
import cc.carm.lib.easyplugin.utils.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class EasyPlugin extends JavaPlugin {

    protected @NotNull EasyPluginMessageProvider messageProvider;

    public EasyPlugin() {
        this(EasyPluginMessageProvider.ZH_CN);
    }

    public EasyPlugin(@NotNull EasyPluginMessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    protected EasyPlugin(JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
        this(EasyPluginMessageProvider.ZH_CN, loader, descriptionFile, dataFolder, file);
    }

    protected EasyPlugin(@NotNull EasyPluginMessageProvider messageProvider,
                         JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
        super(loader, descriptionFile, dataFolder, file);
        this.messageProvider = messageProvider;
    }

    protected SchedulerUtils scheduler;
    protected boolean initialized = false;

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
        if (!hasOverride("shutdown") || !this.initialized) return;
        outputInfo();

        log(messageProvider.disabling(this));
        long startTime = System.currentTimeMillis();
        shutdown();
        log(messageProvider.disabled(this, startTime));
    }

    protected void load() {
    }

    protected abstract boolean initialize();

    protected void shutdown() {
    }

    /**
     * 重写以展示插件的相关信息，如插件横幅、下载地址等。
     */
    public void outputInfo() {
        Optional.ofNullable(JarResourceUtils.readResource(this.getResource("PLUGIN_INFO"))).ifPresent(this::log);
    }

    public boolean isDebugging() {
        return false;
    }

    public SchedulerUtils getScheduler() {
        return scheduler;
    }

    public void registerListener(@NotNull Listener... listeners) {
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

    /**
     * 在主线程执行操作，并支持获取其结果。
     *
     * @param <T> 结果类型
     * @return CompletableFuture
     */
    public @NotNull <T> CompletableFuture<T> supplySync(@NotNull Supplier<T> action) {
        CompletableFuture<T> future = new CompletableFuture<>();
        getScheduler().run(() -> future.complete(action.get()));
        return future;
    }

    /**
     * 在异步线程中执行一个操作，并获取操作的结果。
     *
     * @param <T> 事件类型
     * @return CompletableFuture
     */
    public @NotNull <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> action) {
        CompletableFuture<T> future = new CompletableFuture<>();
        getScheduler().runAsync(() -> future.complete(action.get()));
        return future;
    }

    /**
     * 在主线程唤起一个事件，并支持获取事件的结果。
     *
     * @param event 同步事件 (isAsync=false)
     * @param <T>   事件类型
     * @return CompletableFuture
     */
    public @NotNull <T extends Event> CompletableFuture<T> callSync(T event) {
        return supplySync(() -> {
            Bukkit.getPluginManager().callEvent(event);
            return event;
        });
    }

    /**
     * 在异步线程中唤起一个事件，并支持获取事件的结果。
     *
     * @param event 异步事件 (isAsync=true)
     * @param <T>   事件类型
     * @return CompletableFuture
     */
    public @NotNull <T extends Event> CompletableFuture<T> callAsync(T event) {
        return supplyAsync(() -> {
            Bukkit.getPluginManager().callEvent(event);
            return event;
        });
    }

    protected void setMessageProvider(@NotNull EasyPluginMessageProvider provider) {
        this.messageProvider = provider;
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
