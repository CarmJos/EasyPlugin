package cc.carm.lib.easyplugin.gui.paged;

import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.gui.GUIType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AutoPagedGUI extends CommonPagedGUI {

    public static Function<Player, ItemStack> defaultPreviousPage = null;
    public static Function<Player, ItemStack> defaultNextPage = null;

    protected ItemStack previousPageUI;
    protected ItemStack nextPageUI;
    protected ItemStack noPreviousPageUI;
    protected ItemStack noNextPageUI;
    protected int previousPageSlot = -1;
    protected int nextPageSlot = -1;

    public AutoPagedGUI(@NotNull GUIType type, @NotNull String title, int[] range) {
        super(type, title, range);
    }

    public AutoPagedGUI(@NotNull GUIType type, @NotNull String title, int a, int b) {
        super(type, title, a, b);
    }

    public void setPreviousPageUI(@Nullable ItemStack lastPageUI) {
        this.previousPageUI = lastPageUI;
    }

    public void setNoPreviousPageUI(@Nullable ItemStack noPreviousPageUI) {
        this.noPreviousPageUI = noPreviousPageUI;
    }

    public void setNextPageUI(@Nullable ItemStack nextPageUI) {
        this.nextPageUI = nextPageUI;
    }

    public void setNoNextPageUI(@Nullable ItemStack noNextPageUI) {
        this.noNextPageUI = noNextPageUI;
    }

    public void setPreviousPageSlot(int slot) {
        this.previousPageSlot = slot;
    }

    public void setNextPageSlot(int slot) {
        this.nextPageSlot = slot;
    }

    @Override
    protected void fillEmptySlots(@NotNull Inventory inventory) {
        if (emptyItem == null) return;
        Set<Integer> rangeSet = Arrays.stream(this.range).boxed().collect(Collectors.toSet());
        if (previousPageSlot >= 0) rangeSet.add(previousPageSlot);
        if (nextPageSlot >= 0) rangeSet.add(nextPageSlot);
        IntStream.range(0, inventory.getSize())
                .filter(i -> inventory.getItem(i) == null)
                .filter(i -> !rangeSet.contains(i))
                .forEach(index -> inventory.setItem(index, emptyItem));
    }


    @Override
    public void openGUI(Player user) {
        if (previousPageSlot >= 0) {
            if (hasPreviousPage()) {
                setItem(previousPageSlot, new GUIItem(Optional.ofNullable(defaultPreviousPage).map(d -> d.apply(user)).orElse(previousPageUI)) {
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
                setItem(previousPageSlot, this.noPreviousPageUI == null ? null : new GUIItem(noPreviousPageUI));
            }
        }

        if (nextPageSlot >= 0) {
            if (hasNextPage()) {
                setItem(nextPageSlot, new GUIItem(Optional.ofNullable(defaultNextPage).map(d -> d.apply(user)).orElse(nextPageUI)) {
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
                setItem(nextPageSlot, this.noNextPageUI == null ? null : new GUIItem(noNextPageUI));
            }
        }

        super.openGUI(user);
    }


}
