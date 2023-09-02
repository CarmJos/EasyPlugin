package cc.carm.lib.easyplugin.user;

import cc.carm.lib.easyplugin.EasyPlugin;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class UserDataManager<K, U extends UserData<K>> {

    protected final @NotNull EasyPlugin plugin;

    protected final @NotNull ExecutorService executor;
    protected final @NotNull Map<K, U> dataCache;

    public UserDataManager(@NotNull EasyPlugin plugin) {
        this(plugin, Executors.newCachedThreadPool((r) -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName(plugin.getName() + "-UserManager");
            return t;
        }), new ConcurrentHashMap<>());
    }

    public UserDataManager(@NotNull EasyPlugin plugin, @NotNull ExecutorService executor) {
        this(plugin, executor, new ConcurrentHashMap<>());
    }

    public UserDataManager(@NotNull EasyPlugin plugin, @NotNull ExecutorService executor, @NotNull Map<K, U> cacheMap) {
        this.plugin = plugin;
        this.executor = executor;
        this.dataCache = cacheMap;
    }

    public void shutdown() {
        this.executor.shutdown();
    }

    protected @NotNull EasyPlugin getPlugin() {
        return plugin;
    }

    protected @NotNull Logger getLogger() {
        return getPlugin().getLogger();
    }

    public String serializeKey(@NotNull K key) {
        return key.toString();
    }

    public abstract @NotNull U emptyUser(@NotNull K key);

    public @NotNull U errorUser(@NotNull K key) {
        return emptyUser(key);
    }

    protected abstract @Nullable U loadData(@NotNull K key) throws Exception;

    protected abstract void saveData(@NotNull U data) throws Exception;

    public @NotNull CompletableFuture<U> load(@NotNull K key) {
        return load(key, false);
    }

    public @NotNull CompletableFuture<U> load(@NotNull K key, boolean cache) {
        return load(key, () -> cache);
    }

    public @NotNull Map<K, U> getDataCache() {
        return dataCache;
    }

    @Unmodifiable
    public @NotNull Set<U> list() {
        return ImmutableSet.copyOf(getDataCache().values());
    }

    public @NotNull U get(@NotNull K key) {
        return Optional.ofNullable(getNullable(key)).orElseThrow(() -> new NullPointerException("User " + key + " not found."));
    }

    public @Nullable U getNullable(@NotNull K key) {
        return getDataCache().get(key);
    }

    public @NotNull Optional<@Nullable U> getOptional(@NotNull K key) {
        return Optional.ofNullable(getNullable(key));
    }

    public @NotNull CompletableFuture<U> load(@NotNull K key, @NotNull Supplier<Boolean> cacheCondition) {
        return CompletableFuture.supplyAsync(() -> {
            String identifier = serializeKey(key);

            try {
                long s1 = System.currentTimeMillis();
                getPlugin().debug("开始加载用户 " + identifier + " 的数据...");
                U data = loadData(key);
                if (data == null) {
                    getPlugin().debug("数据库内不存在用户 " + identifier + " 的数据，视作新档案。");
                    return emptyUser(key);
                } else {
                    getPlugin().debug("加载用户 " + identifier + " 的数据完成，耗时 " + (System.currentTimeMillis() - s1) + " ms.");
                    return data;
                }
            } catch (Exception ex) {
                getPlugin().error("加载用户 " + serializeKey(key) + " 数据失败，请检查相关配置！");
                ex.printStackTrace();
                return errorUser(key);
            }

        }, executor).thenApply((data) -> {
            if (cacheCondition.get()) dataCache.put(key, data);
            return data;
        });
    }

    public @NotNull CompletableFuture<Boolean> save(@NotNull U user) {
        return CompletableFuture.supplyAsync(() -> {
            String identifier = serializeKey(user.getKey());

            try {
                long s1 = System.currentTimeMillis();
                getPlugin().debug("开始保存用户 " + identifier + " 的数据...");
                saveData(user);
                getPlugin().debug("保存用户 " + identifier + " 的数据完成，耗时 " + (System.currentTimeMillis() - s1) + " ms.");
                return true;
            } catch (Exception ex) {
                getPlugin().error("保存用户 " + identifier + " 数据失败，请检查相关配置！");
                ex.printStackTrace();
                return false;
            }

        }, executor);
    }

    public @NotNull CompletableFuture<Boolean> unload(@NotNull K key) {
        return unload(key, true);
    }

    public @NotNull CompletableFuture<Boolean> unload(@NotNull K key, boolean save) {
        U data = getNullable(key);
        if (data == null) return CompletableFuture.completedFuture(false);

        if (save) {
            return save(data).thenApply(result -> {
                this.dataCache.remove(key);
                return result;
            });
        } else {
            this.dataCache.remove(key);
            return CompletableFuture.completedFuture(true);
        }

    }

    public @NotNull CompletableFuture<Boolean> modify(@NotNull K key, @NotNull Consumer<U> consumer) {
        U cached = getNullable(key);
        if (cached != null) {
            return CompletableFuture.supplyAsync(() -> {
                consumer.accept(cached);
                return true;
            }, executor);
        } else {
            return load(key, false).thenApply((data) -> {
                consumer.accept(data);
                return data;
            }).thenCompose(this::save);
        }
    }

    public <V> @NotNull CompletableFuture<V> peek(@NotNull K key, @NotNull Function<U, V> function) {
        U cached = getNullable(key);
        if (cached != null) {
            return CompletableFuture.supplyAsync(() -> function.apply(cached), executor);
        } else {
            return load(key, false).thenApply(function);
        }
    }

    public @NotNull CompletableFuture<Map<K, U>> loadOnline(@NotNull Function<Player, ? extends K> function) {
        return loadGroup(Bukkit.getOnlinePlayers(), function, OfflinePlayer::isOnline);
    }

    public <T> @NotNull CompletableFuture<Map<K, U>> loadGroup(@NotNull Collection<? extends T> users,
                                                               @NotNull Function<? super T, ? extends K> function,
                                                               @NotNull Predicate<T> cacheCondition) {
        CompletableFuture<Map<K, U>> task = CompletableFuture.completedFuture(new ConcurrentHashMap<>());
        if (users.isEmpty()) return task;

        Map<K, T> usersMap = users.stream().collect(Collectors.toMap(function, Function.identity()));
        for (Map.Entry<K, T> entry : usersMap.entrySet()) {
            K key = entry.getKey();
            T user = entry.getValue();
            task = task.thenCombine(
                    load(key, () -> cacheCondition.test(user)),
                    (map, result) -> {
                        map.put(key, result);
                        return map;
                    }
            );
        }

        return task.thenApply(Collections::unmodifiableMap);
    }

    public @NotNull CompletableFuture<Map<K, U>> loadGroup(@NotNull Collection<K> allKeys,
                                                           @NotNull Predicate<K> cacheCondition) {
        return loadGroup(allKeys, Function.identity(), cacheCondition);
    }

    public @NotNull CompletableFuture<Map<K, U>> loadGroup(@NotNull Collection<K> allKeys) {
        return loadGroup(allKeys, (v) -> false);
    }

    public void saveAll() {
        if (getDataCache().isEmpty()) return;
        for (U u : getDataCache().values()) {
            try {
                saveData(u);
            } catch (Exception e) {
                getPlugin().error("保存用户 " + serializeKey(u.getKey()) + " 数据失败，请检查相关配置！");
                e.printStackTrace();
            }
        }
    }

    public int unloadAll(boolean save) {
        if (save) saveAll();
        int size = getDataCache().size();
        getDataCache().clear();
        return size;
    }


}
