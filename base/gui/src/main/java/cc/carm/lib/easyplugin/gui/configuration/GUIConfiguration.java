package cc.carm.lib.easyplugin.gui.configuration;

import cc.carm.lib.easyplugin.gui.GUI;
import cc.carm.lib.easyplugin.gui.GUIType;
import cc.carm.lib.easyplugin.utils.ColorParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GUIConfiguration {

	String title;
	int lines;

	List<GUIItemConfiguration> guiItems;

	public GUIConfiguration(String title, int lines, List<GUIItemConfiguration> guiItems) {
		this.title = title;
		this.lines = lines;
		this.guiItems = guiItems;
	}

	public String getTitle() {
		return ColorParser.parse(title);
	}

	public int getLines() {
		return lines;
	}

	public GUIType getGUIType() {
		return Optional.of(GUIType.getByLines(lines))
				.map(type -> type == GUIType.CANCEL ? GUIType.SIX_BY_NINE : type)
				.get();
	}

	public List<GUIItemConfiguration> getGuiItems() {
		return guiItems;
	}

	public void setupItems(Player player, GUI gui) {
		getGuiItems().forEach(itemConfiguration -> itemConfiguration.setupItems(player, gui));
	}


	public static GUIConfiguration readConfiguration(@Nullable ConfigurationSection section) {
		if (section == null) return new GUIConfiguration("name", 6, new ArrayList<>());

		String title = section.getString("title", "");
		int lines = section.getInt("lines", 6);
		ConfigurationSection itemsSection = section.getConfigurationSection("items");
		if (itemsSection == null) return new GUIConfiguration(title, lines, new ArrayList<>());

		return new GUIConfiguration(
				title, lines, itemsSection.getKeys(false).stream()
				.map(key -> GUIItemConfiguration.readFrom(itemsSection.getConfigurationSection(key)))
				.filter(Objects::nonNull)
				.collect(Collectors.toList())
		);
	}

	public static ClickType readClickType(String type) {
		return Arrays.stream(ClickType.values())
				.filter(click -> click.name().equalsIgnoreCase(type))
				.findFirst().orElse(null);
	}

}
