package cc.carm.lib.easyplugin.configuration.message;


import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.values.ConfigValueList;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigMessageList extends ConfigValueList<String> {

	@Nullable String[] messageParams;

	public ConfigMessageList(String sectionName) {
		this(sectionName, new String[0]);
	}

	public ConfigMessageList(@NotNull String sectionName, @Nullable String[] defaultValue) {
		this(sectionName, defaultValue, null);
	}

	public ConfigMessageList(@NotNull String sectionName, @Nullable String[] defaultValue, String[] messageParams) {
		super(null, sectionName, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public ConfigMessageList(@Nullable FileConfig source, @NotNull String sectionName,
							 @Nullable String[] defaultValue, String[] messageParams) {
		super(source, sectionName, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public @NotNull List<String> get(@Nullable CommandSender sender) {
		return MessageUtils.setPlaceholders(sender, get());
	}

	public @NotNull List<String> get(@Nullable CommandSender sender, Object[] values) {
		if (messageParams != null) {
			return get(sender, messageParams, values);
		} else {
			return get(sender);
		}
	}

	public @NotNull List<String> get(@Nullable CommandSender sender, String[] params, Object[] values) {
		return MessageUtils.setPlaceholders(sender, get(), params, values);
	}

	public void send(@Nullable CommandSender sender) {
		MessageUtils.sendWithPlaceholders(sender, get());
	}

	public void send(@Nullable CommandSender sender, Object[] values) {
		if (messageParams != null) {
			send(sender, messageParams, values);
		} else {
			send(sender);
		}
	}

	public void send(@Nullable CommandSender sender, String[] params, Object[] values) {
		MessageUtils.sendWithPlaceholders(sender, get(), params, values);
	}

	public void sendToAll(String[] params, Object[] values) {
		Bukkit.getOnlinePlayers().forEach(pl -> MessageUtils.sendWithPlaceholders(pl, get(), params, values));
	}

	public void sendToAll() {
		Bukkit.getOnlinePlayers().forEach(player -> MessageUtils.sendWithPlaceholders(player, get()));
	}

	public void sendToAll(Object[] values) {
		if (messageParams != null) {
			sendToAll(messageParams, values);
		} else {
			sendToAll();
		}
	}

	@Override
	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getMessageConfiguration() : source;
	}

}
