package cc.carm.lib.easyplugin.configuration.language.builder;

import cc.carm.lib.easyplugin.configuration.language.EasyMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class EasyMessageBuilder {

    String content;
    String[] params;

    @Nullable String paramPrefix = "%(";
    @Nullable String paramSuffix = ")";

    public EasyMessageBuilder() {
    }


    EasyMessageBuilder contents(String content) {
        this.content = content;
        return this;
    }

    EasyMessageBuilder params(String... placeholders) {
        this.params = placeholders;
        return this;
    }

    public EasyMessageBuilder setParamPrefix(@Nullable String paramPrefix) {
        this.paramPrefix = paramPrefix;
        return this;
    }

    public EasyMessageBuilder setParamSuffix(@Nullable String paramSuffix) {
        this.paramSuffix = paramSuffix;
        return this;
    }

    public EasyMessageBuilder setParamFormat(@Nullable String paramPrefix, @Nullable String paramSuffix) {
        this.paramPrefix = paramPrefix;
        this.paramSuffix = paramSuffix;
        return this;
    }

    public EasyMessage build() {
        return new EasyMessage(this.content, Optional.ofNullable(this.params).map(params -> Arrays.stream(this.params).map(param -> paramPrefix + param + paramSuffix).toArray(String[]::new)).orElse(null));
    }

}
