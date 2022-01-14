package cc.carm.lib.easyplugin.configuration.impl;

import cc.carm.lib.easyplugin.configuration.cast.ConfigSectionCast;
import cc.carm.lib.easyplugin.configuration.file.FileConfig;
import cc.carm.lib.easyplugin.utils.ItemStackFactory;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigItem extends ConfigSectionCast<ConfigItem.ItemConfiguration> {

	@Nullable
	String[] itemParams;

	public ConfigItem(@NotNull String configSection) {
		this(configSection, null, null);
	}

	public ConfigItem(@NotNull String configSection,
					  @Nullable String[] itemParams) {
		this(configSection, itemParams, null);
	}

	public ConfigItem(@NotNull String configSection,
					  @Nullable ItemConfiguration defaultValue) {
		this(configSection, null, defaultValue);
	}

	public ConfigItem(@NotNull String sectionName,
					  @Nullable String[] itemParams,
					  @Nullable ItemConfiguration defaultValue) {
		this(null, sectionName, itemParams, defaultValue);
	}

	public ConfigItem(@Nullable Supplier<FileConfig> provider,
					  @NotNull String sectionName,
					  @Nullable String[] itemParams,
					  @Nullable ItemConfiguration defaultValue) {
		this(provider, sectionName, ConfigItem::parseItemConfiguration, itemParams, defaultValue);
	}

	public ConfigItem(@Nullable Supplier<FileConfig> provider,
					  @NotNull String sectionName,
					  @NotNull Function<ConfigurationSection, ItemConfiguration> valueCast,
					  @Nullable String[] itemParams,
					  @Nullable ItemConfiguration defaultValue) {
		super(provider, sectionName, valueCast, defaultValue);
		this.itemParams = itemParams;
	}

	public ItemStack getItem(@NotNull Player player) {
		return getItem(player, null);
	}

	public ItemStack getItem(@NotNull Player player,
							 @Nullable Object[] values) {
		if (values != null && itemParams != null && itemParams.length > 0) {
			return getItem(player, itemParams, values);
		} else {
			return getItem(player, null, null);
		}
	}

	public ItemStack getItem(@NotNull Player player,
							 @Nullable String[] params,
							 @Nullable Object[] values) {
		params = params == null ? new String[0] : params;
		values = values == null ? new Object[0] : values;

		ItemConfiguration configuration = get();
		if (configuration == null) return null;

		ItemStackFactory factory = new ItemStackFactory(configuration.getType());
		if (configuration.getName() != null) {
			factory.setDisplayName(MessageUtils.setCustomParams(configuration.getName(), params, values));
		}

		if (configuration.getLore() != null) {
			factory.setLore(MessageUtils.setCustomParams(configuration.getLore(), params, values));
		}

		return factory.toItemStack();
	}

	public static class ItemConfiguration {

		@NotNull Material type;
		@Nullable String name;
		@Nullable List<String> lore;

		public ItemConfiguration(@NotNull Material type, @Nullable String name, @Nullable List<String> lore) {
			this.type = type;
			this.name = name;
			this.lore = lore;
		}


		public @NotNull Material getType() {
			return type;
		}

		public @Nullable String getName() {
			return name;
		}


		public @Nullable List<String> getLore() {
			return lore;
		}

		public static ItemConfiguration readFrom(@NotNull ConfigurationSection section) {
			String typeName = section.getString("type");
			if (typeName == null) return null;
			Material type = Material.matchMaterial(typeName);
			if (type == null) return null;
			String name = section.getString("name");
			List<String> lore = section.getStringList("lore");
			return new ItemConfiguration(type, name, lore);
		}
	}

	private static ItemConfiguration parseItemConfiguration(@NotNull ConfigurationSection section) {
		return ItemConfiguration.readFrom(section);
	}

	@NotNull
	public static ItemConfiguration create(@NotNull Material type) {
		return create(type, null);
	}

	@NotNull
	public static ItemConfiguration create(@NotNull Material type,
										   @Nullable String name) {
		return create(type, name, null);
	}

	@NotNull
	public static ItemConfiguration create(@NotNull Material type,
										   @Nullable String name,
										   @Nullable String[] lore) {
		return new ItemConfiguration(type, name, lore == null ? null : Arrays.asList(lore));
	}
}
