package cc.carm.lib.easyplugin.configuration.message;


import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.configuration.values.ConfigValue;
import cc.carm.lib.easyplugin.utils.ColorParser;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ConfigMessage extends ConfigValue<String> {

	String[] messageParams;

	public ConfigMessage(@NotNull String sectionName) {
		this(sectionName, null);
	}

	public ConfigMessage(@NotNull String sectionName, @Nullable String defaultValue) {
		this(sectionName, defaultValue, null);
	}

	public ConfigMessage(@NotNull String sectionName,
						 @Nullable String defaultValue,
						 String[] messageParams) {
		this((Supplier<FileConfig>) null, sectionName, defaultValue, messageParams);
	}

	public ConfigMessage(@Nullable FileConfig source, @NotNull String sectionName,
						 @Nullable String defaultValue, String[] messageParams) {
		this(source == null ? null : () -> source, sectionName, defaultValue, messageParams);
	}

	public ConfigMessage(@Nullable Supplier<FileConfig> provider, @NotNull String sectionName,
						 @Nullable String defaultValue, String[] messageParams) {
		super(provider, sectionName, String.class, defaultValue);
		this.messageParams = messageParams;
	}

	public String[] getMessageParams() {
		return messageParams;
	}

	public @NotNull String get(@Nullable CommandSender sender) {
		return get(sender, null);
	}

	public @NotNull String get(@Nullable CommandSender sender, @Nullable Object[] values) {
		return get(sender, getMessageParams(), values);
	}

	public @NotNull String get(@Nullable CommandSender sender, @Nullable String[] params, @Nullable Object[] values) {
		String messages = get();
		if (sender == null || messages.length() < 1) return messages;
		params = params == null ? new String[0] : params;
		values = values == null ? new Object[0] : values;
		return ColorParser.parse(MessageUtils.setPlaceholders(sender, messages, params, values));
	}

	public void send(@Nullable CommandSender sender) {
		send(sender, null);
	}

	public void send(@Nullable CommandSender sender, @Nullable Object[] values) {
		send(sender, getMessageParams(), values);
	}

	public void send(@Nullable CommandSender sender, @Nullable String[] params, @Nullable Object[] values) {
		String message = get(sender, params, values);
		if (message.length() < 1) return;

		MessageUtils.send(sender, message);
	}

	public void sendBar(@Nullable Player player) {
		sendBar(player, null);
	}

	public void sendBar(@Nullable Player player, @Nullable Object[] values) {
		sendBar(player, getMessageParams(), values);
	}

	public void sendBar(@Nullable Player player, @Nullable String[] params, @Nullable Object[] values) {
		if (player == null) return;
		String message = get(player, params, values);
		if (message.length() < 1) return;

		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(get(player, params, values)));
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
