package cc.carm.lib.easyplugin.storage.file;

import cc.carm.lib.easyplugin.storage.DataStorage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class FolderBasedStorage<K, T> implements DataStorage<K, T> {

    protected final @NotNull String folderPath;
    protected File dataFolder;

    public FolderBasedStorage(@NotNull String folderPath) {
        this.folderPath = folderPath;
    }

    protected @NotNull File initializeFolder(@NotNull File parentFolder) throws Exception {
        this.dataFolder = new File(parentFolder, folderPath);
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdir()) {
                throw new Exception("无法创建数据文件夹！");
            }
        } else if (!dataFolder.isDirectory()) {
            throw new Exception("数据文件夹路径对应的不是一个文件夹！");
        }
        return dataFolder;
    }

    protected @NotNull List<File> listFiles() {
        if (this.dataFolder == null) return Collections.emptyList();
        if (!this.dataFolder.isDirectory()) return Collections.emptyList();

        File[] files = this.dataFolder.listFiles();
        if (files == null) return Collections.emptyList();

        return Arrays.asList(files);
    }

    public File getDataFolder() {
        return dataFolder;
    }

}
