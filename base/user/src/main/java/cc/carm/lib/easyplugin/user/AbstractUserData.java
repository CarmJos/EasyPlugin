package cc.carm.lib.easyplugin.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractUserData<K> implements UserData<K> {

    protected final @NotNull K key;

    /**
     * Used to mark the data as dropping (save and unload) status.
     */
    protected boolean dropping = false;

    protected AbstractUserData(@NotNull K key) {
        this.key = key;
    }

    /**
     * @param dropping true if the data is dropping, false otherwise
     */
    public void setDropping(boolean dropping) {
        this.dropping = dropping;
    }

    /**
     * @return true if the data is dropping, false otherwise
     */
    public boolean isDropping() {
        return dropping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUserData<?> abstractUserData = (AbstractUserData<?>) o;
        return key.equals(abstractUserData.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

}
