package net.minecraftforge.common.tags;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.ITagEntry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ForgeTagBuilder<T> extends Tag.Builder<T>
{
    /**
     *
     * @param <T> The type of {@link IForgeRegistryEntry}. Completely irrelevant for the Comparator. Just here to save People using it from unnecessary casting.
     * @return A comparator comparing {@link IForgeRegistryEntry} by their registryName's naturalOrder ({@link ResourceLocation} comparison).
     */
    public static <T> Comparator<IForgeRegistryEntry<T>> registryNameComparator()
    {
        return Comparator.comparing(IForgeRegistryEntry::getRegistryName);
    }

    /**
     *
     * @param <T> The type of {@link Tag}. Completely irrelevant for the Comparator. Just here to save People using it from unnecessary casting.
     * @return A comparator comparing {@link Tag}'s by their id's naturalOrder ({@link ResourceLocation} comparison).
     */
    public static <T> Comparator<Tag<T>> tagIdComparator()
    {
        return Comparator.comparing(Tag::getId);
    }

    /**
     *
     * @param <T> The type of {@link ITagEntry}. Completely irrelevant for the Comparator. Just here to save People using it from unnecessary casting.
     * @return A Comparator comparing vanilla {@link ITagEntry}'s: sorting {@link Tag.ListEntry} and non-vanilla ITagEntry sub-classes to the front.
     *         {@link Tag.TagEntry} will be sorted to the end, ordered by their serializedId's ({@link ResourceLocation} comparison).
     */
    public static <T> Comparator<ITagEntry<T>> tagEntryComparator()
    {
        return Comparator.comparing(e -> (e instanceof Tag.TagEntry ? ((Tag.TagEntry<T>) e).getSerializedId() : null), Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    private static <T> void populateEntries(Iterable<ITagEntry<T>> entries, Consumer<Collection<T>> consumer)
    {
        List<T> list = new ArrayList<>();
        for (ITagEntry<T> entry : entries)
        {
            entry.populate(list);
            consumer.accept(list);
            list.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Comparator<T> uncheckedComparator(@Nonnull Comparator<?> comparator)
    {
        return (Comparator<T>) comparator;
    }

    @Nullable
    private static <T> Comparator<T> tryCreateComparatorFor(Iterator<ITagEntry<T>> it)
    {
        List<T> testItems = new ArrayList<>();
        while (testItems.isEmpty() && it.hasNext())
            it.next().populate(testItems);
        if (testItems.isEmpty())
            return null;
        else if (testItems.get(0) instanceof Comparable)
            return uncheckedComparator(Comparator.naturalOrder());
        else if (testItems.get(0) instanceof IForgeRegistryEntry)
            return uncheckedComparator(registryNameComparator());
        return null;
    }

    public static <T> Tag<T> copyOf(ResourceLocation resourceLocationIn, Iterable<ITagEntry<T>> entries, boolean preserveOrder)
    {
        Iterator<ITagEntry<T>> it = entries.iterator();
        if (!it.hasNext()) return empty(resourceLocationIn);
        if (preserveOrder) return copyOf(resourceLocationIn, entries, null, null);
        return copyOf(resourceLocationIn, entries, tryCreateComparatorFor(it),tagEntryComparator());
    }

    public static <T> Tag<T> copyOf(ResourceLocation resourceLocationIn, Iterable<ITagEntry<T>> entries, @Nullable Comparator<T> comparator)
    {
        return copyOf(resourceLocationIn, entries, comparator, comparator!=null?tagEntryComparator():null);
    }

    public static <T> Tag<T> copyOf(ResourceLocation resourceLocationIn, Iterable<ITagEntry<T>> entries, @Nullable Comparator<T> comparator, @Nullable Comparator<ITagEntry<T>> entryComparator)
    {
        ImmutableList<ITagEntry<T>> entryList = (entryComparator != null ? ImmutableList.sortedCopyOf(entryComparator, entries) : ImmutableList.copyOf(entries));
        ImmutableSet.Builder<T> builder = comparator != null ? ImmutableSortedSet.orderedBy(comparator) : ImmutableSet.builder();
        populateEntries(entryList, builder::addAll);
        return new Tag<>(resourceLocationIn, entryList, builder.build());
    }

    public static <T> Tag<T> empty(ResourceLocation id)
    {
        return new Tag<>(id, ImmutableList.of(), ImmutableSortedSet.of());
    }

    public static <T> ForgeTagBuilder<T> vanillaTagBuilder()
    {
        return new ForgeTagBuilder<>((location, iTagEntries, preserveOrder, itemComparator) -> new Tag<>(location, iTagEntries, preserveOrder));
    }

    public static <T> ForgeTagBuilder immutableTagBuilder()
    {
        return new ForgeTagBuilder<>((TagFactory<T>) (location, iTagEntries, preserveOrder, itemComparator) ->
                itemComparator!=null && !preserveOrder? copyOf(location,iTagEntries,itemComparator):copyOf(location, iTagEntries, preserveOrder));
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private boolean resolved;
    private final TagFactory<T> factory;
    private Comparator<T> comparator;

    public ForgeTagBuilder(TagFactory<T> factory)
    {
        this.resolved = false;
        this.factory = factory;
        this.comparator = null;
    }

    public ForgeTagBuilder<T> comparingEntriesBy(Comparator<T> comparator)
    {
        this.comparator = comparator;
        return this;
    }

    /**
     * @return Whether or not this Builder could be resolved
     */
    public boolean isResolved()
    {
        return resolved;
    }

    protected void onChange()
    {
        this.resolved = false;
    }

    @Nullable
    public Comparator<T> getEntryOrdering()
    {
        return comparator;
    }

    @Override
    public ForgeTagBuilder<T> add(ITagEntry<T> entry)
    {
        onChange();
        return (ForgeTagBuilder<T>) super.add(entry);
    }

    @Override
    public ForgeTagBuilder<T> add(T itemIn)
    {
        onChange();
        return (ForgeTagBuilder<T>) super.add(itemIn);
    }

    @Override
    public ForgeTagBuilder<T> addAll(Collection<T> itemsIn)
    {
        onChange();
        return (ForgeTagBuilder<T>) super.addAll(itemsIn);
    }


    public ForgeTagBuilder<T> addAllTags(Iterable<ResourceLocation> itemsIn)
    {
        for (ResourceLocation location : itemsIn)
        {
            add(location);
        }
        return this;
    }

    @Override
    public ForgeTagBuilder<T> add(ResourceLocation resourceLocationIn)
    {
        onChange();
        return (ForgeTagBuilder<T>) super.add(resourceLocationIn);
    }

    @Override
    public ForgeTagBuilder<T> add(Tag<T> tagIn)
    {
        onChange();
        return (ForgeTagBuilder<T>) super.add(tagIn);
    }

    /**
     * @param preserveOrderIn whether or not the Order in which TagEntries were added should be preserved
     * @return this Builder, in order to support Method chaining
     */
    @Override
    public ForgeTagBuilder<T> ordered(boolean preserveOrderIn)
    {
        return (ForgeTagBuilder<T>) super.ordered(preserveOrderIn);
    }

    public ForgeTagBuilder<T> deserialize(Predicate<ResourceLocation> isValueKnownPredicate, Function<ResourceLocation, T> objectGetter, JsonObject json)
    {
        JsonArray jsonarray = JsonUtils.getJsonArray(json, "values");
        if (JsonUtils.getBoolean(json, "replace", false))
        {
            this.entries.clear();
        }
        Set<ResourceLocation> missingEntries = new LinkedHashSet<>(); //preserve Order
        for (JsonElement jsonelement : jsonarray)
        {
            String s = JsonUtils.getString(jsonelement, "value");
            if (!s.startsWith("#"))
            {
                ResourceLocation resourcelocation = new ResourceLocation(s);
                T t = objectGetter.apply(resourcelocation);
                if (t == null || !isValueKnownPredicate.test(resourcelocation)) missingEntries.add(resourcelocation);
                else this.add(t);
            } else
            {
                this.add(new ResourceLocation(s.substring(1)));
            }
        }

        if (!missingEntries.isEmpty())
            throw new MissingEntriesException(missingEntries);

        return this;
    }

    @Override
    public boolean resolve(Function<ResourceLocation, Tag<T>> resourceLocationToTag)
    {
        if (!isResolved())
            this.resolved = super.resolve(resourceLocationToTag);
        return isResolved();
    }

    public Set<ResourceLocation> findResolveFailures(Function<ResourceLocation, Tag<T>> resourceLocationToTag)
    {
        Set<ResourceLocation> tags = new LinkedHashSet<>(); //preserve Order
        for (ITagEntry<T> entry : this.entries)
        {
            if ((entry instanceof Tag.TagEntry) && !entry.resolve(resourceLocationToTag))
                tags.add(((Tag.TagEntry<T>) entry).getSerializedId());
        }
        return tags;
    }

    @Override
    public Tag<T> build(ResourceLocation resourceLocationIn)
    {
        return build(resourceLocationIn, getEntryOrdering());
    }

    public Tag<T> build(ResourceLocation resourceLocationIn, @Nullable Comparator<T> itemComparator)
    {
        if (!resolved) LOGGER.trace("Building unresolved Tag!");
        return factory.build(resourceLocationIn, entries, preserveOrder, itemComparator);
    }

    @FunctionalInterface
    public interface TagFactory<T>
    {
        public Tag<T> build(ResourceLocation location, Set<ITagEntry<T>> entries, boolean preserveOrder, @Nullable Comparator<T> entryComparator);
    }
}
