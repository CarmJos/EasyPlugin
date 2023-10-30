package cc.carm.lib.easyplugin.gui.paged;


import cc.carm.lib.easyplugin.gui.GUI;
import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.gui.GUIType;

import java.util.ArrayList;
import java.util.List;

public abstract class PagedGUI extends GUI {

    List<GUIItem> container = new ArrayList<>();
    public int page = 1;

    public PagedGUI(GUIType type, String name) {
        super(type, name);
    }

    public int addItem(GUIItem i) {
        container.add(i);
        return container.size() - 1;
    }

    /**
     * 从GUI中移除一个物品
     *
     * @param item 物品
     */
    public void removeItem(GUIItem item) {
        container.remove(item);
    }

    /**
     * 从GUI中移除一个物品
     *
     * @param slot 物品格子数
     */
    public void removeItem(int slot) {
        container.remove(slot);
    }

    public List<GUIItem> getItemsContainer() {
        return new ArrayList<>(container);
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
