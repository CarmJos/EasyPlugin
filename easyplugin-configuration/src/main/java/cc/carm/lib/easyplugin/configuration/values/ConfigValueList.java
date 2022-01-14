package cc.carm.lib.easyplugin.configuration.values;


import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.file.FileConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigValueList<V> extends FileConfigValue {

	private final @NotNull Class<V> clazz;

	@Nullable V[] defaultValue;

	public ConfigValueList(@NotNull String sectionName,
						   @NotNull Class<V> clazz) {
		this(sectionName, clazz, null);
	}

	public ConfigValueList(@NotNull String sectionName,
						   @NotNull Class<V> clazz,
						   @Nullable V[] defaultValue) {
		this(null, sectionName, clazz, defaultValue);
	}

	public ConfigValueList(@Nullable Supplier<FileConfig> provider,
						   @NotNull String sectionName,
						   Class<V> clazz) {
		this(provider, sectionName, clazz, null);
	}

	public ConfigValueList(@Nullable Supplier<FileConfig> provider,
						   @NotNull String sectionName,
						   @NotNull Class<V> clazz,
						   @Nullable V[] defaultValue) {
		super(provider, sectionName);
		this.clazz = clazz;
		this.defaultValue = defaultValue;
	}

	public @NotNull ArrayList<V> get() {
		return getConfigOptional()
				.map(configuration -> configuration.getList(getSectionName()))
				.map(list -> list.stream()
						.map(o -> castValue(o, this.clazz))
						.filter(Objects::nonNull)
						.collect(Collectors.toCollection(ArrayList::new)))
				.orElse(getDefaultList());
	}

	public @NotNull ArrayList<V> getDefaultList() {
		return defaultValue == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(defaultValue));

	}

	public void set(@Nullable ArrayList<V> value) {
		setIfPresent(value, true);
	}


}
