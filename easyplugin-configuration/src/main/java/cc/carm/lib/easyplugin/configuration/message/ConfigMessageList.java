package cc.carm.lib.easyplugin.configuration.message;


import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.values.ConfigValueList;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigMessageList extends ConfigValueList<String> {

	@Nullable String[] messageParams;

	public ConfigMessageList(String configSection) {
		this(configSection, new String[0]);
	}

	public ConfigMessageList(@NotNull String configSection, @Nullable String[] defaultValue) {
		this(configSection, defaultValue, null);
	}

	public ConfigMessageList(@NotNull String configSection, @Nullable String[] defaultValue, String[] messageParams) {
		super(null, configSection, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public ConfigMessageList(@Nullable FileConfig config, @NotNull String configSection,
							 @Nullable String[] defaultValue, String[] messageParams) {
		super(config, configSection, String.class, defaultValue);
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

	@Override
	public @Nullable FileConfig getSource() {
		return source == null ? FileConfig.getMessageConfiguration() : source;
	}
}
