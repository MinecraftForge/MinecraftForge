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
        return asTag(tag.getId(), tag.getAllElements());
    }

    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> asTag(ResourceLocation id, Iterable<T> elements)
    {
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf(TagHelper.registryNameComparator(), elements);
        ImmutableList.Builder<Tag.ITagEntry<T>> tagEntryBuilder = ImmutableList.builder();
        for (T item : items)
        {
            tagEntryBuilder.add(new Tag.ListEntry<>(Collections.singleton(item)));
        }
        return new ImmutableTag<>(id, items, tagEntryBuilder.build());
    }


    /**
     * @param entries The entries to copy
     * @param <T>     The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyPreserveShallowTagStructure(ResourceLocation id, Collection<ITagEntry<T>> entries)
    {
        //This kind of Sorting (immutableShallowCopyTransformer) will flatten out the Tag Structure contained in Tag.TagEntries - more efficient
        Set<ITagEntry<T>> sortedEntries = TagHelper.sortTagEntries(entries, TagHelper.immutableShallowCopyTransformer(), Collectors.toCollection(LinkedHashSet::new));
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf(
                TagHelper.registryNameComparator(), sortedEntries.stream().flatMap(tagentry ->
                {
                    List<T> tagEntries = new ArrayList<>();
                    tagentry.populate(tagEntries);
                    return tagEntries.stream();
                })
                        .collect(Collectors.toList()));
        return new ImmutableTag<>(id, items, ImmutableList.copyOf(sortedEntries));
    }

    /**
     * Like {@link #copyOf(Tag)} except that {@link Tag.TagEntry}'s won't be flattened out into Singleton {@link Tag.ListEntry}, but preserved and sorted.
     * Hereby sorting is
     *
     * @param tag The tag to copy
     * @param <T> The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyPreserveShallowTagStructure(Tag<T> tag)
    {
        return copyPreserveShallowTagStructure(tag.getId(), tag.getEntries());
    }

    /**
     * @param entries The entries to copy
     * @param <T>     The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyPreserveTagStructure(ResourceLocation id, Collection<ITagEntry<T>> entries)
    {
        //This kind of Sorting (immutableDeepCopyTransformer) will recursively call copyPreserveTagStructure forEach Tag.TagEntry in there, and there by recursively copy that tag too... etc.
        Set<ITagEntry<T>> sortedEntries = TagHelper.sortTagEntries(entries, TagHelper.immutableDeepCopyTransformer(), Collectors.toCollection(LinkedHashSet::new));
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf(
                TagHelper.registryNameComparator(), sortedEntries.stream().flatMap(tagentry ->
                {
                    List<T> tagEntries = new ArrayList<>();
                    tagentry.populate(tagEntries);
                    return tagEntries.stream();
                })
                        .collect(Collectors.toList()));
        return new ImmutableTag<>(id, items, ImmutableList.copyOf(sortedEntries));
    }

    /**
     * Like {@link #copyOf(Tag)} except that {@link Tag.TagEntry}'s won't be flattened out into Singleton {@link Tag.ListEntry}, but preserved and sorted.
     * Hereby sorting is
     *
     * @param tag The tag to copy
     * @param <T> The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyPreserveTagStructure(Tag<T> tag)
    {
        return copyPreserveTagStructure(tag.getId(), tag.getEntries());
    }

    public static <T extends IForgeRegistryEntry<T>> Builder<T> builder()
    {
        return new Builder<>();
    }

    private ImmutableTag(ResourceLocation resourceLocationIn, ImmutableSortedSet<T> sortedItems, ImmutableList<ITagEntry<T>> entriesIn)
    {
        super(resourceLocationIn, sortedItems, entriesIn);
    }

    @Override
    public ImmutableSortedSet<T> getAllElements()
    {
        return (ImmutableSortedSet<T>) super.getAllElements();
    }

    @Override
    public ImmutableList<ITagEntry<T>> getEntries()
    {
        return (ImmutableList<ITagEntry<T>>) super.getEntries();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("ImmutableTag{\n");
        ImmutableList<ITagEntry<T>> entries = getEntries();
        for (int i = 0; i<entries.size(); ++i)
        {
            ITagEntry<T> entry = entries.get(i);
            builder.append('"');
            if (entry instanceof Tag.TagEntry) {
                builder.append('#');
                builder.append(((TagEntry<T>) entry).getSerializedId());
            }
            else {
                Collection<T> subElements = new LinkedList<>();
                entry.populate(subElements);
                for (T object: subElements)
                {
                    if (object.getRegistryName()!=null)
                    {
                        builder.append(object.getRegistryName());
                        builder.append(';');
                    }
                }
            }
            builder.append('"');
            if (i < entries.size()-1) {
                builder.append(',');
            }
            builder.append("\n");
        }
        builder.append("}");
        return builder.toString();
    }

    public static final class Builder<T extends IForgeRegistryEntry<T>> extends SortedTagBuilder<T> {
        private Builder()
        {
            super(ImmutableTag::copyPreserveTagStructure);
        }

        @Override
        public Builder<T> add(ITagEntry<T> entry)
        {
            if (entry instanceof Tag.TagEntry)
                super.add(new Tag.TagEntry<>(ImmutableTag.copyPreserveTagStructure(((TagEntry<T>) entry).getTag())));
            else
                super.add(entry);
            return this;
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
            return (Builder<T>) super.add(ImmutableTag.copyPreserveTagStructure(tagIn));
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
            this.ordered(true); //whatever the case, the Immutable tag will order itself, so don't apply any special ordering
            return (ImmutableTag<T>) super.build(resourceLocationIn);
        }
    }
}
