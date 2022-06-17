package cc.carm.lib.easyplugin.storage.file;

import cc.carm.lib.easyplugin.storage.DataStorage;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class FileBasedStorage<K, T> implements DataStorage<K, T> {


    protected final String fileName;
    protected File dataFile;

    public FileBasedStorage(String fileName) {
        this.fileName = fileName;
    }

    protected @NotNull File initializeFile(@NotNull File parentFolder) throws Exception {
        this.dataFile = new File(parentFolder, fileName);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) throw new Exception("无法创建数据文件！");
        } else if (dataFile.isDirectory()) {
            throw new Exception("文件路径对应的不是一个文件！");
        }
        return dataFile;
    }

    public File getDataFile() {
        return dataFile;
    }
}
