package cc.carm.lib.easyplugin.papi.expansion;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EasyExpansion {
    
    @NotNull String getIdentifier();

    @NotNull List<String> getAliases();

}
