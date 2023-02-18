package cc.carm.lib.easyplugin.papi.expansion;

import cc.carm.lib.easyplugin.papi.EasyPlaceholder;
import cc.carm.lib.easyplugin.papi.handler.PlaceholderHandler;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SectionExpansion implements EasyExpansion {

    protected final EasyPlaceholder root;

    protected final @NotNull String identifier;
    protected final @NotNull List<String> aliases;

    protected final @NotNull Map<String, SectionExpansion> registeredSections = new HashMap<>();
    protected final @NotNull Map<String, SubExpansion<?>> registeredExpansions = new HashMap<>();

    protected final @NotNull Map<String, String> aliasesMap = new HashMap<>();

    public SectionExpansion(@NotNull EasyPlaceholder root,
                            @NotNull String identifier, @NotNull String... aliases) {
        this(root, identifier, Arrays.asList(aliases));
    }

    public SectionExpansion(@NotNull EasyPlaceholder root,
                            @NotNull String identifier, @NotNull List<String> aliases) {
        this.root = root;
        this.identifier = identifier;
        this.aliases = aliases;
    }

    public EasyPlaceholder getRoot() {
        return root;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull List<String> getAliases() {
        return this.aliases;
    }

    public void register(@NotNull SubExpansion<?> placeholder) {
        String name = placeholder.getIdentifier().toLowerCase();
        this.registeredExpansions.put(name, placeholder);
        placeholder.getAliases().forEach(alias -> this.aliasesMap.put(alias.toLowerCase(), name));
    }

    public final void register(@NotNull SectionExpansion placeholder) {
        String name = placeholder.getIdentifier().toLowerCase();
        this.registeredSections.put(name, placeholder);
        placeholder.getAliases().forEach(alias -> this.aliasesMap.put(alias.toLowerCase(), name));
    }

    public final void handle(@NotNull String identifier, @NotNull PlaceholderHandler handler,
                             @NotNull String... aliases) {
        handle(identifier, handler, Collections.emptyList(), aliases);
    }

    public final void handle(@NotNull String identifier, @NotNull PlaceholderHandler handler,
                             @NotNull Consumer<ArrayList<String>> paramsConsumer, @NotNull String... aliases) {
        handle(identifier, handler, () -> {
            ArrayList<String> params = new ArrayList<>();
            paramsConsumer.accept(params);
            return params;
        }, aliases);
    }

    public final void handle(@NotNull String identifier, @NotNull PlaceholderHandler handler,
                             @NotNull List<String> availableParams, @NotNull String... aliases) {
        handle(identifier, handler, () -> availableParams, aliases);
    }

    public final void handle(@NotNull String identifier, @NotNull PlaceholderHandler handler,
                             @NotNull Supplier<List<String>> availableParams, @NotNull String... aliases) {
        register(new SubExpansion<SectionExpansion>(this, identifier, aliases) {
            @Override
            public Object handle(@Nullable OfflinePlayer player, @NotNull String[] args) throws Exception {
                return handler.handle(player, args);
            }

            @Override
            public List<String> listAvailableParams() {
                return availableParams.get();
            }
        });
    }

    public final void handleSection(@NotNull String section, @NotNull Consumer<SectionExpansion> consumer,
                                    @NotNull String... aliases) {
        SectionExpansion sectionExpansion = new SectionExpansion(getRoot(), section, aliases);
        consumer.accept(sectionExpansion);
        register(sectionExpansion);
    }

    public final @Nullable Object handleRequest(@Nullable OfflinePlayer player, @NotNull String[] args) {
        if (args.length == 0) return getRoot().onErrorParams(player);
        String input = args[0].toLowerCase();

        SectionExpansion group = getHandler(input);
        if (group != null) return group.handleRequest(player, this.shortenArgs(args));

        SubExpansion<?> sub = getSubPlaceholder(input);
        if (sub == null) return getRoot().onErrorParams(player);

        try {
            return sub.handle(player, this.shortenArgs(args));
        } catch (Exception ex) {
            return getRoot().onException(player, sub, ex);
        }
    }

    public List<String> listPlaceholders() {
        List<String> placeholders = new ArrayList<>();

        String currentID = getIdentifier();

        for (SectionExpansion section : this.registeredSections.values()) {
            for (String groupID : section.listPlaceholders()) {
                placeholders.add(currentID + "_" + groupID);
            }
        }

        for (SubExpansion<?> placeholder : this.registeredExpansions.values()) {
            String identifier = placeholder.getIdentifier();

            List<String> params = placeholder.listAvailableParams();
            if (params.isEmpty()) {
                placeholders.add(currentID + "_" + identifier);
                continue;
            }

            for (String param : params) {
                placeholders.add(currentID + "_" + identifier + "_" + param);
            }

        }

        return placeholders;
    }


    protected @Nullable SectionExpansion getHandler(@NotNull String name) {
        SectionExpansion fromName = this.registeredSections.get(name);
        if (fromName != null) return fromName;

        String nameFromAlias = this.aliasesMap.get(name);
        if (nameFromAlias == null) return null;
        else return this.registeredSections.get(nameFromAlias);
    }

    protected @Nullable SubExpansion<?> getSubPlaceholder(@NotNull String name) {
        SubExpansion<?> fromName = this.registeredExpansions.get(name);
        if (fromName != null) return fromName;

        String nameFromAlias = this.aliasesMap.get(name);
        if (nameFromAlias == null) return null;
        else return this.registeredExpansions.get(nameFromAlias);
    }

    protected String[] shortenArgs(String[] args) {
        if (args.length == 0) {
            return args;
        } else {
            List<String> argList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
            return argList.toArray(new String[0]);
        }
    }

}
