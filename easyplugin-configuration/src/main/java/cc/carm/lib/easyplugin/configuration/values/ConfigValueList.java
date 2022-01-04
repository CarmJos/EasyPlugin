package cc.carm.lib.easyplugin.configuration.values;


import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigValueList<V> {

	protected @Nullable FileConfig source;

	private final @NotNull String configSection;
	private final @NotNull Class<V> clazz;

	@Nullable V[] defaultValue;

	public ConfigValueList(@NotNull String configSection, @NotNull Class<V> clazz) {
		this(configSection, clazz, null);
	}

	public ConfigValueList(@NotNull String configSection, @NotNull Class<V> clazz, @Nullable V[] defaultValue) {
		this(null, configSection, clazz, defaultValue);
	}

	public ConfigValueList(@Nullable FileConfig configuration, @NotNull String configSection, Class<V> clazz) {
		this(configuration, configSection, clazz, null);
	}

	public ConfigValueList(@Nullable FileConfig configuration, @NotNull String configSection,
						   @NotNull Class<V> clazz, @Nullable V[] defaultValue) {
		this.source = configuration;
		this.configSection = configSection;
		this.clazz = clazz;
		this.defaultValue = defaultValue;
	}

	public @NotNull ArrayList<V> get() {
		FileConfig source = getSource();
		if (source == null) return new ArrayList<>();

		List<?> list = source.getConfig().getList(this.configSection);
		if (list == null) {
			if (defaultValue != null) {
				return new ArrayList<>(Arrays.asList(defaultValue));
			} else {
				return new ArrayList<>();
			}
		} else {
			ArrayList<V> result = new ArrayList<>();

			for (Object object : list) {
				if (this.clazz.isInstance(object)) {
					result.add(this.clazz.cast(object));
				}
			}
			return result;
		}
	}

	public void set(@Nullable ArrayList<V> value) {
		FileConfig source = getSource();
		if (source == null) return;
		source.getConfig().set(this.configSection, value);
		this.save();
	}

	public void save() {
		if (getSource() != null) getSource().save();
	}

	public @Nullable FileConfig getSource() {
		return this.source == null ? FileConfig.getPluginConfiguration() : this.source;
	}

}
