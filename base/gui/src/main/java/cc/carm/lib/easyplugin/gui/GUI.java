package cc.carm.lib.easyplugin.gui;

import cc.carm.lib.easyplugin.utils.ColorParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public class GUI {

    private static JavaPlugin plugin;
    private static final Map<UUID, GUI> openedGUIs = new HashMap<>();

    public static void initialize(JavaPlugin plugin) {
        GUI.plugin = plugin;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static Map<UUID, GUI> getOpenedGUIs() {
        return openedGUIs;
    }

    protected final @NotNull GUIType type;
    protected @NotNull String title;
    protected Inventory inv;

    protected final SortedMap<Integer, GUIItem> items = new TreeMap<>();
    protected ItemStack emptyItem = null;

    protected boolean cancelOnTarget = true; // 当玩家点击GUI时是否取消对应事件
    protected boolean cancelOnSelf = true; // 当玩家点击自己背包时是否取消对应事件
    protected boolean cancelOnOuter = true; //  当玩家点击界面外时是否取消对应事件

    protected final Map<String, Object> flags = new LinkedHashMap<>();

    protected GUIListener listener;

    public GUI(@NotNull GUIType type, @NotNull String title) {
        this.type = type;
        this.title = ColorParser.parse(title);
    }

    public SortedMap<@NotNull Integer, @NotNull GUIItem> getItems() {
        return items;
    }

    public final void setItem(int index, @Nullable GUIItem item) {
        if (item == null) {
            this.items.remove(index);
        } else {
            this.items.put(index, item);
        }
    }

    public void setItemStack(int index, @Nullable ItemStack item) {
        setItem(index, item == null ? null : new GUIItem(item));
    }

    public void setItem(GUIItem item, int... index) {
        for (int i : index) {
            setItem(i, item);
        }
    }

    public void setItemStack(ItemStack item, int... index) {
        for (int i : index) {
            setItemStack(i, item);
        }
    }

    public GUIItem getItem(int index) {
        return this.items.get(index);
    }

    public void setEmptyItem(ItemStack item) {
        this.emptyItem = item;
    }

    protected void fillEmptySlots(@NotNull Inventory inventory) {
        if (emptyItem == null) return;
        IntStream.range(0, inventory.getSize())
                .filter(i -> inventory.getItem(i) == null)
                .forEach(index -> inventory.setItem(index, emptyItem));
    }

    protected void applyToInventory(Inventory inventory) {
        IntStream.range(0, inventory.getSize()).forEach(index -> inventory.setItem(index, new ItemStack(Material.AIR)));
        getItems().forEach((index, item) -> inventory.setItem(index, item.getDisplay()));
        fillEmptySlots(inventory);
    }

    public void updateTitle(@NotNull String title) {
        this.title = ColorParser.parse(title);
        if (this.inv != null) {
            this.inv = Bukkit.createInventory(null, this.type.getSize(), this.title);
            applyToInventory(this.inv);
        }
    }

    /**
     * 更新玩家箱子的视图
     */
    public void updateView() {
        this.onUpdate();
        if (this.inv != null) {
            List<HumanEntity> viewers = this.inv.getViewers();
            applyToInventory(this.inv);
            viewers.forEach(p -> ((Player) p).updateInventory());
        }
    }

    /**
     * 设置是否取消点击GUI内物品的事件
     * 如果不取消，玩家可以从GUI中拿取物品。
     *
     * @param b 是否取消
     */
    public void setCancelOnTarget(boolean b) {
        this.cancelOnTarget = b;
    }

    /**
     * 设置是否取消点击自己背包内物品的事件
     * 如果不取消，玩家可以从自己的背包中拿取物品。
     *
     * @param b 是否取消
     */
    public void setCancelOnSelf(boolean b) {
        this.cancelOnSelf = b;
    }

    /**
     * 设置是否取消点击GUI外的事件
     * 如果不取消，玩家可以把物品从GUI或背包中丢出去
     *
     * @param b 是否取消
     */
    public void setCancelOnOuter(boolean b) {
        this.cancelOnOuter = b;
    }

    public Object getFlag(String flag) {
        return this.flags.get(flag);
    }

    public void setFlag(String flag, Object obj) {
        this.flags.put(flag, obj);
    }

    public void removeFlag(String flag) {
        this.flags.remove(flag);
    }

    public void rawClickListener(InventoryClickEvent event) {
    }

    public void openGUI(Player player) {
        if (this.type == GUIType.CANCEL) {
            throw new IllegalStateException("被取消或不存在的GUI");
        }

        Inventory ui = Bukkit.createInventory(null, this.type.getSize(), this.title);
        applyToInventory(ui);

        GUI previous = getOpenedGUI(player);
        if (previous != null && previous.listener != null) {
            previous.listener.close(player);
        }

        setOpenedGUI(player, this);

        this.inv = ui;
        player.openInventory(ui);

        if (listener == null) {
            this.listener = new GUIListener(this);
            Bukkit.getPluginManager().registerEvents(this.listener, getPlugin());
        }
    }

    /**
     * 拖动GUI内物品是执行的代码
     *
     * @param event InventoryDragEvent
     */
    public void onDrag(InventoryDragEvent event) {
    }

    /**
     * 关闭GUI时执行的代码
     */
    public void onClose() {
    }

    /**
     * 当GUI更新时执行的代码
     */
    public void onUpdate() {
    }

    public GUIType getGUIType() {
        return type;
    }

    public String getGUIName() {
        return title;
    }

    public static void setOpenedGUI(Player player, GUI gui) {
        getOpenedGUIs().put(player.getUniqueId(), gui);
    }

    public static boolean hasOpenedGUI(Player player) {
        return getOpenedGUIs().containsKey(player.getUniqueId());
    }

    public static GUI getOpenedGUI(Player player) {
        return getOpenedGUIs().get(player.getUniqueId());
    }

    public static void removeOpenedGUI(Player player) {
        getOpenedGUIs().remove(player.getUniqueId());
    }

    public static void closeAll() {
        Set<HumanEntity> viewers = new HashSet<>();
        getOpenedGUIs().values().stream().flatMap(gui -> gui.inv.getViewers().stream()).forEach(viewers::add);
        viewers.forEach(HumanEntity::closeInventory);
        getOpenedGUIs().clear();
    }

}
