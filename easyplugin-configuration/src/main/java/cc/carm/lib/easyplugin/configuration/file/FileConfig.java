package cc.carm.lib.easyplugin.configuration.file;


import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.function.Supplier;

@SuppressWarnings("ResultOfMethodCallIgnored")
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

    protected long updateTime;

    protected final JavaPlugin plugin;
    protected final File fileFolder;
    protected final String fileName;
    protected final String resourcePath;

    protected File file;
    protected FileConfiguration config;

    public FileConfig(@NotNull JavaPlugin plugin) throws IOException {
        this(plugin, "config.yml");
    }

    public FileConfig(@NotNull JavaPlugin plugin,
                      @NotNull String fileName) throws IOException {
        this(plugin, fileName, fileName);
    }

    public FileConfig(@NotNull JavaPlugin plugin, @NotNull String resourcePath,
                      @NotNull String fileName) throws IOException {
        this(plugin, resourcePath, plugin.getDataFolder(), fileName);
    }

    public FileConfig(@NotNull JavaPlugin plugin, @NotNull String resourcePath,
                      @NotNull File fileFolder, @NotNull String fileName) throws IOException {
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        this.fileFolder = fileFolder;
        this.fileName = fileName;
        initFile();
    }

    protected void initFile() throws IOException {
        if (!getFileFolder().exists()) getFileFolder().mkdirs();
        this.file = new File(getFileFolder(), fileName);

        if (!file.exists()) {
            InputStream resourceStream = plugin.getResource(resourcePath);
            if (resourceStream == null) {
                throw new IOException("The resource " + resourcePath + " cannot find in " + plugin.getName() + " !");
            }

            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];

            int readBytes;
            while ((readBytes = resourceStream.read(buffer)) > 0) {
                out.write(buffer, 0, readBytes);
            }

            out.close();
            resourceStream.close();

            ConfigUpdater.update(plugin, resourcePath, file); // Save comments
        }

        this.updateTime = System.currentTimeMillis();
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public File getFileFolder() {
        return fileFolder;
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void save() throws IOException {
        getConfig().save(getFile());
        ConfigUpdater.update(plugin, resourcePath, file); // Save comments
    }

    public void reload() throws IOException {
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
