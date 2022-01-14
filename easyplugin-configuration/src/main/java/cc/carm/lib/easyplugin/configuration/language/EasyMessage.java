package cc.carm.lib.easyplugin.configuration.language;

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

	public void initialize(@NotNull FileConfig source, @NotNull String sectionName) {
		this.configValue = new ConfigValue<>(() -> source, sectionName, String.class, getDefaultValue());
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
		String messages = getMessages();
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


}
