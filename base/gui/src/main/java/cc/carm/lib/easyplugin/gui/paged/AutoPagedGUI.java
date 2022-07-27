package cc.carm.lib.easyplugin.gui.paged;

import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.gui.GUIType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class AutoPagedGUI extends CommonPagedGUI {

    public static Function<Player, ItemStack> defaultPreviousPage = null;
    public static Function<Player, ItemStack> defaultNextPage = null;

    ItemStack previousPageUI;
    ItemStack nextPageUI;
    int previousPageSlot = -1;
    int nextPageSlot = -1;

    public AutoPagedGUI(GUIType type, String name, int[] range) {
        super(type, name, range);
    }

    public AutoPagedGUI(GUIType type, String name, int a, int b) {
        super(type, name, a, b);
    }

    public void setPreviousPageUI(ItemStack lastPageUI) {
        this.previousPageUI = lastPageUI;
    }

    public void setNextPageUI(ItemStack nextPageUI) {
        this.nextPageUI = nextPageUI;
    }

    public void setPreviousPageSlot(int slot) {
        this.previousPageSlot = slot;
    }

    public void setNextPageSlot(int slot) {
        this.nextPageSlot = slot;
    }

    @Override
    public void openGUI(Player user) {
        if (previousPageSlot >= 0) {
            if (hasPreviousPage()) {
                setItem(previousPageSlot, new GUIItem(
                        previousPageUI == null ? getDefaultPreviousPage(user) : previousPageUI) {
                    @Override
                    public void onClick(Player clicker, ClickType type) {
                        if (type == ClickType.RIGHT) {
                            goFirstPage();
                        } else {
                            goPreviousPage();
                        }
                        openGUI(user);
                    }
                });
            } else {
                setItem(previousPageSlot, null);
            }
        }

        if (nextPageSlot >= 0) {
            if (hasNextPage()) {
                setItem(nextPageSlot, new GUIItem(
                        nextPageUI == null ? getDefaultNextPage(user) : nextPageUI) {
                    @Override
                    public void onClick(Player clicker, ClickType type) {
                        if (type == ClickType.RIGHT) {
                            goLastPage();
                        } else {
                            goNextPage();
                        }
                        openGUI(user);
                    }
                });
            } else {
                setItem(nextPageSlot, null);
            }
        }


        super.openGUI(user);
    }

    private static ItemStack getDefaultNextPage(Player player) {
        return defaultNextPage != null ? defaultNextPage.apply(player) : null;
    }

    private static ItemStack getDefaultPreviousPage(Player player) {
        return defaultPreviousPage != null ? defaultPreviousPage.apply(player) : null;
    }
}
