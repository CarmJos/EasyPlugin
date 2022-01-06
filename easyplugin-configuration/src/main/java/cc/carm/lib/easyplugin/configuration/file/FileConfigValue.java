package cc.carm.lib.easyplugin.configuration.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class FileConfigValue {

	protected @Nullable FileConfig source;
	private final @NotNull String sectionName;

	public FileConfigValue(@NotNull String sectionName) {
		this(null, sectionName);
	}

	public FileConfigValue(@Nullable FileConfig source, @NotNull String sectionName) {
		this.source = source;
		this.sectionName = sectionName;
	}

	public @NotNull String getSectionName() {
		return sectionName;
	}

	public void save() {
		getSourceOptional().ifPresent(FileConfig::save);
	}

	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getPluginConfiguration() : source;
	}

	public Optional<FileConfig> getSourceOptional() {
		return Optional.ofNullable(getSource());
	}

	public Optional<FileConfiguration> getConfigOptional() {
		return getSourceOptional().map(FileConfig::getConfig);
	}

	public static @Nullable <V> V castValue(@Nullable Object val, @NotNull Class<V> clazz) {
		return castValue(val, clazz, null);
	}

	public static @Nullable <V> V castValue(@Nullable Object val, @NotNull Class<V> clazz, @Nullable V defaultValue) {
		return clazz.isInstance(val) ? clazz.cast(val) : defaultValue;
	}

}
