package cc.carm.lib.easyplugin.configuration.values;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigValue<V> {

	protected @Nullable FileConfig source;

	private final @NotNull String configSection;
	private final @NotNull Class<V> clazz;
	@Nullable V defaultValue;

	public ConfigValue(@NotNull String configSection, @NotNull Class<V> clazz) {
		this(configSection, clazz, null);
	}

	public ConfigValue(@NotNull String configSection, @NotNull Class<V> clazz, @Nullable V defaultValue) {
		this(null, configSection, clazz, defaultValue);
	}

	public ConfigValue(@Nullable FileConfig source, @NotNull String configSection,
					   @NotNull Class<V> clazz, @Nullable V defaultValue) {
		this.source = source;
		this.configSection = configSection;
		this.clazz = clazz;
		this.defaultValue = defaultValue;
	}

	public V get() {
		FileConfig source = getSource();
		if (source == null) return this.defaultValue;

		if (source.getConfig().contains(this.configSection)) {
			Object val = source.getConfig().get(this.configSection, this.defaultValue);
			return this.clazz.isInstance(val) ? this.clazz.cast(val) : this.defaultValue;
		} else {
			// 如果没有默认值，就把配置写进去，便于配置
			return setDefault();
		}
	}

	public void set(@Nullable V value) {
		FileConfig source = getSource();
		if (source == null) return;

		source.getConfig().set(this.configSection, value);
		source.save();
	}

	public void save() {
		if (getSource() != null) getSource().save();
	}

	public V setDefault() {
		set(this.defaultValue);
		return this.defaultValue;
	}

	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getPluginConfiguration() : source;
	}
}
