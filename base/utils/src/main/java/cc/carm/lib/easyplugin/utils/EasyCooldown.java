package cc.carm.lib.easyplugin.utils;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <a href="https://gist.github.com/CarmJos/402cb5aad0ec14ab25c2fa0d21571703">Easy cooldown time utils.</a>
 *
 * @param <P> Cooldown key provider
 * @param <K> Cooldown key
 * @author CarmJos
 */
public class EasyCooldown<P, K> {

    protected final NumberFormat numberFormatter;

    protected final @NotNull Map<K, Long> cooldown = new HashMap<>();
    protected final @NotNull Function<P, K> providerToKey;

    protected long defaultDuration;

    public EasyCooldown(@NotNull Function<P, K> providerToKey) {
        this(defaultFormatter(), providerToKey, 1000L);
    }

    public EasyCooldown(@NotNull NumberFormat numberFormatter,
                        @NotNull Function<P, K> providerToKey) {
        this(numberFormatter, providerToKey, 1000L);
    }

    public EasyCooldown(@NotNull NumberFormat numberFormatter,
                        @NotNull Function<P, K> providerToKey,
                        long defaultDuration) {
        this.numberFormatter = numberFormatter;
        this.providerToKey = providerToKey;
        this.defaultDuration = defaultDuration;
    }

    public long getCooldown(@NotNull P provider) {
        Long time = this.cooldown.get(this.providerToKey.apply(provider));
        if (time == null || time < 0) return 0;

        long duration = getDuration(provider);
        if (duration <= 0) return 0;

        long past = System.currentTimeMillis() - time;
        return duration - past;
    }

    public @NotNull String getCooldownSeconds(@NotNull P provider) {
        return formatSeconds(getCooldown(provider));
    }

    public void updateTime(@NotNull P provider) {
        this.cooldown.put(this.providerToKey.apply(provider), System.currentTimeMillis());
    }

    public void clear(@NotNull P provider) {
        clearCooldown(this.providerToKey.apply(provider));
    }

    public void clearCooldown(@NotNull K key) {
        this.cooldown.remove(key);
    }

    public boolean isCoolingDown(@NotNull P provider) {
        return getCooldown(provider) > 0;
    }

    public long getDuration(@NotNull P provider) {
        return this.defaultDuration;
    }

    public @NotNull String formatSeconds(long cooldownMillis) {
        return numberFormatter.format((double) cooldownMillis / 1000D);
    }

    public static NumberFormat createFormatter(@NotNull Consumer<NumberFormat> consumer) {
        NumberFormat format = NumberFormat.getInstance();
        consumer.accept(format);
        return format;
    }

    public static NumberFormat defaultFormatter() {
        return createFormatter((f) -> f.setMaximumFractionDigits(2));
    }

}
