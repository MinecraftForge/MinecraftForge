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
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class ImmutableTag<T extends IForgeRegistryEntry<T>> extends Tag<T> {
    private ImmutableTag(ResourceLocation resourceLocationIn, ImmutableSortedSet<T> sortedItems, ImmutableList<ITagEntry<T>> entriesIn)
    {
        super(resourceLocationIn, sortedItems, entriesIn);
    }

    /**
     * @param id  Te id for the Tag
     * @param <T> The entry type
     * @return an empty ImmutableTag with the specified id
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> empty(ResourceLocation id)
    {
        return new ImmutableTag<>(id, ImmutableSortedSet.<T>of(), ImmutableList.<Tag.ITagEntry<T>>of());
    }

    /**
     * @param tag The tag to copy
     * @param <T> The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyOf(Tag<T> tag)
    {
        return asTag(tag.getId(), tag.getAllElements());
    }

    /**
     * @param elements The elements to use
     * @param id       The tag to copy
     * @param <T>      The entry type
     * @return An immutable Tag representation of the given elements
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> asTag(ResourceLocation id, Iterable<T> elements)
    {
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf(TagHelper.registryNameComparator(), elements);
        ImmutableList.Builder<Tag.ITagEntry<T>> tagEntryBuilder = ImmutableList.builder();
        for (T item : items)
        {
            tagEntryBuilder.add(TagHelper.singletonEntry(item));
        }
        return new ImmutableTag<>(id, items, tagEntryBuilder.build());
    }

    /**
     * Creates an ImmutableTag from the given Elements, flattening out any Sub-Tags that might be contained in entries into Singleton {@link Tag.ListEntry} entries via {@link TagHelper#singletonEntry(Object)}.
     *
     * @param entries The elements to use
     * @param id      The tag to copy
     * @param <T>     The entry type
     * @return An immutable Tag representation of the given elements
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> asTag(ResourceLocation id, Collection<ITagEntry<T>> entries)
    {
        return asTag(id, TagHelper.collectTagEntries(Collectors.toList(), entries));
    }

    /**
     * Like {@link #copyOf(Tag)} except that the first Layer {@link Tag.TagEntry}'s won't be flattened out into Singleton {@link Tag.ListEntry}, but preserved and sorted.
     * Deeper TagStructures aren't preserved.
     * Hereby sorting is done via {@link TagHelper#sortTagEntries(Collection, UnaryOperator, Collector)}.
     *
     * @param entries The entries to copy
     * @param <T>     The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyPreserveShallowTagStructure(ResourceLocation id, Collection<ITagEntry<T>> entries)
    {
        //This kind of Sorting (immutableShallowCopyTransformer) will flatten out the Tag Structure contained in Tag.TagEntries - more efficient
        Set<ITagEntry<T>> sortedEntries = TagHelper.sortTagEntries(entries, TagHelper.immutableShallowCopyTransformer(), Collectors.toCollection(LinkedHashSet::new));
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf(
                TagHelper.registryNameComparator(), TagHelper.collectTagEntries(Collectors.toList(), entries));
        return new ImmutableTag<>(id, items, ImmutableList.copyOf(sortedEntries));
    }

    /**
     * Like {@link #copyOf(Tag)} except that the first Layer {@link Tag.TagEntry}'s won't be flattened out into Singleton {@link Tag.ListEntry}, but preserved and sorted.
     * Deeper TagStructures aren't preserved.
     * Hereby sorting is done via {@link TagHelper#sortTagEntries(Collection, UnaryOperator, Collector)}.
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
     * Like {@link #copyOf(Tag)} except that {@link Tag.TagEntry}'s won't be flattened out into Singleton {@link Tag.ListEntry}, but preserved and sorted.
     * Hereby sorting is done via {@link TagHelper#sortTagEntries(Collection, UnaryOperator, Collector)}.
     *
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
     * Hereby sorting is done via {@link TagHelper#sortTagEntries(Collection, UnaryOperator, Collector)}.
     *
     * @param tag The tag to copy
     * @param <T> The entry type
     * @return An Immutable copy of the given tag
     */
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyPreserveTagStructure(Tag<T> tag)
    {
        return copyPreserveTagStructure(tag.getId(), tag.getEntries());
    }

    /**
     * @param <T> The Tag Type
     * @return An {@link ImmutableTag.Builder} which can be used to create an ImmutableTag
     */
    public static <T extends IForgeRegistryEntry<T>> Builder<T> builder()
    {
        return new Builder<>();
    }

    /**
     * @return The {@link ImmutableSortedSet} backing this ImmutableTag
     */
    @Override
    public ImmutableSortedSet<T> getAllElements()
    {
        return (ImmutableSortedSet<T>) super.getAllElements();
    }

    /**
     * @return The {@link ImmutableList} backing this ImmutableTag. Notice that it will be sorted as defined by {@link TagHelper#sortTagEntries(Collection, UnaryOperator, Collector)}
     */
    @Override
    public ImmutableList<ITagEntry<T>> getEntries()
    {
        return (ImmutableList<ITagEntry<T>>) super.getEntries();
    }

    /**
     * @return A String representation of this ImmutableTag representing {@link #getEntries()}.
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("ImmutableTag{\n");
        ImmutableList<ITagEntry<T>> entries = getEntries();
        for (int i = 0; i < entries.size(); ++i)
        {
            ITagEntry<T> entry = entries.get(i);
            builder.append('"');
            if (entry instanceof Tag.TagEntry)
            {
                builder.append('#');
                builder.append(((TagEntry<T>) entry).getSerializedId());
            } else
            {
                Collection<T> subElements = new LinkedList<>();
                entry.populate(subElements);
                for (T object : subElements)
                {
                    if (object.getRegistryName() != null)
                    {
                        builder.append(object.getRegistryName());
                        builder.append(';');
                    }
                }
            }
            builder.append('"');
            if (i < entries.size() - 1)
            {
                builder.append(',');
            }
            builder.append("\n");
        }
        builder.append("}");
        return builder.toString();
    }

    public static final class Builder<T extends IForgeRegistryEntry<T>> extends SortedTagBuilder<T> {
        private boolean resolved;

        private Builder()
        {
            super(ImmutableTag::copyPreserveTagStructure);
            this.resolved = false;
        }

        /**
         * @return Whether or not this Builder could be resolved
         */
        public boolean isResolved()
        {
            return resolved;
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
        public boolean resolve(Function<ResourceLocation, Tag<T>> resourceLocationToTag)
        {
            this.resolved = super.resolve(resourceLocationToTag);
            return resolved;
        }

        /**
         * Using this build will be preserve the TagStructure, if this Builder was resolved.
         * Otherwise {@link #buildCopy(ResourceLocation)} will be used to prevent mutation of {@link Tag.TagEntry} by resolving them after the ImmutableTag was build.
         *
         * @param resourceLocationIn The new TagId
         * @return An ImmutableTag created via {@link #copyPreserveTagStructure(ResourceLocation, Collection)} or {@link #asTag(ResourceLocation, Collection)}
         */
        @Override
        public ImmutableTag<T> build(ResourceLocation resourceLocationIn)
        {
            if (!this.isResolved())
                return buildCopy(resourceLocationIn); //Prevent mutation by resolving Tag.TagEntries after the ImmutableTag was build
            this.ordered(true); //whatever the case, the Immutable tag will order itself, so don't apply any special ordering
            return (ImmutableTag<T>) super.build(resourceLocationIn);
        }

        /**
         * Using this build, the TagStructure will be lost, but there won't be the overhead of {@link #copyPreserveTagStructure(ResourceLocation, Collection)}.
         * Additionally, the behaviour doesn't change no matter whether this Builder was resolved, or not.
         *
         * @param resourceLocationIn The new TagId
         * @return An ImmutableTag created via {@link #asTag(ResourceLocation, Collection)}
         */
        public ImmutableTag<T> buildCopy(ResourceLocation resourceLocationIn)
        {
            this.ordered(true); //whatever the case, the Immutable tag will order itself, so don't apply any special ordering
            return ImmutableTag.asTag(resourceLocationIn, this.entries);
        }
    }
}
