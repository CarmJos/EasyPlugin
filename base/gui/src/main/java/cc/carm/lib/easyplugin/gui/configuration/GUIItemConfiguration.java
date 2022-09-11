package cc.carm.lib.easyplugin.gui.configuration;

import cc.carm.lib.easyplugin.gui.GUI;
import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.utils.ColorParser;
import cc.carm.lib.easyplugin.utils.ItemStackFactory;
import cc.carm.lib.easyplugin.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GUIItemConfiguration {

    @Nullable ItemStack original;

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
        this(null, type, amount, data, name, lore, actions, slots);
    }

    public GUIItemConfiguration(@Nullable ItemStack original,
                                @NotNull Material type, int amount, int data,
                                @Nullable String name, @NotNull List<String> lore,
                                @NotNull List<GUIActionConfiguration> actions,
                                @NotNull List<Integer> slots) {
        this.original = original;
        this.type = type;
        this.amount = amount;
        this.data = data;
        this.name = name;
        this.lore = lore;
        this.slots = slots;
        this.actions = actions;
    }

    public void setupItems(Player player, GUI gui) {
        ItemStack itemStack;
        if (original != null) {
            ItemStack tmp = original.clone();
            ItemMeta originalMeta = original.getItemMeta();
            if (originalMeta != null) {
                if (originalMeta.hasDisplayName()) {
                    originalMeta.setDisplayName(parseText(player, originalMeta.getDisplayName()));
                }
                if (originalMeta.getLore() != null) {
                    originalMeta.setLore(parseTexts(player, originalMeta.getLore()));
                }

            }

            tmp.setItemMeta(originalMeta);
            itemStack = tmp;
        } else {
            ItemStackFactory icon = new ItemStackFactory(this.type, this.amount, this.data);
            if (this.name != null) {
                icon.setDisplayName(parseText(player, this.name));
            }
            if (!this.lore.isEmpty()) {
                icon.setLore(parseTexts(player, this.lore));
            }
            itemStack = icon.toItemStack();
        }


        GUIItem item = new GUIItem(itemStack);
        this.actions.stream().map(GUIActionConfiguration::toClickAction).forEach(item::addClickAction);
        this.slots.forEach(slot -> gui.setItem(slot, item));
    }

    private List<String> parseTexts(Player player, List<String> lore) {
        return ColorParser.parse(MessageUtils.setPlaceholders(player, lore));
    }

    @NotNull
    private String parseText(Player player, @NotNull String name) {
        return ColorParser.parse(MessageUtils.setPlaceholders(player, name));
    }

    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (original != null) map.put("original", original);
        else {
            map.put("type", this.type.name());
            if (this.data != 0) map.put("data", this.data);
        }

        if (this.name != null) map.put("name", this.name);
        if (this.amount != 1) map.put("amount", this.amount);
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

        ItemStack original = null;
        if (itemSection.contains("original")) original = itemSection.getItemStack("original");

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
                original, type, amount, data, name, lore, actions,
                slots.size() > 0 ? slots : Collections.singletonList(slot)
        );
    }

}
