package cc.carm.lib.easyplugin.configuration.cast;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.file.FileConfigCachedValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class ConfigStringCast<V> extends FileConfigCachedValue<V> {

	@NotNull Function<String, V> valueCast;
	@Nullable V defaultValue;

	public ConfigStringCast(@NotNull String configSection,
							@NotNull Function<String, V> valueCast) {
		this(configSection, valueCast, null);
	}

	public ConfigStringCast(@NotNull String configSection,
							@NotNull Function<String, V> valueCast,
							@Nullable V defaultValue) {
		this(null, configSection, valueCast, defaultValue);
	}

	public ConfigStringCast(@Nullable FileConfig source, @NotNull String sectionName,
							@NotNull Function<String, V> valueCast,
							@Nullable V defaultValue) {
		super(source, sectionName);
		this.valueCast = valueCast;
		this.defaultValue = defaultValue;
	}

	public @Nullable V get() {
		V cached = getCachedValue();
		if (cached != null && !isExpired()) {
			return cached;
		} else {
			return getConfigOptional()
					.map(config -> config.getString(getSectionName()))
					.map(s -> updateCache(valueCast.apply(s)))
					.orElse(defaultValue);
		}
	}

	public @NotNull Optional<V> getOptional() {
		return Optional.ofNullable(get());
	}

	public void set(@Nullable String value) {
		setIfPresent(value, true);
	}

}
