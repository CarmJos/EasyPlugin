package cc.carm.lib.easyplugin.storage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface StorageType<K, T, S extends DataStorage<K, T>> {

    int getID();

    @NotNull List<String> getAlias();

    @NotNull Class<? extends S> getStorageClass();

    default @NotNull S createStorage() throws Exception {
        return getStorageClass().newInstance();
    }

}
