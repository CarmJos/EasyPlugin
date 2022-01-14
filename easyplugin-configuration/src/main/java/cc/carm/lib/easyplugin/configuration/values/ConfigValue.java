package cc.carm.lib.easyplugin.configuration.values;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.file.FileConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ConfigValue<V> extends FileConfigValue {
	private final @NotNull Class<V> clazz;
	@Nullable V defaultValue;

	public ConfigValue(@NotNull String sectionName,
					   @NotNull Class<V> clazz) {
		this(sectionName, clazz, null);
	}

	public ConfigValue(@NotNull String sectionName,
					   @NotNull Class<V> clazz,
					   @Nullable V defaultValue) {
		this((Supplier<FileConfig>) null, sectionName, clazz, defaultValue);
	}

	public ConfigValue(@Nullable FileConfig source, @NotNull String sectionName,
					   @NotNull Class<V> clazz,
					   @Nullable V defaultValue) {
		this(source == null ? null : () -> source, sectionName, clazz, defaultValue);
	}

	public ConfigValue(@Nullable Supplier<FileConfig> provider, @NotNull String sectionName,
					   @NotNull Class<V> clazz,
					   @Nullable V defaultValue) {
		super(provider, sectionName);
		this.clazz = clazz;
		this.defaultValue = defaultValue;
	}

	public V get() {
		return getConfigOptional()
				.map(config -> {
					if (config.contains(getSectionName())) {
						return castValue(config.get(getSectionName()), clazz, this.defaultValue);
					} else {
						return setDefault(); // 如果没有默认值，就把配置写进去，便于配置
					}
				})
				.orElse(defaultValue);
	}

	public @NotNull Optional<V> getOptional() {
		return Optional.ofNullable(get());
	}

	public void set(@Nullable V value) {
		setIfPresent(value, true);
	}

	public V setDefault() {
		if (this.defaultValue != null) set(this.defaultValue);
		return this.defaultValue;
	}
}
