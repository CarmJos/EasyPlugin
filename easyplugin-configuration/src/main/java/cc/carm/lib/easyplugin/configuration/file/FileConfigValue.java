package cc.carm.lib.easyplugin.configuration.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class FileConfigValue {

	protected @Nullable Supplier<FileConfig> provider;
	private final @NotNull String sectionName;

	public FileConfigValue(@NotNull String sectionName) {
		this(null, sectionName);
	}

	public FileConfigValue(@Nullable Supplier<FileConfig> provider, @NotNull String sectionName) {
		this.provider = provider;
		this.sectionName = sectionName;
	}

	public @NotNull String getSectionName() {
		return sectionName;
	}

	public void save() {
		getSourceOptional().ifPresent(fileConfig -> {
			try {
				fileConfig.save();
			} catch (Exception ex) {
				fileConfig.getPlugin().getLogger().severe("Could not save the " + fileConfig.getFile() + " .");
				ex.printStackTrace();
			}
		});
	}

	public void setIfPresent(@Nullable Object value, boolean save) {
		getConfigOptional().ifPresent(configuration -> configuration.set(getSectionName(), value));
		if (save) save();
	}

	public void createSection(Map<?, ?> values) {
		getConfigOptional().ifPresent(configuration -> {
			if (values == null) {
				configuration.set(getSectionName(), null);
			} else {
				configuration.createSection(getSectionName(), values);
			}
		});
	}

	public @Nullable FileConfig getSource() {
		return provider == null || provider.get() == null ? defaultSource() : provider.get();
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

	public FileConfig defaultSource() {
		return FileConfig.getPluginConfiguration();
	}

}
