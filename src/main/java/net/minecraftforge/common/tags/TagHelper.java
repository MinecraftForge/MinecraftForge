package net.minecraftforge.common.tags;

import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.TagProvider;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class TagHelper {
    @Nullable
    public static <T extends IForgeRegistryEntry<T>> TagProvider<T> getTagsIfPresent(IForgeRegistry<T> registry)
    {
        if (registry.supportsTagging()) return registry.getTagProvider();
        return null;
    }

    public static <T extends IForgeRegistryEntry<T>> Collection<ResourceLocation> getTagIDsFor(IForgeRegistry<T> registry, ResourceLocation object)
    {
        if (!registry.supportsTagging()) return Collections.emptyList();
        return registry.getTagProvider().getOwningTagIDs(object);
    }

    public static <T extends IForgeRegistryEntry<T>> Collection<ResourceLocation> getTagIDsFor(IForgeRegistry<T> registry, T object)
    {
        if (!registry.supportsTagging()) return Collections.emptyList();
        return registry.getTagProvider().getOwningTagIDs(object);
    }

    public static <T extends IForgeRegistryEntry<T>> Collection<T> getTaggedObjects(IForgeRegistry<T> registry, ResourceLocation tagId)
    {
        if (!registry.supportsTagging()) return Collections.emptyList();
        return registry.getTagProvider().getOrCreate(tagId).getAllElements();
    }

    public static <T extends IForgeRegistryEntry<T>> Collection<Tag<T>> getTagsFor(IForgeRegistry<T> registry, ResourceLocation object)
    {
        if (!registry.supportsTagging()) return Collections.emptyList();
        return registry.getTagProvider().getOwningTags(object);
    }

    public static <T extends IForgeRegistryEntry<T>> Collection<Tag<T>> getTagsFor(IForgeRegistry<T> registry, T object)
    {
        if (!registry.supportsTagging()) return Collections.emptyList();
        return registry.getTagProvider().getOwningTags(object);
    }

    public static <T extends IForgeRegistryEntry<T>> List<Tag<T>> getTagsMatchingPredicate(IForgeRegistry<T> registry, Predicate<Tag<T>> predicate)
    {
        if (!registry.supportsTagging()) return Collections.emptyList();
        return registry.getTagProvider().getMatchingTags(predicate);
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsMatchingPredicate(IForgeRegistry<T> registry, Predicate<Tag<T>> predicate)
    {
        if (!registry.supportsTagging()) return false;
        return registry.getTagProvider().matchesTag(predicate);
    }

    public static <V> Predicate<Tag<V>> containsObjectPredicate(V object)
    {
        return tag -> tag.contains(object);
    }

    public static Predicate<Tag<?>> pathEqualsPredicate(String path)
    {
        return tag -> tag.getId().getPath().equals(path);
    }

    public static Predicate<Tag<?>> pathContainsPredicate(String path)
    {
        return tag -> tag.getId().getPath().contains(path);
    }

    public static Predicate<Tag<?>> pathMatchesPattern(String pattern)
    {
        return tag -> tag.getId().getPath().matches(pattern);
    }

    public static Predicate<Tag<?>> pathStartsWithPredicate(String s)
    {
        return tag -> tag.getId().getPath().startsWith(s);
    }

    public static Predicate<Tag<?>> byModPredicate(String modId)
    {
        return tag -> tag.getId().getNamespace().equals(modId);
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsWithExactPath(IForgeRegistry<T> registry, T object, String path)
    {
        return anyTagsMatchingPredicate(registry, t -> containsObjectPredicate(object).test(t) && pathEqualsPredicate(path).test(t));
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsContainingPath(IForgeRegistry<T> registry, T object, String path)
    {
        return anyTagsMatchingPredicate(registry, t -> containsObjectPredicate(object).test(t) && pathContainsPredicate(path).test(t));
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsStartingWithPath(IForgeRegistry<T> registry, T object, String path)
    {
        return anyTagsMatchingPredicate(registry, t -> containsObjectPredicate(object).test(t) && pathStartsWithPredicate(path).test(t));
    }

    public static <T extends IForgeRegistryEntry<T>> boolean anyTagsFromMod(IForgeRegistry<T> registry, T object, String modId)
    {
        return anyTagsMatchingPredicate(registry, t -> containsObjectPredicate(object).test(t) && byModPredicate(modId).test(t));
    }

    public static <T> Tag.Builder<T> addAll(Tag.Builder<T> builder, Iterable<ResourceLocation> iterable) {
        for (ResourceLocation thing: iterable)
        {
            builder.add(thing);
        }
        return builder;
    }
}
