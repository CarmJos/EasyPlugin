package cc.carm.lib.easyplugin.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleCompleter {

    public static @NotNull List<String> none() {
        return ImmutableList.of();
    }

    public static @NotNull List<String> objects(@NotNull String input, Collection<?> objects) {
        return objects(input, objects.size(), objects);
    }

    public static @NotNull List<String> objects(@NotNull String input, int limit, Collection<?> objects) {
        return objects(input, limit, objects.stream());
    }

    public static @NotNull List<String> objects(@NotNull String input, Stream<?> stream) {
        return objects(input, 20, stream);
    }

    public static @NotNull List<String> objects(@NotNull String input, int limit, Stream<?> stream) {
        return stream.filter(Objects::nonNull).map(Object::toString)
                .filter(s -> StringUtil.startsWithIgnoreCase(s, input))
                .limit(Math.max(0, limit)).collect(Collectors.toList());
    }

    public static @NotNull List<String> text(@NotNull String input, String... texts) {
        return text(input, texts.length, texts);
    }

    public static @NotNull List<String> text(@NotNull String input, int limit, String... texts) {
        return text(input, limit, Arrays.asList(texts));
    }

    public static @NotNull List<String> text(@NotNull String input, Collection<String> texts) {
        return text(input, texts.size(), texts);
    }

    public static @NotNull List<String> text(@NotNull String input, int limit, Collection<String> texts) {
        return objects(input, limit, texts);
    }

    public static @NotNull List<String> onlinePlayers(@NotNull String input) {
        return onlinePlayers(input, 10);
    }

    public static @NotNull List<String> onlinePlayers(@NotNull String input, int limit) {
        return objects(input, limit, Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName));
    }

    public static @NotNull List<String> allPlayers(@NotNull String input) {
        return allPlayers(input, 10);
    }

    public static @NotNull List<String> allPlayers(@NotNull String input, int limit) {
        return objects(input, limit, Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName));
    }

    public static @NotNull List<String> worlds(@NotNull String input) {
        return worlds(input, 10);
    }

    public static @NotNull List<String> worlds(@NotNull String input, int limit) {
        return objects(input, limit, Bukkit.getWorlds().stream().map(World::getName));
    }

    public static @NotNull List<String> materials(@NotNull String input) {
        return materials(input, 10);
    }

    public static @NotNull List<String> materials(@NotNull String input, int limit) {
        return objects(input, limit, Arrays.stream(Material.values()).map(Enum::name));
    }

    public static @NotNull List<String> effects(@NotNull String input) {
        return effects(input, 10);
    }

    public static @NotNull List<String> effects(@NotNull String input, int limit) {
        return objects(input, limit, Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName));
    }

    public static @NotNull List<String> enchantments(@NotNull String input) {
        return effects(input, 10);
    }

    @SuppressWarnings("deprecation")
    public static @NotNull List<String> enchantments(@NotNull String input, int limit) {
        return objects(input, limit, Arrays.stream(Enchantment.values()).map(Enchantment::getName));
    }

}
