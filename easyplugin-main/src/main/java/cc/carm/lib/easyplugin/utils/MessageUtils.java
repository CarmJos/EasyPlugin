package cc.carm.lib.easyplugin.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageUtils {

	public static boolean hasPlaceholderAPI() {
		return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
	}


	public static void send(@Nullable CommandSender sender, String... messages) {
		send(sender, Arrays.asList(messages));
	}

	public static void send(@Nullable CommandSender sender, List<String> messages) {
		if (messages == null || messages.isEmpty() || sender == null) return;
		for (String s : messages) {
			sender.sendMessage(ColorParser.parse(s));
		}
	}

	public static void sendWithPlaceholders(CommandSender sender, String... messages) {
		sendWithPlaceholders(sender, Arrays.asList(messages));
	}

	public static void sendWithPlaceholders(@Nullable CommandSender sender, List<String> messages) {
		if (messages == null || messages.isEmpty() || sender == null) return;
		send(sender, setPlaceholders(sender, messages));
	}

	public static void sendWithPlaceholders(@Nullable CommandSender sender, List<String> messages, String param, Object value) {
		sendWithPlaceholders(sender, messages, new String[]{param}, new Object[]{value});
	}

	public static void sendWithPlaceholders(@Nullable CommandSender sender, List<String> messages, String[] params, Object[] values) {
		sendWithPlaceholders(sender, setCustomParams(messages, params, values));
	}

	public static String setPlaceholders(@Nullable CommandSender sender, @Nullable String message) {
		if (message == null || sender == null) return message;
		if (hasPlaceholderAPI() && sender instanceof Player) {
			return PlaceholderAPI.setPlaceholders((Player) sender, message);
		} else {
			return message;
		}
	}


	@Nullable
	@Contract("_, !null -> !null")
	public static List<String> setPlaceholders(@Nullable CommandSender sender,
											   @Nullable List<String> messages) {
		if (messages == null || messages.isEmpty() || sender == null) return messages;
		if (hasPlaceholderAPI() && sender instanceof Player) {
			return PlaceholderAPI.setPlaceholders((Player) sender, messages);
		} else {
			return messages;
		}
	}

	public static String setPlaceholders(@Nullable CommandSender sender,
										 @NotNull String message,
										 @Nullable String[] params,
										 @Nullable Object[] values) {
		return setPlaceholders(sender, setCustomParams(message, params, values));
	}

	public static List<String> setPlaceholders(@Nullable CommandSender sender,
											   @NotNull List<String> messages,
											   @Nullable String[] params,
											   @Nullable Object[] values) {
		return setPlaceholders(sender, setCustomParams(messages, params, values));
	}

	public static String setCustomParams(@NotNull String message,
										 @NotNull String param,
										 @NotNull Object value) {
		return setCustomParams(message, new String[]{param}, new Object[]{value});
	}

	@Nullable
	@Contract("!null, _, _-> !null ; null, _, _->null ")
	public static String setCustomParams(@Nullable String message,
										 @Nullable String[] params,
										 @Nullable Object[] values) {
		if (message == null) return null;
		if (params == null || values == null) return message;
		if (params.length != values.length) return message;

		HashMap<String, Object> paramsMap = new HashMap<>();
		for (int i = 0; i < params.length; i++) {
			paramsMap.put(params[i], values[i]);
		}
		return setCustomParams(message, paramsMap);
	}

	@NotNull
	public static String setCustomParams(@NotNull String message, @NotNull HashMap<String, Object> params) {
		String afterMessage = message;
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			afterMessage = afterMessage.replace(entry.getKey(), entry.getValue().toString());
		}
		return afterMessage;
	}

	@NotNull
	public static List<String> setCustomParams(@NotNull List<String> messages,
											   @NotNull String param,
											   @NotNull Object value) {
		return setCustomParams(messages, new String[]{param}, new Object[]{value});
	}

	@NotNull
	public static List<String> setCustomParams(@NotNull List<String> messages,
											   @Nullable String[] params,
											   @Nullable Object[] values) {
		if (params == null || values == null) return messages;
		if (params.length != values.length) return messages;
		HashMap<String, Object> paramsMap = new HashMap<>();
		for (int i = 0; i < params.length; i++) {
			paramsMap.put(params[i], values[i]);
		}
		return setCustomParams(messages, paramsMap);
	}

	@NotNull
	public static List<String> setCustomParams(List<String> messages, HashMap<String, Object> params) {
		return messages.stream()
				.map(message -> setCustomParams(message, params))
				.collect(Collectors.toList());
	}


}
