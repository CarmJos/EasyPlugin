package cc.carm.lib.easyplugin.storage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface StorageType<K, V> {

    int getID();

    @NotNull List<String> getAlias();

    @NotNull Class<? extends DataStorage<K, V>> getStorageClass();

    default @NotNull DataStorage<K, V> createStorage() throws Exception {
        return getStorageClass().newInstance();
    }

}
