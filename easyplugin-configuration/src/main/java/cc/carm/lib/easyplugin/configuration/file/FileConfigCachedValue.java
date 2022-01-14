package cc.carm.lib.easyplugin.configuration.file;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class FileConfigCachedValue<V> extends FileConfigValue {

	protected V cachedValue;
	protected long updateTime;

	public FileConfigCachedValue(@NotNull String sectionName) {
		super(sectionName);
	}

	public FileConfigCachedValue(@Nullable Supplier<FileConfig> provider, @NotNull String sectionName) {
		super(provider, sectionName);
	}

	public V updateCache(V value) {
		this.updateTime = System.currentTimeMillis();
		this.cachedValue = value;
		return getCachedValue();
	}

	public boolean isExpired() {
		return getSource() == null || getSource().isExpired(this.updateTime);
	}

	public long getUpdateTime() {
		return updateTime;
	}

	@Nullable
	public V getCachedValue() {
		return cachedValue;
	}

	public void clearCache() {
		this.cachedValue = null;
	}

}
