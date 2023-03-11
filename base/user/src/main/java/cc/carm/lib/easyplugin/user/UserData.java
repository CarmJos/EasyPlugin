package cc.carm.lib.easyplugin.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class UserData<K> {

    protected final @NotNull K key;

    protected UserData(@NotNull K key) {
        this.key = key;
    }

    public @NotNull K getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData<?> userData = (UserData<?>) o;
        return key.equals(userData.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

}
