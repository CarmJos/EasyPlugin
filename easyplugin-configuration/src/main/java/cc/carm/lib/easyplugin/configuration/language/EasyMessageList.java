package cc.carm.lib.easyplugin.configuration.language;

import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.values.ConfigValueList;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EasyMessageList {

    @Nullable ConfigValueList<String> configValue;

    @Nullable String[] defaultValue;
    @Nullable String[] messageParams;

    public EasyMessageList() {
        this((String[]) null);
    }

    public EasyMessageList(@Nullable String... defaultValue) {
        this(defaultValue, null);
    }

    public EasyMessageList(@Nullable String[] defaultValue,
                           @Nullable String[] messageParams) {
        this.defaultValue = defaultValue;
        this.messageParams = messageParams;
    }

    public void initialize(FileConfig sourceConfig, String sectionName) {
        configValue = new ConfigValueList<>(sourceConfig, sectionName, String.class, getDefaultValue());
    }

    private @Nullable String[] getDefaultValue() {
        return defaultValue;
    }

    @Unmodifiable
    private @NotNull List<String> getDefaultMessages() {
        if (getDefaultValue() == null) return new ArrayList<>();
        else return Arrays.asList(getDefaultValue());
    }


    private @Nullable String[] getMessageParams() {
        return messageParams;
    }

    private @NotNull List<String> getMessages() {
        if (configValue == null) {
            return getDefaultMessages();
        } else {
            return configValue.get();
        }
    }

    public @NotNull List<String> get(@Nullable CommandSender sender) {
        return get(sender, null);
    }

    public @NotNull List<String> get(@Nullable CommandSender sender, @Nullable Object[] values) {
        return get(sender, getMessageParams(), values);
    }

    public @NotNull List<String> get(@Nullable CommandSender sender, @Nullable String[] params, @Nullable Object[] values) {
        if (sender == null) return getMessages();
        if (params == null || values == null) {
            return MessageUtils.setPlaceholders(sender, getMessages());
        } else {
            return MessageUtils.setPlaceholders(sender, getMessages(), params, values);
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
            MessageUtils.sendWithPlaceholders(sender, getMessages(), params, values);
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
