package cc.carm.lib.easyplugin.user;

import org.jetbrains.annotations.NotNull;

public interface UserData<K> {

    @NotNull K key();

    @Deprecated
    default @NotNull K getKey() {
        return key();
    }

    /**
     * @param dropping true if the data is dropping, false otherwise
     */
    void setDropping(boolean dropping);

    /**
     * @return true if the data is dropping, false otherwise
     */
    boolean isDropping();
}
