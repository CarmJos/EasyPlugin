package cc.carm.lib.easyplugin.configuration.message;


import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.values.ConfigValue;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class ConfigMessage extends ConfigValue<String> {

	String[] messageParams;

	public ConfigMessage(@NotNull String sectionName) {
		this(sectionName, null);
	}

	public ConfigMessage(@NotNull String sectionName, @Nullable String defaultValue) {
		this(sectionName, defaultValue, null);
	}

	public ConfigMessage(@NotNull String sectionName, @Nullable String defaultValue, String[] messageParams) {
		super(null, sectionName, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public ConfigMessage(@Nullable FileConfig source, @NotNull String sectionName,
						 @Nullable String defaultValue, String[] messageParams) {
		super(source, sectionName, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public @NotNull String get(CommandSender sender) {
		return MessageUtils.setPlaceholders(sender, get());
	}

	public @NotNull String get(CommandSender sender, Object[] values) {
		if (messageParams != null) {
			return get(sender, messageParams, values);
		} else {
			return get(sender);
		}
	}

	public @NotNull String get(CommandSender sender, String[] params, Object[] values) {
		return MessageUtils.setPlaceholders(sender, get(), params, values);
	}

	public void send(CommandSender sender) {
		MessageUtils.sendWithPlaceholders(sender, get());
	}

	public void send(CommandSender sender, Object[] values) {
		if (messageParams != null) {
			send(sender, messageParams, values);
		} else {
			send(sender, new String[0], new Object[0]);
		}

	}

	public void send(CommandSender sender, String[] params, Object[] values) {
		MessageUtils.sendWithPlaceholders(sender, Collections.singletonList(get()), params, values);
	}

	public void sendToAll() {
		Bukkit.getOnlinePlayers().forEach(player -> MessageUtils.sendWithPlaceholders(player, Collections.singletonList(get())));
	}

	public void sendToAll(Object[] values) {
		if (messageParams != null) {
			sendToAll(messageParams, values);
		} else {
			sendToAll();
		}
	}

	public void sendToAll(String[] params, Object[] values) {
		Bukkit.getOnlinePlayers().forEach(pl -> MessageUtils.sendWithPlaceholders(pl, Collections.singletonList(get()), params, values));
	}

	@Override
	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getMessageConfiguration() : source;
	}


}
