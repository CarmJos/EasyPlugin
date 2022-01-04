package cc.carm.lib.easyplugin.configuration.values;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ConfigValueMap<K, V> {

	@Nullable FileConfig source;

	@NotNull String configSection;

	@NotNull Function<String, K> keyCast;
	@NotNull Class<V> valueClazz;

	@Nullable LinkedHashMap<K, V> valueCache;

	long updateTime;

	public ConfigValueMap(@NotNull String configSection, @NotNull Function<String, K> keyCast,
						  @NotNull Class<V> valueClazz) {
		this(null, configSection, keyCast, valueClazz);
	}

	public ConfigValueMap(@Nullable FileConfig configuration, @NotNull String configSection,
						  @NotNull Function<String, K> keyCast, @NotNull Class<V> valueClazz) {
		this.source = configuration;
		this.configSection = configSection;
		this.keyCast = keyCast;
		this.valueClazz = valueClazz;
	}

	public void clearCache() {
		this.valueCache = null;
	}

	@NotNull
	public Map<K, V> get() {
		FileConfig source = getSource();
		if (source == null) return new HashMap<>();

		if (valueCache != null && !getSource().isExpired(this.updateTime)) return valueCache;

		ConfigurationSection section = source.getConfig().getConfigurationSection(this.configSection);
		if (section == null) return new LinkedHashMap<>();

		Set<String> keys = section.getKeys(false);
		if (keys.isEmpty()) return new LinkedHashMap<>();

		else {
			LinkedHashMap<K, V> result = new LinkedHashMap<>();
			for (String key : keys) {
				K finalKey = keyCast.apply(key);
				Object val = section.get(key);
				V finalValue = this.valueClazz.isInstance(val) ? this.valueClazz.cast(val) : null;
				if (finalKey != null && finalValue != null) {
					result.put(finalKey, finalValue);
				}
			}
			this.updateTime = System.currentTimeMillis();
			this.valueCache = result;
			return result;
		}
	}

	public void set(@Nullable HashMap<K, V> valuesMap) {
		FileConfig source = getSource();
		if (source == null) return;
		source.getConfig().createSection(this.configSection, valuesMap);
		source.save();
	}

	public void save() {
		if (getSource() != null) getSource().save();
	}

	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getPluginConfiguration() : source;
	}

}
