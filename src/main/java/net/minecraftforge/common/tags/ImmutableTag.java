package net.minecraftforge.common.tags;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.JsonObject;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ImmutableTag<T extends IForgeRegistryEntry<T>> extends Tag<T> {
    /**
     * @param tag The tag to copy
     * @param <T> The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyOf(Tag<T> tag)
    {
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf(TagHelper.registryNameComparator(), tag.getAllElements());
        ImmutableList.Builder<Tag.ITagEntry<T>> tagEntryBuilder = ImmutableList.builder();
        for (T item : items)
        {
            tagEntryBuilder.add(new Tag.ListEntry<>(Collections.singleton(item)));
        }
        return new ImmutableTag<>(tag.getId(), items, tagEntryBuilder.build());
    }

    /**
     * @param entries The entries to copy
     * @param <T>     The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyOf(ResourceLocation id, Collection<ITagEntry<T>> entries)
    {
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf
                (TagHelper.registryNameComparator(), entries.stream().flatMap(tagentry ->
                {
                    List<T> internalEntries = new ArrayList<>();
                    tagentry.populate(internalEntries);
                    return internalEntries.stream();
                })
                        .collect(Collectors.toList()));
        return new ImmutableTag<>(id, items, ImmutableList.copyOf(entries));
    }

    /**
     * Like {@link #copyOf(Tag)} except that {@link Tag.TagEntry}'s won't be flattened out into Singleton {@link Tag.ListEntry}, but preserved and sorted.
     * Hereby sorting is
     *
     * @param tag The tag to copy
     * @param <T> The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyPreserveTagEntries(Tag<T> tag)
    {
        Set<ITagEntry<T>> sortedEntries = TagHelper.sortTagEntries(tag.getEntries());
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf
                (TagHelper.registryNameComparator(), sortedEntries.stream().flatMap(tagentry ->
                {
                    List<T> entries = new ArrayList<>();
                    tagentry.populate(entries);
                    return entries.stream();
                })
                        .collect(Collectors.toList()));
        return new ImmutableTag<>(tag.getId(), items, ImmutableList.copyOf(sortedEntries));
    }


    public static <T extends IForgeRegistryEntry<T>> Builder<T> builder()
    {
        return new Builder<>();
    }

    private ImmutableSortedSet<T> sortedElements;
    private ImmutableList<ITagEntry<T>> entries;

    private ImmutableTag(ResourceLocation resourceLocationIn, ImmutableSortedSet<T> sortedItems, ImmutableList<ITagEntry<T>> entriesIn)
    {
        super(resourceLocationIn, ImmutableList.of(), true);
        this.sortedElements = sortedItems;
        this.entries = entriesIn;
    }

    @Override
    public ImmutableSortedSet<T> getAllElements()
    {
        return sortedElements;
    }

    @Override
    public ImmutableList<ITagEntry<T>> getEntries()
    {
        return entries;
    }

    @Override
    public boolean contains(T itemIn)
    {
        return sortedElements.contains(itemIn);
    }

    public static final class Builder<T extends IForgeRegistryEntry<T>> extends SortedTagBuilder<T> {
        private Builder()
        {
            super(ImmutableTag::copyOf);
        }

        @Override
        public Builder<T> add(ITagEntry<T> entry)
        {
            return (Builder<T>) super.add(entry);
        }

        @Override
        public Builder<T> add(T itemIn)
        {
            return (Builder<T>) super.add(itemIn);
        }

        @Override
        public Builder<T> addAll(Collection<T> itemsIn)
        {
            return (Builder<T>) super.addAll(itemsIn);
        }

        @Override
        public Builder<T> addAllTags(Iterable<ResourceLocation> itemsIn)
        {
            return (Builder<T>) super.addAllTags(itemsIn);
        }

        @Override
        public Builder<T> add(ResourceLocation resourceLocationIn)
        {
            return (Builder<T>) super.add(resourceLocationIn);
        }

        @Override
        public Builder<T> add(Tag<T> tagIn)
        {
            return (Builder<T>) super.add(tagIn);
        }

        /**
         * @param preserveOrderIn whether or not the Order in which TagEntries were added should be preserved
         * @return this Builder, in order to support Method chaining
         * @implNote Notice that this doesn't have any influence on the resulting ImmutableTag, because ImmutableTags require to be sorted.
         */
        @Override
        public Builder<T> ordered(boolean preserveOrderIn)
        {
            return (Builder<T>) super.ordered(preserveOrderIn);
        }

        @Override
        public Builder<T> deserialize(Predicate<ResourceLocation> isValueKnownPredicate, Function<ResourceLocation, T> objectGetter, JsonObject json)
        {
            return (Builder<T>) super.deserialize(isValueKnownPredicate, objectGetter, json);
        }

        @Override
        public ImmutableTag<T> build(ResourceLocation resourceLocationIn)
        {
            return (ImmutableTag<T>) super.build(resourceLocationIn);
        }
    }
}
