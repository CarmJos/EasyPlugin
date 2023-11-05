package cc.carm.lib.easyplugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class GUIItem {

    protected ItemStack display;
    protected boolean actionActive = true;

    protected final Set<GUIClickAction> actions = new HashSet<>();
    protected final Set<GUIClickAction> actionsIgnoreActive = new HashSet<>();

    public GUIItem(ItemStack display) {
        this.display = display;
    }

    public final ItemStack getDisplay() {
        return this.display;
    }

    public final void setDisplay(ItemStack display) {
        this.display = display;
    }

    public final boolean isActionActive() {
        return this.actionActive;
    }

    public final void setActionActive(boolean b) {
        actionActive = b;
    }

    /**
     * 玩家点击该物品后执行的代码
     * 可以使用 {@link #onClick(Player, ClickType)} 操作点击者
     *
     * @param type 点击的类型
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public void onClick(ClickType type) {
    }

    /**
     * 玩家点击GUI后执行的代码
     *
     * @param clicker 点击的玩家
     * @param type    点击的类型
     */
    public void onClick(Player clicker, ClickType type) {
        this.onClick(type); // Deprecated method support
    }

    public void addClickAction(GUIClickAction action) {
        actions.add(action);
    }

    public void addActionIgnoreActive(GUIClickAction action) {
        actionsIgnoreActive.add(action);
    }

    public void rawClickAction(InventoryClickEvent event) {

    }

    /**
     * 自定义点击事件代码 (须自行触发)
     *
     * @param player 点击GUI的玩家
     */
    public void customAction(Player player) {

    }

    public abstract static class GUIClickAction {
        public abstract void run(ClickType type, Player player);
    }

}
