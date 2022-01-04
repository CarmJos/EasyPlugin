package cc.carm.lib.easyplugin.configuration.file;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class FileConfig {

	public static Supplier<FileConfig> pluginConfiguration = null;
	public static Supplier<FileConfig> messageConfiguration = null;

	@Nullable
	public static FileConfig getPluginConfiguration() {
		return pluginConfiguration == null ? null : pluginConfiguration.get();
	}

	@Nullable
	public static FileConfig getMessageConfiguration() {
		return messageConfiguration == null ? null : messageConfiguration.get();
	}

	private long updateTime;

	private final JavaPlugin plugin;
	private final String fileName;


	private File file;
	private FileConfiguration config;

	public FileConfig(final JavaPlugin plugin) {
		this(plugin, "config.yml");
	}

	public FileConfig(final JavaPlugin plugin, final String name) {
		this.plugin = plugin;
		this.fileName = name;
		initFile();
	}

	private void initFile() {
		this.updateTime = System.currentTimeMillis();
		this.file = new File(plugin.getDataFolder(), fileName);
		if (!this.file.exists()) {
			if (!this.file.getParentFile().exists()) {
				boolean success = this.file.getParentFile().mkdirs();
			}
			plugin.saveResource(fileName, true);
		}
		this.config = YamlConfiguration.loadConfiguration(this.file);
	}

	public File getFile() {
		return file;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void save() {
		try {
			getConfig().save(getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		this.updateTime = System.currentTimeMillis();
		if (getFile().exists()) {
			this.config = YamlConfiguration.loadConfiguration(getFile());
		} else {
			initFile();
		}
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public boolean isExpired(long time) {
		return getUpdateTime() > time;
	}
}
