package cc.carm.lib.easyplugin.gui.paged;


import cc.carm.lib.easyplugin.gui.GUI;
import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.gui.GUIType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PagedGUI extends GUI {

    protected List<GUIItem> container = new ArrayList<>();
    protected int page = 1;

    protected PagedGUI(@NotNull GUIType type, @NotNull String title) {
        super(type, title);
    }

    public int addItem(@NotNull GUIItem i) {
        container.add(i);
        return container.size() - 1;
    }

    public int addItem(int index, @NotNull GUIItem i) {
        container.add(index, i);
        return container.size() - 1;
    }

    public int addItemStack(@NotNull ItemStack itemStack) {
        return addItem(new GUIItem(itemStack));
    }

    /**
     * 从GUI中移除一个物品
     *
     * @param item 物品
     */
    public void removeItem(@NotNull GUIItem item) {
        container.remove(item);
    }

    /**
     * 从GUI中移除一个物品
     *
     * @param index 物品格子数
     */
    public void removeItem(int index) {
        container.remove(index);
    }

    public @NotNull List<GUIItem> getPagedContainer() {
        return this.container;
    }

    /**
     * 当GUI改变页码时执行的代码
     */
    public void onPageChange(int pageNum) {
    }

    /**
     * 前往上一页
     */
    public void goPreviousPage() {
        if (hasPreviousPage()) {
            page--;
            this.onPageChange(this.page);
        } else throw new IndexOutOfBoundsException();
    }


    /**
     * 前往下一页
     */
    public void goNextPage() {
        if (hasNextPage()) {
            page++;
            this.onPageChange(this.page);
        } else throw new IndexOutOfBoundsException();
    }


    /**
     * @return 是否有上一页
     */
    public abstract boolean hasPreviousPage();

    /**
     * @return 是否有下一页
     */
    public abstract boolean hasNextPage();

}
