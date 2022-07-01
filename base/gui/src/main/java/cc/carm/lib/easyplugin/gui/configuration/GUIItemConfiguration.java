package cc.carm.lib.easyplugin.gui.configuration;

import cc.carm.lib.easyplugin.gui.GUI;
import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.utils.ItemStackFactory;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GUIItemConfiguration {

    @NotNull Material type;
    int amount;
    int data;
    @Nullable String name;
    @NotNull List<String> lore;

    @NotNull List<Integer> slots;
    @NotNull List<GUIActionConfiguration> actions;

    public GUIItemConfiguration(@NotNull Material type, int amount, int data,
                                @Nullable String name, @NotNull List<String> lore,
                                @NotNull List<GUIActionConfiguration> actions,
                                @NotNull List<Integer> slots) {
        this.type = type;
        this.amount = amount;
        this.data = data;
        this.name = name;
        this.lore = lore;
        this.slots = slots;
        this.actions = actions;
    }

    public void setupItems(Player player, GUI gui) {
        ItemStackFactory icon = new ItemStackFactory(this.type, this.amount, this.data);
        if (this.name != null) icon.setDisplayName(this.name);
        icon.setLore(MessageUtils.setPlaceholders(player, this.lore));

        GUIItem item = new GUIItem(icon.toItemStack());
        this.actions.stream().map(GUIActionConfiguration::toClickAction).forEach(item::addClickAction);
        this.slots.forEach(slot -> gui.setItem(slot, item));
    }

    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("type", this.type.name());
        if (this.name != null) map.put("name", this.name);
        if (this.amount != 1) map.put("amount", this.amount);
        if (this.data != 0) map.put("data", this.data);
        if (!this.lore.isEmpty()) map.put("lore", this.lore);
        if (this.slots.size() > 1) {
            map.put("slots", this.slots);
        } else if (slots.size() == 1) {
            map.put("slot", this.slots.get(0));
        }
        if (!this.actions.isEmpty()) {
            map.put("actions", this.actions.stream().map(GUIActionConfiguration::serialize).collect(Collectors.toList()));
        }
        return map;
    }

    @Nullable
    public static GUIItemConfiguration readFrom(@Nullable ConfigurationSection itemSection) {
        if (itemSection == null) return null;
        String material = Optional.ofNullable(itemSection.getString("type")).orElse("STONE");
        Material type = Optional.ofNullable(Material.matchMaterial(material)).orElse(Material.STONE);
        int data = itemSection.getInt("data", 0);
        int amount = itemSection.getInt("amount", 1);
        String name = itemSection.getString("name");
        List<String> lore = itemSection.getStringList("lore");

        List<Integer> slots = itemSection.getIntegerList("slots");
        int slot = itemSection.getInt("slot", 0);

        List<String> actionsString = itemSection.getStringList("actions");
        List<GUIActionConfiguration> actions = new ArrayList<>();
        for (String actionString : actionsString) {
            GUIActionConfiguration action = GUIActionConfiguration.deserialize(actionString);
            if (action == null) continue;
            actions.add(action);
        }

        return new GUIItemConfiguration(
                type, amount, data, name, lore, actions,
                slots.size() > 0 ? slots : Collections.singletonList(slot)
        );
    }

}
