package cc.carm.lib.easyplugin.user;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

public interface UserDataRegistry<K, U extends UserData<K>> {

    void shutdown();

    @NotNull Logger getLogger();

    @NotNull Map<K, U> cache();

    default String serializeKey(@NotNull K key) {
        return key.toString();
    }

    default @NotNull CompletableFuture<U> load(@NotNull K key) {
        return load(key, false);
    }

    default @NotNull CompletableFuture<U> load(@NotNull K key, boolean cache) {
        return load(key, () -> cache);
    }

    @Unmodifiable
    default @NotNull Set<U> list() {
        return ImmutableSet.copyOf(cache().values());
    }

    default @NotNull U get(@NotNull K key) {
        return Optional.ofNullable(getNullable(key)).orElseThrow(() -> new NullPointerException("User " + key + " not found."));
    }

    default @Nullable U getNullable(@NotNull K key) {
        return cache().get(key);
    }

    default @NotNull Optional<@Nullable U> getOptional(@NotNull K key) {
        return Optional.ofNullable(getNullable(key));
    }

    @NotNull CompletableFuture<U> load(@NotNull K key, @NotNull Supplier<Boolean> cacheCondition);

    @NotNull CompletableFuture<Boolean> save(@NotNull U user);

    default @NotNull CompletableFuture<Boolean> unload(@NotNull K key) {
        return unload(key, true);
    }

    @NotNull CompletableFuture<Boolean> unload(@NotNull K key, boolean save);

    @NotNull CompletableFuture<Boolean> modify(@NotNull K key, @NotNull Consumer<U> consumer);

    <V> @NotNull CompletableFuture<V> peek(@NotNull K key, @NotNull Function<U, V> function);

    default @NotNull CompletableFuture<Map<K, U>> loadOnline(@NotNull Function<Player, ? extends K> function) {
        return loadGroup(Bukkit.getOnlinePlayers(), function, OfflinePlayer::isOnline);
    }

    <T> @NotNull CompletableFuture<Map<K, U>> loadGroup(@NotNull Collection<? extends T> users,
                                                        @NotNull Function<? super T, ? extends K> function,
                                                        @NotNull Predicate<T> cacheCondition);

    default @NotNull CompletableFuture<Map<K, U>> loadGroup(@NotNull Collection<K> allKeys,
                                                            @NotNull Predicate<K> cacheCondition) {
        return loadGroup(allKeys, Function.identity(), cacheCondition);
    }

    default @NotNull CompletableFuture<Map<K, U>> loadGroup(@NotNull Collection<K> allKeys) {
        return loadGroup(allKeys, (v) -> false);
    }

    void saveAll();

    int unloadAll(boolean save);
}
