package cc.carm.lib.easyplugin.configuration.impl;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ConfigSectionCast<V> {

	@Nullable FileConfig source;

	@NotNull String configSection;
	@NotNull Function<ConfigurationSection, V> valueCast;
	@Nullable V defaultValue;

	V valueCache;
	long updateTime;

	public ConfigSectionCast(@NotNull String configSection, @NotNull Function<ConfigurationSection, V> valueCast) {
		this(configSection, valueCast, null);
	}

	public ConfigSectionCast(@NotNull String configSection,
							 @NotNull Function<ConfigurationSection, V> valueCast,
							 @Nullable V defaultValue) {
		this(null, configSection, valueCast, defaultValue);
	}

	public ConfigSectionCast(@Nullable FileConfig source, @NotNull String configSection,
							 @NotNull Function<ConfigurationSection, V> valueCast,
							 @Nullable V defaultValue) {
		this.source = source;
		this.configSection = configSection;
		this.valueCast = valueCast;
		this.defaultValue = defaultValue;
	}


	public @Nullable V get() {
		FileConfig source = getSource();
		if (source == null) return defaultValue;
		
		if (valueCache != null && !source.isExpired(this.updateTime)) return valueCache;
		if (!source.getConfig().contains(this.configSection)) return defaultValue;
		try {
			V finalValue = this.valueCast.apply(source.getConfig().getConfigurationSection(this.configSection));
			if (finalValue != null) {
				this.valueCache = finalValue;
				this.updateTime = System.currentTimeMillis();
				return finalValue;
			} else {
				return defaultValue;
			}
		} catch (Exception ignore) {
			return defaultValue;
		}
	}

	public void set(ConfigurationSection section) {

	}

	public void save() {
		if (getSource() != null) getSource().save();
	}

	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getPluginConfiguration() : source;
	}

}
