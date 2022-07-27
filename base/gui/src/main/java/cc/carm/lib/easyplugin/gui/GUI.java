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
    private static final HashMap<UUID, GUI> openedGUIs = new HashMap<>();

    public static void initialize(JavaPlugin plugin) {
        GUI.plugin = plugin;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static HashMap<UUID, GUI> getOpenedGUIs() {
        return openedGUIs;
    }

    protected GUIType type;
    protected String name;
    public HashMap<Integer, GUIItem> items;
    public Inventory inv;

    /**
     * 当玩家点击目标GUI时是否取消
     */
    boolean cancelOnTarget = true;

    /**
     * 当玩家点击自己背包时是否取消
     */
    boolean cancelOnSelf = true;

    /**
     * 当玩家点击界面外时是否取消
     */
    boolean cancelOnOuter = true;

    protected final Map<String, Object> flags = new LinkedHashMap<>();

    protected GUIListener listener;

    public GUI(GUIType type, String name) {
        this.type = type;
        this.name = ColorParser.parse(name);
        this.items = new HashMap<>();
    }


    public HashMap<@NotNull Integer, @NotNull GUIItem> getItems() {
        return new HashMap<>(items);
    }

    public final void setItem(int index, @Nullable GUIItem item) {
        if (item == null) {
            this.items.remove(index);
        } else {
            this.items.put(index, item);
        }
    }

    public void setItem(GUIItem item, int... index) {
        for (int i : index) {
            setItem(i, item);
        }
    }

    public GUIItem getItem(int index) {
        return this.items.get(index);
    }

    /**
     * 更新玩家箱子的视图
     */
    public void updateView() {
        if (this.inv != null) {
            List<HumanEntity> viewers = this.inv.getViewers();
            IntStream.range(0, this.inv.getSize()).forEach(index -> inv.setItem(index, new ItemStack(Material.AIR)));
            getItems().forEach((index, item) -> inv.setItem(index, item.getDisplay()));
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

        Inventory inv = Bukkit.createInventory(null, this.type.getSize(), this.name);
        IntStream.range(0, inv.getSize()).forEach(index -> inv.setItem(index, new ItemStack(Material.AIR)));
        getItems().forEach((index, item) -> inv.setItem(index, item.getDisplay()));

        GUI previous = getOpenedGUI(player);
        if (previous != null) {
            previous.listener.close(player);
        }

        setOpenedGUI(player, this);

        this.inv = inv;
        player.openInventory(inv);

        if (listener == null) {
            Bukkit.getPluginManager().registerEvents(listener = new GUIListener(this), getPlugin());
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

    public GUIType getGUIType() {
        return type;
    }

    public String getGUIName() {
        return name;
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

}
