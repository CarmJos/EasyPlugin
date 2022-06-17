package cc.carm.lib.easyplugin.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DataStorage<K, T> {

    /**
     * 在插件加载存储源时执行。
     *
     * @throws Exception 当出现任何错误时抛出
     */
    void initialize() throws Exception;

    /**
     * 在插件被卸载时执行。
     */
    void shutdown();

    /**
     * 用于加载数据的方法。<b>该方法应当异步运行！</b>
     * <br>
     * <br>若不存在对应数据，请返回 null 。
     * <br>若加载出现任何错误，请抛出异常。
     *
     * @param key 数据主键
     * @throws Exception 当出现任何错误时抛出
     */
    @Nullable
    T loadData(@NotNull K key) throws Exception;

    /**
     * 用于保存数据的方法。 <b>该方法应当被异步运行！</b>
     *
     * @param data 数据
     * @throws Exception 当出现任何错误时抛出
     */
    void saveData(@NotNull T data) throws Exception;

}
