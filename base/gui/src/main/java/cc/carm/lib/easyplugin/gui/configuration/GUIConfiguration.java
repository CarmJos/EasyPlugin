package cc.carm.lib.easyplugin.gui.configuration;

import cc.carm.lib.easyplugin.gui.GUI;
import cc.carm.lib.easyplugin.gui.GUIType;
import cc.carm.lib.easyplugin.utils.ColorParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class GUIConfiguration {

    protected String title;
    protected int lines;

    protected Map<String, GUIItemConfiguration> guiItems;

    public GUIConfiguration(String title, int lines) {
        this(title, lines, new LinkedHashMap<>(1));
    }

    public GUIConfiguration(String title, int lines, Map<String, GUIItemConfiguration> guiItems) {
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

    public Map<String, GUIItemConfiguration> getGUIItems() {
        return guiItems;
    }

    public void setupItems(Player player, GUI gui) {
        getGUIItems().values().forEach(itemConfiguration -> itemConfiguration.setupItems(player, gui));
    }

    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("title", this.title);
        map.put("lines", this.lines);
        if (!this.guiItems.isEmpty()) {
            LinkedHashMap<String, Object> items = new LinkedHashMap<>();
            this.guiItems.forEach((key, value) -> items.put(key, value.serialize()));
            map.put("items", items);
        }
        return map;
    }


    public static GUIConfiguration readConfiguration(@Nullable ConfigurationSection section) {
        if (section == null) return new GUIConfiguration("name", 6);

        return new GUIConfiguration(
                section.getString("title", ""),
                section.getInt("lines", 6),
                readItems(section.getConfigurationSection("items"))
        );
    }

    public static Map<String, GUIItemConfiguration> readItems(ConfigurationSection itemsSection) {
        Map<String, GUIItemConfiguration> items = new LinkedHashMap<>();
        if (itemsSection == null) return items;

        for (String key : itemsSection.getKeys(false)) {
            GUIItemConfiguration item = GUIItemConfiguration.readFrom(itemsSection.getConfigurationSection(key));
            if (item != null) items.put(key, item);
        }

        return items;
    }

    public static ClickType readClickType(String type) {
        return Arrays.stream(ClickType.values())
                .filter(click -> click.name().equalsIgnoreCase(type))
                .findFirst().orElse(null);
    }

}
