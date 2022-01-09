package cc.carm.lib.easyplugin.configuration.language;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MessagesConfig extends FileConfig {

    public MessagesConfig(@NotNull JavaPlugin plugin) throws IOException {
        this(plugin, "messages.yml");
    }

    public MessagesConfig(@NotNull JavaPlugin plugin, @NotNull String fileName) throws IOException {
        this(plugin, plugin.getDataFolder(), fileName);
    }

    public MessagesConfig(@NotNull JavaPlugin plugin, @NotNull File fileFolder, @NotNull String fileName) throws IOException {
        super(plugin, fileName, fileFolder, fileName);
    }

    @Override
    public void initFile() throws IOException {
        if (!getFileFolder().exists()) getFileFolder().mkdirs();
        this.file = new File(getFileFolder(), fileName);
        if (!file.exists()) file.createNewFile();
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public void save() throws IOException {
        getConfig().save(getFile());
    }

}
