package cc.carm.lib.easyplugin.storage.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class YAMLBasedStorage<K, T> extends FileBasedStorage<K, T> {

    protected FileConfiguration configuration;

    public YAMLBasedStorage(String fileName) {
        super(fileName);
    }

    protected @NotNull FileConfiguration initializeConfiguration(@NotNull File parentFolder) throws Exception {
        return this.configuration = YamlConfiguration.loadConfiguration(initializeFile(parentFolder));
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }
}
