package cc.carm.lib.easyplugin.papi.expansion;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class SubExpansion<PARENT extends SectionExpansion> implements EasyExpansion {

    protected final @NotNull PARENT parent;
    protected final @NotNull String identifier;
    protected final @NotNull List<String> aliases;

    public SubExpansion(@NotNull PARENT parent,
                        @NotNull String identifier, @NotNull String... aliases) {
        this.parent = parent;
        this.identifier = identifier;
        this.aliases = Arrays.asList(aliases);
    }

    public abstract Object handle(@Nullable OfflinePlayer player, @NotNull String[] args) throws Exception;

    /**
     * 用于提示此子变量的全部可用参数
     *
     * @return 该子变量的全部可用参数
     */
    public List<String> listAvailableParams() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @Override
    public @NotNull List<String> getAliases() {
        return this.aliases;
    }
}
