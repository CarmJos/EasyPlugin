package cc.carm.lib.easyplugin.configuration.values;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.file.FileConfigCachedValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigValueMap<K, V> extends FileConfigCachedValue<Map<K, V>> {

	@NotNull Function<String, K> keyCast;
	@NotNull Class<V> valueClazz;

	public ConfigValueMap(@NotNull String sectionName, @NotNull Function<String, K> keyCast,
						  @NotNull Class<V> valueClazz) {
		this((Supplier<FileConfig>) null, sectionName, keyCast, valueClazz);
	}


	public ConfigValueMap(@Nullable FileConfig source, @NotNull String sectionName,
						  @NotNull Function<String, K> keyCast, @NotNull Class<V> valueClazz) {
		this(source == null ? null : () -> source, sectionName, keyCast, valueClazz);
	}

	public ConfigValueMap(@Nullable Supplier<FileConfig> provider, @NotNull String sectionName,
						  @NotNull Function<String, K> keyCast, @NotNull Class<V> valueClazz) {
		super(provider, sectionName);
		this.keyCast = keyCast;
		this.valueClazz = valueClazz;
	}


	@NotNull
	public Map<K, V> get() {
		Map<K, V> cached = getCachedValue();
		if (cached != null && !isExpired()) {
			return cached;
		} else {
			return getConfigOptional()
					.map(config -> config.getConfigurationSection(getSectionName()))
					.map(section -> {
						Map<K, V> result = new LinkedHashMap<>();
						for (String key : section.getKeys(false)) {
							K finalKey = keyCast.apply(key);
							V finalValue = castValue(section.get(key), valueClazz);
							if (finalKey != null && finalValue != null) {
								result.put(finalKey, finalValue);
							}
						}
						return updateCache(result);
					}).orElse(new LinkedHashMap<>());
		}
	}

	public void set(@Nullable Map<K, V> valuesMap) {
		createSection(valuesMap);
	}

}
