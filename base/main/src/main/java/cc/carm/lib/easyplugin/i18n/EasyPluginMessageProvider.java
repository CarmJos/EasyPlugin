package cc.carm.lib.easyplugin.i18n;

import cc.carm.lib.easyplugin.utils.ColorParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public interface EasyPluginMessageProvider {

    EasyPluginMessageProvider ZH_CN = new zh_CN();
    EasyPluginMessageProvider EN_US = new en_US();

    String loading(Plugin plugin);

    String loaded(Plugin plugin, long startMillis);

    String enabling(Plugin plugin);

    String enableSuccess(Plugin plugin, long startMillis);

    String enableFailure(Plugin plugin, long startMillis);

    String disabling(Plugin plugin);

    String disabled(Plugin plugin, long startMillis);

    default void print(@NotNull Plugin plugin, @Nullable String prefix, @Nullable String... messages) {
        Arrays.stream(messages)
                .map(message -> "[" + plugin.getName() + "] " + (prefix == null ? "" : prefix) + message)
                .map(ColorParser::parse)
                .forEach(message -> Bukkit.getConsoleSender().sendMessage(message));
    }

    class zh_CN implements EasyPluginMessageProvider {

        @Override
        public String loading(Plugin plugin) {
            return "&f" + plugin.getName() + " " + plugin.getDescription().getVersion() + " 开始加载...";
        }

        @Override
        public String loaded(Plugin plugin, long startMillis) {
            return "&f加载完成 ，共耗时 " + (System.currentTimeMillis() - startMillis) + " ms 。";
        }

        @Override
        public String enabling(Plugin plugin) {
            return "&f" + plugin.getName() + " " + plugin.getDescription().getVersion() + " 开始启动...";
        }

        @Override
        public String enableSuccess(Plugin plugin, long startMillis) {
            return "&a启用完成! &f共耗时 " + (System.currentTimeMillis() - startMillis) + " ms 。";
        }

        @Override
        public String enableFailure(Plugin plugin, long startMillis) {
            return "&c启用失败! &f已耗时 " + (System.currentTimeMillis() - startMillis) + " ms 。";
        }

        @Override
        public String disabling(Plugin plugin) {
            return "&f" + plugin.getName() + " " + plugin.getDescription().getVersion() + " 开始卸载...";
        }

        @Override
        public String disabled(Plugin plugin, long startMillis) {
            return "&f卸载完成! 共耗时 " + (System.currentTimeMillis() - startMillis) + " ms 。";
        }
    }

    class en_US implements EasyPluginMessageProvider {

        @Override
        public String loading(Plugin plugin) {
            return "&f" + plugin.getName() + " " + plugin.getDescription().getVersion() + " loading...";
        }

        @Override
        public String loaded(Plugin plugin, long startMillis) {
            return "&fLoaded after " + (System.currentTimeMillis() - startMillis) + " ms.";
        }

        @Override
        public String enabling(Plugin plugin) {
            return "&f" + plugin.getName() + " " + plugin.getDescription().getVersion() + " enabling...";
        }

        @Override
        public String enableSuccess(Plugin plugin, long startMillis) {
            return "&aEnabled successfully!&f Cost " + (System.currentTimeMillis() - startMillis) + " ms.";
        }

        @Override
        public String enableFailure(Plugin plugin, long startMillis) {
            return "&cEnabled failed after " + (System.currentTimeMillis() - startMillis) + " ms.";
        }

        @Override
        public String disabling(Plugin plugin) {
            return "&f" + plugin.getName() + " " + plugin.getDescription().getVersion() + " begin to shutdown...";
        }

        @Override
        public String disabled(Plugin plugin, long startMillis) {
            return "&fShutdown successfully, cost " + (System.currentTimeMillis() - startMillis) + " ms.";
        }
    }

}
