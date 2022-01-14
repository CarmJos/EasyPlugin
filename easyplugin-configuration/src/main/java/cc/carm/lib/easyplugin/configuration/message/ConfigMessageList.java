package cc.carm.lib.easyplugin.configuration.message;


import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.values.ConfigValueList;
import cc.carm.lib.easyplugin.utils.ColorParser;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ConfigMessageList extends ConfigValueList<String> {

	@Nullable String[] messageParams;

	public ConfigMessageList(String sectionName) {
		this(sectionName, new String[0]);
	}

	public ConfigMessageList(@NotNull String sectionName, @Nullable String[] defaultValue) {
		this(sectionName, defaultValue, null);
	}

	public ConfigMessageList(@NotNull String sectionName,
							 @Nullable String[] defaultValue,
							 String[] messageParams) {
		this((Supplier<FileConfig>) null, sectionName, defaultValue, null);
	}

	public ConfigMessageList(@Nullable FileConfig source, @NotNull String sectionName,
							 @Nullable String[] defaultValue, String[] messageParams) {
		this(source == null ? null : () -> source, sectionName, defaultValue, messageParams);
	}

	public ConfigMessageList(@Nullable Supplier<FileConfig> provider, @NotNull String sectionName,
							 @Nullable String[] defaultValue, String[] messageParams) {
		super(provider, sectionName, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public String[] getMessageParams() {
		return messageParams;
	}

	public @NotNull List<String> get(@Nullable CommandSender sender) {
		return get(sender, null);
	}


	public @NotNull List<String> get(@Nullable CommandSender sender, @Nullable Object[] values) {
		return get(sender, getMessageParams(), values);
	}

	public @NotNull List<String> get(@Nullable CommandSender sender, @Nullable String[] params, @Nullable Object[] values) {
		if (sender == null) return get();
		params = params == null ? new String[0] : params;
		values = values == null ? new Object[0] : values;

		return ColorParser.parse(MessageUtils.setPlaceholders(sender, get(), params, values));
	}

	public void send(@Nullable CommandSender sender) {
		send(sender, null);
	}

	public void send(@Nullable CommandSender sender, @Nullable Object[] values) {
		send(sender, getMessageParams(), values);
	}

	public void send(@Nullable CommandSender sender, @Nullable String[] params, @Nullable Object[] values) {
		List<String> messages = get(sender, params, values);
		if (messages.isEmpty()) return;
		if (messages.size() == 1 && messages.get(0).length() == 0) return; //空消息不再发送
		MessageUtils.send(sender, messages);
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

	@Override
	public FileConfig defaultSource() {
		return FileConfig.getMessageConfiguration();
	}
}
