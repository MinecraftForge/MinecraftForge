package net.minecraftforge.common.tags;

import com.google.common.collect.Ordering;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.ITagEntry;
import net.minecraft.tags.Tag.ListEntry;
import net.minecraft.tags.Tag.TagEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.TagProvider;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagHelper {

    public static <T extends IForgeRegistryEntry<T>> Ordering<T> registryNameComparator()
    {
        return Ordering.natural().nullsFirst().onResultOf(entry -> entry != null ? entry.getRegistryName() : null);
    }

    public static <T extends IForgeRegistryEntry<T>> Ordering<Tag.TagEntry<T>> tagEntryComparator()
    {
        return Ordering.natural().nullsFirst().<TagEntry<T>>onResultOf(TagEntry::getSerializedId).nullsFirst();
    }

    /**
     * Sorts the entry Collection by there Registry names. Notice that {@link Tag.TagEntry#getSerializedId()} will be used for comparisons with the flattened out Elements of all other Entries.
     *
     * @param entries The Entries to sort
     * @param <T>     The type of the entries
     * @return A sorted Set which contains all {@link Tag.TagEntry} that were in entries, whilst all other Entries are flattened out with {{@link #singletonEntry(Object)}}
     */
    public static <T extends IForgeRegistryEntry<T>> Set<ITagEntry<T>> sortTagEntries(Collection<ITagEntry<T>> entries)
    {
        return entries.stream().flatMap(tagentry ->
        {
            if (tagentry instanceof Tag.TagEntry)
            {
                return Stream.of(new Tuple<>(tagentry, ((TagEntry<T>) tagentry).getSerializedId()));
            } else if (tagentry != null)
            {
                List<T> list = new ArrayList<>();
                tagentry.populate(list);
                return list.stream().filter(entry -> entry.getRegistryName() != null).map(entry -> new Tuple<>(singletonEntry(entry), entry.getRegistryName()));
            }
            return Stream.of();
        }).sorted(Ordering.natural().onResultOf(Tuple::getB)).map(Tuple::getA).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static <T> Tag.ListEntry<T> singletonEntry(T object)
    {
        return new Tag.ListEntry<>(Collections.singleton(object));
    }

    public static <T> Tag.Builder<T> addAll(Tag.Builder<T> builder, Iterable<ResourceLocation> iterable)
    {
        for (ResourceLocation thing : iterable)
        {
            builder.add(thing);
        }
        return builder;
    }

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
}
