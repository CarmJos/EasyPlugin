package cc.carm.lib.easyplugin.gui.paged;


import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.gui.GUIType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommonPagedGUI extends PagedGUI {

    protected final int[] range;

    public CommonPagedGUI(@NotNull GUIType type, @NotNull String title, int a, int b) {
        this(type, title, toRange(type, a, b));
    }

    public CommonPagedGUI(@NotNull GUIType type, @NotNull String title, int[] range) {
        super(type, title);
        Arrays.sort(range);
        this.range = range;
    }

    @Override
    protected void fillEmptySlots(@NotNull Inventory inventory) {
        if (emptyItem == null) return;
        Set<Integer> rangeSet = Arrays.stream(this.range).boxed().collect(Collectors.toSet());
        IntStream.range(0, inventory.getSize())
                .filter(i -> inventory.getItem(i) == null)
                .filter(i -> !rangeSet.contains(i))
                .forEach(index -> inventory.setItem(index, emptyItem));
    }

    private static int getLine(int i) {
        return i / 9 + 1;
    }

    private static int getColumn(int i) {
        return i % 9 + 1;
    }

    @Override
    public boolean hasPreviousPage() {
        return page > 1;
    }

    @Override
    public boolean hasNextPage() {
        return page < getLastPageNumber();
    }


    /**
     * 前往第一页
     */
    public void goFirstPage() {
        this.page = 1;
        onPageChange(this.page);
    }


    /**
     * 前往最后一页
     */
    public void goLastPage() {
        this.page = getLastPageNumber();
        onPageChange(this.page);
    }


    /**
     * 得到最后一页的页码
     *
     * @return 最后一页的页码
     */
    @Override
    public int getLastPageNumber() {
        return getLastPageNumber(range.length);
    }

    /**
     * 得到第一页的页码
     *
     * @return 第一页页码(默认为1)
     */
    public int getFirstPageNumber() {
        return 1;
    }


    @Override
    public void openGUI(Player player) {
        if (container.isEmpty()) {
            super.openGUI(player);
            return;
        }
        List<GUIItem> list = new ArrayList<>();
        int start = (page - 1) * range.length;
        for (int i = start; i < start + range.length; i++) {
            if (i < container.size()) {
                list.add(container.get(i));
            } else {
                break;
            }
        }

        int i = 0;
        Arrays.stream(range).forEach(index -> setItem(index, null));
        for (int index : range) {
            if (i < list.size()) {
                setItem(index, list.get(i));
                i++;
            } else {
                break;
            }
        }
        super.openGUI(player);
    }


    /*
    int[] matrix = new int[]{
    0,	1,	2,	3,	4,	5,	6,	7,	8,
    9,	10,	11,	12,	13,	14,	15,	16,	17,
    18,	19,	20,	21,	22,	23,	24,	25,	26,
    27,	28,	29,	30,	31,	32,	33,	34,	35,
    36,	37,	38,	39,	40,	41,	42,	43,	44,
    45,	46,	47,	48,	49,	50,	51,	52,	53
    }
    */
    private static int[] toRange(GUIType type, int a, int b) {
        if (a > b) {
            a = a ^ b;
            b = a ^ b;
            a = a ^ b;
        }

        int lineA = getLine(a);
        int columnA = getColumn(a);
        int lineB = getLine(b);
        int columnB = getColumn(b);

        if (lineB > type.getLines())
            throw new IndexOutOfBoundsException("页面内容范围超过了GUI的大小");

        int[] range = new int[(lineB - lineA + 1) * (columnB - columnA + 1)];

        int l = 0;
        for (int i = 0; i < type.getSize(); i++) {
            int li = getLine(i);
            int ci = getColumn(i);
            if (li >= lineA && li <= lineB && ci >= columnA && ci <= columnB) {
                range[l] = i;
                l++;
            }
        }

        return range;
    }
}
