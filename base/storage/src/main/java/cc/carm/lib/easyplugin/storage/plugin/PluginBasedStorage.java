package cc.carm.lib.easyplugin.storage.plugin;

import cc.carm.lib.easyplugin.storage.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class PluginBasedStorage<K, T> implements DataStorage<K, T> {

    protected Plugin dependPlugin;

    public PluginBasedStorage(String dependPluginName) {
        this(Bukkit.getPluginManager().getPlugin(dependPluginName));
    }

    public PluginBasedStorage(Plugin dependPlugin) {
        this.dependPlugin = dependPlugin;
    }

    @Override
    public void initialize() throws NullPointerException {
        if (dependPlugin == null) {
            throw new NullPointerException("该存储类型依赖的插件不存在，请检查配置文件");
        }
    }

    public Plugin getDependPlugin() {
        return dependPlugin;
    }


}
