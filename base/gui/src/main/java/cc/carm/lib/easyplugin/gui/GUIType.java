package cc.carm.lib.easyplugin.gui;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum GUIType {

    ONE_BY_NINE(1, 9),
    TWO_BY_NINE(2, 18),
    THREE_BY_NINE(3, 27),
    FOUR_BY_NINE(4, 36),
    FIVE_BY_NINE(5, 45),
    SIX_BY_NINE(6, 54),

    CANCEL(0, 0);

    private final int lines;
    private final int size;

    GUIType(int lines, int size) {
        this.lines = lines;
        this.size = size;
    }

    public int getLines() {
        return lines;
    }

    public int getSize() {
        return size;
    }

    @NotNull
    public static GUIType getBySize(int size) {
        return Arrays.stream(values()).filter(type -> type.getSize() == size).findFirst().orElse(CANCEL);
    }

    @NotNull
    public static GUIType getByLines(int lines) {
        return Arrays.stream(values()).filter(type -> type.getLines() == lines).findFirst().orElse(CANCEL);
    }


    @NotNull
    public static GUIType getByName(String name) {
        return Arrays.stream(values()).filter(type -> type.name().equalsIgnoreCase(name)).findFirst().orElse(CANCEL);
    }

}
