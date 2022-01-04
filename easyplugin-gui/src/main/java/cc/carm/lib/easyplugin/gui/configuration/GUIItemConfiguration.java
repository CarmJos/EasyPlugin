package cc.carm.lib.easyplugin.gui.configuration;

import cc.carm.lib.easyplugin.gui.GUI;
import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.utils.ItemStackFactory;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GUIItemConfiguration {

	Material material;
	int data;
	String name;
	@NotNull List<String> lore;

	@NotNull List<Integer> slots;
	@NotNull List<GUIActionConfiguration> actions;

	public GUIItemConfiguration(Material material, int data,
								String name, @NotNull List<String> lore,
								@NotNull List<GUIActionConfiguration> actions,
								@NotNull List<Integer> slots) {
		this.material = material;
		this.data = data;
		this.name = name;
		this.lore = lore;
		this.slots = slots;
		this.actions = actions;
	}

	public void setupItems(Player player, GUI gui) {
		ItemStackFactory icon = new ItemStackFactory(this.material);
		icon.setDurability(this.data);
		if (this.name != null) icon.setDisplayName(this.name);
		icon.setLore(MessageUtils.setPlaceholders(player, this.lore));

		GUIItem item = new GUIItem(icon.toItemStack());
		this.actions.stream().map(GUIActionConfiguration::toClickAction).forEach(item::addClickAction);
		this.slots.forEach(slot -> gui.setItem(slot, item));
	}

	@Nullable
	public static GUIItemConfiguration readFrom(@Nullable ConfigurationSection itemSection) {
		if (itemSection == null) return null;
		Material material = Optional.ofNullable(Material.matchMaterial(itemSection.getString("material", "STONE"))).orElse(Material.STONE);
		int data = itemSection.getInt("data", 0);
		String name = itemSection.getString("name");
		List<String> lore = itemSection.getStringList("lore");

		List<Integer> slots = itemSection.getIntegerList("slots");
		int slot = itemSection.getInt("slot", 0);

		List<String> actionsString = itemSection.getStringList("actions");
		List<GUIActionConfiguration> actions = new ArrayList<>();
		for (String actionString : actionsString) {
			int prefixStart = actionString.indexOf("[");
			int prefixEnd = actionString.indexOf("]");
			if (prefixStart < 0 || prefixEnd < 0) continue;

			String prefix = actionString.substring(prefixStart + 1, prefixEnd);
			ClickType clickType = null;
			GUIActionType actionType;
			if (prefix.contains(":")) {
				String[] args = prefix.split(":");
				clickType = GUIConfiguration.readClickType(args[0]);
				actionType = GUIActionType.readActionType(args[1]);
			} else {
				actionType = GUIActionType.readActionType(prefix);
			}

			if (actionType == null) continue;
			actions.add(new GUIActionConfiguration(clickType, actionType, actionString.substring(prefixEnd + 1).trim()));
		}

		return new GUIItemConfiguration(
				material, data, name, lore, actions,
				slots.size() > 0 ? slots : Collections.singletonList(slot)
		);
	}


}
