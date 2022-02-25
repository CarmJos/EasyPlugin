package cc.carm.lib.easyplugin.configuration.language.builder;

import cc.carm.lib.easyplugin.configuration.language.EasyMessageList;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class EasyMessageListBuilder {

    String[] contents;
    String[] params;

    @Nullable String paramPrefix = "%(";
    @Nullable String paramSuffix = ")";

    public EasyMessageListBuilder() {
    }


    EasyMessageListBuilder contents(String... contents) {
        this.contents = contents;
        return this;
    }

    EasyMessageListBuilder params(String... placeholders) {
        this.params = placeholders;
        return this;
    }

    public EasyMessageListBuilder setParamPrefix(@Nullable String paramPrefix) {
        this.paramPrefix = paramPrefix;
        return this;
    }

    public EasyMessageListBuilder setParamSuffix(@Nullable String paramSuffix) {
        this.paramSuffix = paramSuffix;
        return this;
    }

    public EasyMessageListBuilder setParamFormat(@Nullable String paramPrefix, @Nullable String paramSuffix) {
        this.paramPrefix = paramPrefix;
        this.paramSuffix = paramSuffix;
        return this;
    }

    protected @Nullable String[] buildParams() {
        if (this.params == null) return null;
        else return Arrays.stream(this.params).map(param -> paramPrefix + param + paramSuffix).toArray(String[]::new);
    }

    public EasyMessageList build() {
        return new EasyMessageList(this.contents, buildParams());
    }

}
