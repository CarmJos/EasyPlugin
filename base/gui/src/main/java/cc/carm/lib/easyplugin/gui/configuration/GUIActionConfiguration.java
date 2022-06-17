package cc.carm.lib.easyplugin.gui.configuration;

import cc.carm.lib.easyplugin.gui.GUIItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GUIActionConfiguration {

    public static @NotNull GUIActionConfiguration of(@NotNull GUIActionType actionType,
                                                     @Nullable ClickType clickType,
                                                     @Nullable String actionContent) {
        return new GUIActionConfiguration(actionType, clickType, actionContent);
    }

    public static @NotNull GUIActionConfiguration of(@NotNull GUIActionType actionType,
                                                     @Nullable String actionContent) {
        return of(actionType, null, actionContent);
    }

    public static @NotNull GUIActionConfiguration of(@NotNull GUIActionType actionType,
                                                     @Nullable ClickType clickType) {
        return of(actionType, clickType, null);
    }

    public static @NotNull GUIActionConfiguration of(@NotNull GUIActionType actionType) {
        return of(actionType, null, null);
    }

    protected final @NotNull GUIActionType actionType;

    protected final @Nullable ClickType clickType;
    protected final @Nullable String actionContent;

    public GUIActionConfiguration(@NotNull GUIActionType actionType,
                                  @Nullable ClickType clickType,
                                  @Nullable String actionContent) {
        this.clickType = clickType;
        this.actionType = actionType;
        this.actionContent = actionContent;
    }

    public @Nullable ClickType getClickType() {
        return clickType;
    }

    public @NotNull GUIActionType getActionType() {
        return actionType;
    }

    public @Nullable String getActionContent() {
        return actionContent;
    }

    public void checkAction(Player player, ClickType type) {
        if (getClickType() == null || getClickType() == type) executeAction(player);
    }

    public void executeAction(Player targetPlayer) {
        getActionType().getExecutor().accept(targetPlayer, getActionContent());
    }

    public GUIItem.GUIClickAction toClickAction() {
        return new GUIItem.GUIClickAction() {
            @Override
            public void run(ClickType type, Player player) {
                checkAction(player, type);
            }
        };
    }

    @Nullable
    @Contract("null->null")
    public static GUIActionConfiguration deserialize(@Nullable String actionString) {
        if (actionString == null) return null;

        int prefixStart = actionString.indexOf("[");
        int prefixEnd = actionString.indexOf("]");
        if (prefixStart < 0 || prefixEnd < 0) return null;

        String prefix = actionString.substring(prefixStart + 1, prefixEnd);
        ClickType clickType = null;
        GUIActionType actionType;
        if (prefix.contains(":")) {
            String[] args = prefix.split(":");
            clickType = GUIConfiguration.readClickType(args[0]);
            actionType = GUIActionType.readActionType(args[1]);
        } else {
            actionType = GUIActionType.readActionType(prefix);
        }

        if (actionType == null) return null;

        String content = actionString.substring(prefixEnd + 1).trim();
        return of(actionType, clickType, content);
    }

    public @NotNull String serialize() {
        String prefix = "[" + getActionType().name() + (getClickType() == null ? "" : ":" + getClickType().name()) + "]";
        String content = getActionContent() == null ? "" : " " + getActionContent();
        return prefix + content;
    }

}
