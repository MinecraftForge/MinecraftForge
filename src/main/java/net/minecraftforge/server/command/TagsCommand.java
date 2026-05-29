/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@code /singularity tags} command for listing a registry's tags, getting the elements of tags, and querying the tags of a
 * registry object.
 *
 * <p>Each command is paginated, showing {@value PAGE_SIZE} entries at a time. When there are more than 0 entries,
 * the text indicating the amount of entries is highlighted and can be clicked to copy the list of all entries (across
 * all pages) to the clipboard. (This is reflected by the use of green text in brackets, mimicking the clickable
 * coordinates in the {@code /locate} command's message)</p>
 *
 * <p>The command has three subcommands:</p>
 * <ul>
 *     <li>{@code /singularity tags &lt;registry> list [page]} - Lists all available tags in the given registry.</li>
 *     <li>{@code /singularity tags &lt;registry> get &lt;tag> [page]} - Gets all elements of the given tag in the given registry.</li>
 *     <li>{@code /singularity tags &lt;registry> query &lt;element> [page]} - Queries for all tags in the given registry which
 *     contain the given registry object.</li>
 * </ul>
 */
class TagsCommand {
    private static final long PAGE_SIZE = 8;
    private static final ResourceKey<Registry<Registry<?>>> ROOT_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Identifier.parse("root"));

    private static final DynamicCommandExceptionType UNKNOWN_REGISTRY = new DynamicCommandExceptionType(key ->
            Component.translatable("commands.singularity.tags.error.unknown_registry", key));
    private static final Dynamic2CommandExceptionType UNKNOWN_TAG = new Dynamic2CommandExceptionType((tag, registry) ->
            Component.translatable("commands.singularity.tags.error.unknown_tag", tag, registry));
    private static final Dynamic2CommandExceptionType UNKNOWN_ELEMENT = new Dynamic2CommandExceptionType((tag, registry) ->
            Component.translatable("commands.singularity.tags.error.unknown_element", tag, registry));

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        /*
         * /singularity tags <registry> list [page]
         * /singularity tags <registry> get <tag> [page]
         * /singularity tags <registry> query <element> [page]
         */
        return Commands.literal("tags")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("registry", ResourceKeyArgument.key(ROOT_REGISTRY_KEY))
                        .suggests(TagsCommand::suggestRegistries)
                        .then(Commands.literal("list")
                                .executes(ctx -> listTags(ctx, 1))
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(ctx -> listTags(ctx, IntegerArgumentType.getInteger(ctx, "page")))
                                )
                        )
                        .then(Commands.literal("get")
                                .then(Commands.argument("tag", IdentifierArgument.id())
                                        .suggests(suggestFromRegistry(r -> r.getTags().map(t -> t.key().location())::iterator))
                                        .executes(ctx -> listTagElements(ctx, 1))
                                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                                .executes(ctx -> listTagElements(ctx, IntegerArgumentType.getInteger(ctx, "page")))
                                        )
                                )
                        )
                        .then(Commands.literal("query")
                                .then(Commands.argument("element", IdentifierArgument.id())
                                        .suggests(suggestFromRegistry(Registry::keySet))
                                        .executes(ctx -> queryElementTags(ctx, 1))
                                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                                .executes(ctx -> queryElementTags(ctx, IntegerArgumentType.getInteger(ctx, "page")))
                                        )
                                )
                        )
                );
    }

    private static int listTags(final CommandContext<CommandSourceStack> ctx, final int page) throws CommandSyntaxException {
        final ResourceKey<? extends Registry<?>> registryKey = getResourceKey(ctx, "registry", ROOT_REGISTRY_KEY)
                .orElseThrow(); // Expect to be always retrieve a resource key for the root registry (registry key)
        final Registry<?> registry = ctx.getSource().getServer().registryAccess().lookup(registryKey)
                .orElseThrow(() -> UNKNOWN_REGISTRY.create(registryKey.identifier()));

        final long tagCount = registry.getTags().count();

        ctx.getSource().sendSuccess(() -> createMessage(
                Component.translatable("commands.singularity.tags.registry_key", Component.literal(registryKey.identifier().toString()).withStyle(ChatFormatting.GOLD)),
                "commands.singularity.tags.tag_count",
                "commands.singularity.tags.copy_tag_names",
                tagCount,
                page,
                ChatFormatting.DARK_GREEN,
                () -> registry.getTags()
                        .map(s -> s.unwrap().map(k -> k.location().toString(), Object::toString))
        ), false);

        return (int) tagCount;
    }

    private static int listTagElements(final CommandContext<CommandSourceStack> ctx, final int page) throws CommandSyntaxException
    {
        final ResourceKey<? extends Registry<?>> registryKey = getResourceKey(ctx, "registry", ROOT_REGISTRY_KEY)
                .orElseThrow(); // Expect to be always retrieve a resource key for the root registry (registry key)
        final Registry<Object> registry = ctx.getSource().getServer().registryAccess().lookup(registryKey)
                .orElseThrow(() -> UNKNOWN_REGISTRY.create(registryKey.identifier()));

        final Identifier tagLocation = IdentifierArgument.getId(ctx, "tag");
        final TagKey<Object> tagKey = TagKey.create(cast(registryKey), tagLocation);

        final HolderSet.Named<?> tag = registry.get(tagKey)
                .orElseThrow(() -> UNKNOWN_TAG.create(tagKey.location(), registryKey.identifier()));

        ctx.getSource().sendSuccess(() -> createMessage(
                Component.translatable("commands.singularity.tags.tag_key",
                        Component.literal(tagKey.registry().identifier().toString()).withStyle(ChatFormatting.GOLD),
                        Component.literal(tagKey.location().toString()).withStyle(ChatFormatting.DARK_GREEN)),
                "commands.singularity.tags.element_count",
                "commands.singularity.tags.copy_element_names",
                tag.size(),
                page,
                ChatFormatting.YELLOW,
                () -> tag.stream().map(s -> s.unwrap().map(k -> k.identifier().toString(), Object::toString))
        ), false);

        return tag.size();
    }

    private static <T> int queryElementTags(final CommandContext<CommandSourceStack> ctx, final int page) throws CommandSyntaxException {
        final ResourceKey<? extends Registry<?>> registryKey = getResourceKey(ctx, "registry", ROOT_REGISTRY_KEY)
                .orElseThrow(); // Expect to be always retrieve a resource key for the root registry (registry key)
        final Registry<Object> registry = ctx.getSource().getServer().registryAccess().lookup(registryKey)
                .orElseThrow(() -> UNKNOWN_REGISTRY.create(registryKey.identifier()));

        final Identifier elementLocation = IdentifierArgument.getId(ctx, "element");
        final ResourceKey<Object> elementKey = ResourceKey.create(cast(registryKey), elementLocation);

        final Holder<?> elementHolder = registry.get(elementKey)
                .orElseThrow(() -> UNKNOWN_ELEMENT.create(elementLocation, registryKey.identifier()));

        final long containingTagsCount = elementHolder.tags().count();

        ctx.getSource().sendSuccess(() -> createMessage(
                Component.translatable("commands.singularity.tags.element",
                        Component.literal(registryKey.identifier().toString()).withStyle(ChatFormatting.GOLD),
                        Component.literal(elementLocation.toString()).withStyle(ChatFormatting.YELLOW)),
                "commands.singularity.tags.containing_tag_count",
                "commands.singularity.tags.copy_tag_names",
                containingTagsCount,
                page,
                ChatFormatting.DARK_GREEN,
                () -> elementHolder.tags().map(k -> k.location().toString())
        ), false);

        return (int) containingTagsCount;
    }

    private static MutableComponent createMessage(final MutableComponent header,
            final String containsText,
            final String copyHoverText,
            final long count,
            final long currentPage,
            final ChatFormatting elementColor,
            final Supplier<Stream<String>> names
    ) {
        final String allElementNames = names.get().sorted().collect(Collectors.joining("\n"));
        final long totalPages = (count - 1) / PAGE_SIZE + 1;
        final long actualPage = (long) Mth.clamp(currentPage, 1, totalPages);

        MutableComponent containsComponent = Component.translatable(containsText, count);
        if (count > 0) { // Highlight the count text, make it clickable, and append page counters
            containsComponent = ComponentUtils.wrapInSquareBrackets(containsComponent.withStyle(s -> s
                    .withColor(ChatFormatting.GREEN)
                    .withClickEvent(new ClickEvent.CopyToClipboard(allElementNames))
                    .withHoverEvent(new HoverEvent.ShowText(Component.translatable(copyHoverText)))));
            containsComponent = Component.translatable("commands.singularity.tags.page_info",
                    containsComponent, actualPage, totalPages);
        }

        final MutableComponent tagElements = Component.literal("").append(containsComponent);
        names.get()
                .sorted()
                .skip(PAGE_SIZE * (actualPage - 1))
                .limit(PAGE_SIZE)
                .map(Component::literal)
                .map(t -> t.withStyle(elementColor))
                .map(t -> Component.translatable("\n - ").append(t))
                .forEach(tagElements::append);

        return header.append("\n").append(tagElements);
    }

    private static CompletableFuture<Suggestions> suggestRegistries(
        final CommandContext<CommandSourceStack> ctx,
        final SuggestionsBuilder builder
    ) {
        ctx.getSource().registryAccess().registries()
                .map(RegistryAccess.RegistryEntry::key)
                .map(ResourceKey::identifier)
                .map(Identifier::toString)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static SuggestionProvider<CommandSourceStack> suggestFromRegistry(final Function<Registry<?>, Iterable<Identifier>> namesFunction) {
        return (ctx, builder) -> {
            var key = getResourceKey(ctx, "registry", ROOT_REGISTRY_KEY).orElse(null);
            if (key == null)
                return builder.buildFuture();

            var reg = ctx.getSource().registryAccess().lookup(key).orElse(null);
            if (reg == null)
                return builder.buildFuture();

            SharedSuggestionProvider.suggestResource(namesFunction.apply(reg), builder);
            return builder.buildFuture();
        };
    }

    private static <T> Optional<ResourceKey<T>> getResourceKey(
        final CommandContext<CommandSourceStack> ctx,
        final String name,
        final ResourceKey<Registry<T>> registryKey
    ) {
        // Don't inline to avoid an unchecked cast warning due to raw types
        final ResourceKey<?> key = ctx.getArgument(name, ResourceKey.class);
        return key.cast(registryKey);
    }

    @SuppressWarnings("unchecked")
    private static <O> O cast(final Object input) {
        return (O)input;
    }
}

