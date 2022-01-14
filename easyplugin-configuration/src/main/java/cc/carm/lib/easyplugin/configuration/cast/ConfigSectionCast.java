package cc.carm.lib.easyplugin.configuration.cast;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.file.FileConfigCachedValue;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigSectionCast<V> extends FileConfigCachedValue<V> {

	@NotNull Function<ConfigurationSection, V> valueCast;
	@Nullable V defaultValue;

	public ConfigSectionCast(@NotNull String configSection,
							 @NotNull Function<ConfigurationSection, V> valueCast) {
		this(configSection, valueCast, null);
	}

	public ConfigSectionCast(@NotNull String sectionName,
							 @NotNull Function<ConfigurationSection, V> valueCast,
							 @Nullable V defaultValue) {
		this((Supplier<FileConfig>) null, sectionName, valueCast, defaultValue);
	}

	public ConfigSectionCast(@Nullable FileConfig source, @NotNull String sectionName,
							 @NotNull Function<ConfigurationSection, V> valueCast,
							 @Nullable V defaultValue) {
		this(source == null ? null : () -> source, sectionName, valueCast, defaultValue);
	}

	public ConfigSectionCast(@Nullable Supplier<FileConfig> provider, @NotNull String sectionName,
							 @NotNull Function<ConfigurationSection, V> valueCast,
							 @Nullable V defaultValue) {
		super(provider, sectionName);
		this.valueCast = valueCast;
		this.defaultValue = defaultValue;
	}


	public @Nullable V get() {
		V cached = getCachedValue();
		if (cached != null && !isExpired()) {
			return cached;
		} else {
			return getConfigOptional()
					.map(config -> config.getConfigurationSection(getSectionName()))
					.map(section -> updateCache(valueCast.apply(section)))
					.orElse(defaultValue);
		}
	}

	public @NotNull Optional<V> getOptional() {
		return Optional.ofNullable(get());
	}


	public void set(ConfigurationSection section) {
	}

}
