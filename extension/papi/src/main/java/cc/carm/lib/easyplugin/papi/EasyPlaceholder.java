package cc.carm.lib.easyplugin.papi;

import cc.carm.lib.easyplugin.papi.expansion.SectionExpansion;
import cc.carm.lib.easyplugin.papi.expansion.SubExpansion;
import cc.carm.lib.easyplugin.papi.handler.PlaceholderHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EasyPlaceholder extends PlaceholderExpansion {

    protected final @NotNull String plugin;
    protected final @NotNull SectionExpansion rootExpansion;

    protected final @NotNull String name;
    protected final @NotNull String author;
    protected final @NotNull String version;

    protected final boolean persistent;

    public EasyPlaceholder(@NotNull JavaPlugin plugin, @NotNull String rootIdentifier) {
        this(
                plugin.getName(), true, rootIdentifier,
                plugin.getName(), plugin.getDescription().getVersion(),
                String.join(", ", plugin.getDescription().getAuthors())
        );
    }

    public EasyPlaceholder(@NotNull JavaPlugin plugin, @NotNull SectionExpansion rootExpansion) {
        this(
                plugin.getName(), true, rootExpansion,
                plugin.getName(), plugin.getDescription().getVersion(),
                String.join(", ", plugin.getDescription().getAuthors())
        );
    }

    public EasyPlaceholder(@NotNull String plugin, boolean persistent, @NotNull String rootIdentifier,
                           @NotNull String name, @NotNull String version, @NotNull String author) {
        super();
        this.plugin = plugin;
        this.name = name;
        this.author = author;
        this.version = version;
        this.persistent = persistent;
        this.rootExpansion = new SectionExpansion(this, rootIdentifier);
    }

    public EasyPlaceholder(@NotNull String plugin, boolean persistent,
                           @NotNull SectionExpansion rootExpansion,
                           @NotNull String name, @NotNull String version, @NotNull String author) {
        super();
        this.plugin = plugin;
        this.name = name;
        this.author = author;
        this.version = version;
        this.persistent = persistent;
        this.rootExpansion = rootExpansion;
    }

    /**
     * 得到根变量解析器
     *
     * @return 根变量解析器
     */
    public @NotNull SectionExpansion getRootExpansion() {
        return rootExpansion;
    }

    /**
     * 当未找到对应变量处理器时调用
     *
     * @param player 变量解析时的目标玩家，可能为空或不在线。
     * @return 返回给玩家的内容
     */
    public String onErrorParams(@Nullable OfflinePlayer player) {
        return "Wrong params";
    }

    /**
     * 当变量处理器返回 NULL 值时调用
     *
     * @param player 变量解析时的目标玩家，可能为空或不在线。
     * @return 返回给玩家的内容
     */
    public String onNullResponse(@Nullable OfflinePlayer player) {
        return "";
    }

    /**
     * 当变量处理器抛出异常时调用
     *
     * @param player    变量解析时的目标玩家，可能为空或不在线。
     * @param expansion 抛出异常的变量处理器
     * @param exception 异常内容
     * @return 返回给玩家的内容
     */
    public String onException(@Nullable OfflinePlayer player, @NotNull SubExpansion<?> expansion, @NotNull Exception exception) {
        exception.printStackTrace();
        return "Error \"" + expansion.getIdentifier() + "\"";
    }

    /**
     * 处理变量并返回对应内容。
     *
     * @param identifier 该变量的标识符
     * @param handler    该变量的处理器，返回值将会被转换为字符串。
     * @param aliases    该变量的别称
     * @return {@link  EasyPlaceholder}
     */
    public final EasyPlaceholder handle(@NotNull String identifier, @NotNull PlaceholderHandler handler,
                                        @NotNull String... aliases) {
        this.rootExpansion.handle(identifier, handler, aliases);
        return this;
    }

    /**
     * 处理变量并返回对应内容。
     *
     * @param identifier     该变量的标识符
     * @param handler        该变量的处理器，返回值将会被转换为字符串。
     * @param paramsConsumer 用于提供该变量的可用参数
     * @param aliases        该变量的别称
     * @return {@link  EasyPlaceholder}
     */
    public final EasyPlaceholder handle(@NotNull String identifier, @NotNull PlaceholderHandler handler,
                                        @NotNull Consumer<ArrayList<String>> paramsConsumer, @NotNull String... aliases) {
        this.rootExpansion.handle(identifier, handler, paramsConsumer, aliases);
        return this;
    }

    /**
     * 处理变量并返回对应内容。
     *
     * @param identifier      该变量的标识符
     * @param handler         该变量的处理器，返回值将会被转换为字符串。
     * @param availableParams 该变量的可用参数
     * @param aliases         该变量的别称
     * @return {@link  EasyPlaceholder}
     */
    public final EasyPlaceholder handle(@NotNull String identifier, @NotNull PlaceholderHandler handler,
                                        @NotNull List<String> availableParams, @NotNull String... aliases) {
        this.rootExpansion.handle(identifier, handler, availableParams, aliases);
        return this;
    }

    /**
     * 处理变量并返回对应内容。
     *
     * @param identifier      该变量的标识符
     * @param handler         该变量的处理器，返回值将会被转换为字符串。
     * @param availableParams 该变量的可用参数
     * @param aliases         该变量的别称
     * @return {@link  EasyPlaceholder}
     */
    public final EasyPlaceholder handle(String identifier, @NotNull PlaceholderHandler handler,
                                        @NotNull Supplier<List<String>> availableParams, @NotNull String... aliases) {
        this.rootExpansion.handle(identifier, handler, availableParams, aliases);
        return this;
    }

    /**
     * 处理一组变量。
     *
     * @param section  该组变量的标识符
     * @param consumer 该组变量的处理器操作方法
     *                 <br> 在其中可调用 {@link SectionExpansion#handle(String, PlaceholderHandler, String...)} 方法处理子变量,
     *                 <br> 或者调用 {@link SectionExpansion#handleSection(String, Consumer, String...)} 方法处理下一层组变量
     * @param aliases  该变量的别称
     * @return {@link  EasyPlaceholder}
     */
    public final EasyPlaceholder handleSection(@NotNull String section,
                                               @NotNull Consumer<SectionExpansion> consumer,
                                               @NotNull String... aliases) {
        this.rootExpansion.handleSection(section, consumer, aliases);
        return this;
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        String[] args = params.split("_");
        if (args.length == 0) return onErrorParams(player);

        Object response = rootExpansion.handleRequest(player, args);
        if (response == null) return onNullResponse(player);
        else return response.toString();
    }

    @Override
    public @Nullable String getRequiredPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.rootExpansion.getIdentifier();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.author;
    }

    @Override
    public @NotNull String getVersion() {
        return this.version;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return this.rootExpansion.listPlaceholders();
    }

    @Override
    public boolean persist() {
        return this.persistent;
    }


}
