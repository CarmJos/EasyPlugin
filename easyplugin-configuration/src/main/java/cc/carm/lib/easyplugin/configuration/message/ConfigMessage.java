package cc.carm.lib.easyplugin.configuration.message;


import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.values.ConfigValue;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ConfigMessage extends ConfigValue<String> {

	String[] messageParams;

	public ConfigMessage(@NotNull String configSection) {
		this(configSection, null);
	}

	public ConfigMessage(@NotNull String configSection, @Nullable String defaultValue) {
		this(configSection, defaultValue, null);
	}

	public ConfigMessage(@NotNull String configSection, @Nullable String defaultValue, String[] messageParams) {
		super(null, configSection, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public ConfigMessage(@Nullable FileConfig config, @NotNull String configSection,
						 @Nullable String defaultValue, String[] messageParams) {
		super(config, configSection, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public @NotNull String get(CommandSender sender, Object[] values) {
		if (messageParams != null) {
			return get(sender, messageParams, values);
		} else {
			return get(sender, new String[0], new Object[0]);
		}
	}

	public @NotNull String get(CommandSender sender, String[] params, Object[] values) {
		List<String> messages = MessageUtils.setPlaceholders(sender, Collections.singletonList(get()), params, values);
		return messages != null && !messages.isEmpty() ? messages.get(0) : "";
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

	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getMessageConfiguration() : source;
	}


}
