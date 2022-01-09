package cc.carm.lib.easyplugin.configuration.language;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.values.ConfigValue;
import cc.carm.lib.easyplugin.utils.ColorParser;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class EasyMessage {

    @Nullable ConfigValue<String> configValue;

    @Nullable String defaultValue;
    @Nullable String[] messageParams;

    public EasyMessage() {
        this(null);
    }

    public EasyMessage(@Nullable String defaultValue) {
        this(defaultValue, null);
    }

    public EasyMessage(@Nullable String defaultValue, @Nullable String[] messageParams) {
        this.defaultValue = defaultValue;
        this.messageParams = messageParams;
    }

    public void initialize(@NotNull FileConfig sourceConfig, @NotNull String sectionName) {
        this.configValue = new ConfigValue<>(sourceConfig, sectionName, String.class, getDefaultValue());
    }

    private @Nullable String getDefaultValue() {
        return defaultValue;
    }

    private @Nullable String[] getMessageParams() {
        return messageParams;
    }

    private @NotNull String getDefaultMessages() {
        if (getDefaultValue() == null) return "";
        else return getDefaultValue();
    }

    private @NotNull String getMessages() {
        if (configValue == null) {
            return getDefaultMessages();
        } else {
            return configValue.get();
        }
    }

    public @NotNull String get(@Nullable CommandSender sender) {
        return get(sender, null);
    }

    public @NotNull String get(@Nullable CommandSender sender, @Nullable Object[] values) {
        return get(sender, getMessageParams(), values);
    }

    public @NotNull String get(@Nullable CommandSender sender, @Nullable String[] params, @Nullable Object[] values) {
        if (sender == null) return getMessages();
        if (params == null || values == null) {
            return ColorParser.parse(MessageUtils.setPlaceholders(sender, getMessages()));
        } else {
            return ColorParser.parse(MessageUtils.setPlaceholders(sender, getMessages(), params, values));
        }
    }

    public void send(@Nullable CommandSender sender) {
        send(sender, null);
    }

    public void send(@Nullable CommandSender sender, @Nullable Object[] values) {
        send(sender, getMessageParams(), values);
    }

    public void send(@Nullable CommandSender sender, @Nullable String[] params, @Nullable Object[] values) {
        if (params == null || values == null) {
            MessageUtils.sendWithPlaceholders(sender, getMessages());
        } else {
            MessageUtils.sendWithPlaceholders(sender, Collections.singletonList(getMessages()), params, values);
        }
    }

    public void sendToAll() {
        sendToAll(null);
    }

    public void sendToAll(@Nullable Object[] values) {
        sendToAll(messageParams, values);
    }

    public void sendToAll(@Nullable String[] params, @Nullable Object[] values) {
        Bukkit.getOnlinePlayers().forEach(pl -> send(pl, params, values));
    }


}
