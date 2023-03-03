package cc.carm.lib.easyplugin.updatechecker;

import cc.carm.lib.githubreleases4j.GithubReleases4J;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GHUpdateChecker {

    protected static final @NotNull Pattern GH_URL_PATTERN = Pattern.compile("^https?://github.com/([A-Za-z\\d-_]+)/([^/]*?)/?");

    public static @NotNull GHUpdateChecker of(@NotNull Logger logger, @NotNull String owner, @NotNull String repo) {
        return new GHUpdateChecker(logger, owner, repo);
    }

    public static @NotNull GHUpdateChecker of(@NotNull Plugin plugin) {
        return new GHUpdateChecker(plugin.getLogger(), getGithubOwner(plugin), plugin.getName());
    }

    public static @NotNull Runnable runner(@NotNull Logger logger,
                                           @NotNull String owner, @NotNull String repo,
                                           @NotNull String currentVersion) {
        return of(logger, owner, repo).createRunner(currentVersion);
    }

    public static @NotNull Runnable runner(@NotNull Plugin plugin) {
        return of(plugin).createRunner(plugin.getDescription().getVersion());
    }

    public static @NotNull BukkitTask run(@NotNull Plugin plugin) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runner(plugin));
    }

    protected final @NotNull Logger logger;
    protected final @NotNull String owner;
    protected final @NotNull String repo;

    public GHUpdateChecker(@NotNull Logger logger, @NotNull String owner, @NotNull String repo) {
        this.logger = logger;
        this.owner = owner;
        this.repo = repo;
    }

    public void checkUpdate(@NotNull String currentVersion) {
        Integer behindVersions = GithubReleases4J.getVersionBehind(owner, repo, currentVersion);
        String downloadURL = GithubReleases4J.getReleasesURL(owner, repo);

        if (behindVersions == null) {
            this.logger.severe("检查更新失败，请您定期查看插件是否更新，避免安全问题。");
            this.logger.severe("下载地址 " + downloadURL);
        } else if (behindVersions == 0) {
            this.logger.info("检查完成，当前已是最新版本。");
        } else if (behindVersions > 0) {
            this.logger.info("发现新版本! 目前已落后 " + behindVersions + " 个版本。");
            this.logger.info("最新版下载地址 " + downloadURL);
        } else {
            this.logger.severe("检查更新失败! 当前版本未知，请您使用原生版本以避免安全问题。");
            this.logger.severe("最新版下载地址 " + downloadURL);
        }
    }

    public Runnable createRunner(@NotNull String currentVersion) {
        return () -> checkUpdate(currentVersion);
    }

    protected static String getGithubOwner(Plugin plugin) {
        // 首先，尝试从插件提供的网址中获取OWNER
        String websiteOwner = getGithubOwner(plugin.getDescription().getWebsite());
        if (websiteOwner != null && !websiteOwner.isEmpty()) return websiteOwner;

        // 如果插件提供的网址中没有，则尝试获取插件的首位作者名
        List<String> authors = plugin.getDescription().getAuthors();
        if (!authors.isEmpty()) return authors.get(0);

        // 再没有的话只能返回插件的名称了
        return plugin.getName();
    }

    protected static String getGithubOwner(String url) {
        Matcher matcher = GH_URL_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

}