package net.minecraftforge.common.tags;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.reflect.TypeToken;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.tags.ForgeTagBuilder.TagFactory;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ImmutableTag<T> extends Tag<T> {

    public static  Comparator<IForgeRegistryEntry<?>> registryNameComparator() {
        return Comparator.nullsFirst(Comparator.<IForgeRegistryEntry<?>,ResourceLocation>comparing(IForgeRegistryEntry::getRegistryName,Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    public static <T> Comparator<Tag<T>> tagIdComparator()
    {
        return Comparator.nullsFirst(Comparator.<Tag<T>,ResourceLocation>comparing(Tag::getId,Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    //compares Vanilla TagEntries, sorting ListEntries and non-vanilla ITagEntry sub-classes to the front
    public static <T> Comparator<ITagEntry<T>> tagEntryComparator()
    {
        return Comparator.nullsFirst(Comparator.<ITagEntry<T>,ResourceLocation>comparing(e -> (e instanceof TagEntry? ((TagEntry<T>) e).getSerializedId():null),Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    private static <T> void populateEntries(Iterable<ITagEntry<T>> entries, Consumer<Collection<T>> consumer)
    {
        List<T> list = new ArrayList<>();
        for (ITagEntry<T> entry:entries) {
            entry.populate(list);
            consumer.accept(list);
            list.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Comparator<T> uncheckedComparator(@Nullable Comparator<?> comparator)
    {
        return (Comparator<T>) comparator;
    }

    public static <T> ImmutableTag<T> copyOf(ResourceLocation resourceLocationIn, Iterable<ITagEntry<T>> entries, boolean preserveOrder)
    {
        if (preserveOrder) return copyOf(resourceLocationIn,entries,null,null);
        TypeToken<?> valueToken = new TypeToken<ITagEntry<T>>(){}.resolveType(ITagEntry.class.getTypeParameters()[0]);
        Comparator<T> valueComparator = null;

        if (Comparable.class.isAssignableFrom(valueToken.getRawType()))
            valueComparator = uncheckedComparator(Comparator.naturalOrder());
        else if (IForgeRegistryEntry.class.isAssignableFrom(valueToken.getRawType()))
            valueComparator = uncheckedComparator(registryNameComparator());

        return copyOf(resourceLocationIn,entries,valueComparator);
    }

    public static <T> ImmutableTag<T> copyOf(ResourceLocation resourceLocationIn, Iterable<ITagEntry<T>> entries, @Nullable Comparator<T> comparator) {
        return copyOf(resourceLocationIn,entries,comparator,tagEntryComparator());
    }

    public static <T> ImmutableTag<T> copyOf(ResourceLocation resourceLocationIn, Iterable<ITagEntry<T>> entries, @Nullable Comparator<T> comparator, @Nullable Comparator<ITagEntry<T>> entryComparator)
    {
        ImmutableList<ITagEntry<T>> entryList = (entryComparator!=null?ImmutableList.sortedCopyOf(entryComparator,entries):ImmutableList.copyOf(entries));
        ImmutableSet.Builder<T> builder = comparator!=null?ImmutableSortedSet.orderedBy(comparator):ImmutableSet.builder();
        populateEntries(entryList,builder::addAll);
        return new ImmutableTag<>(resourceLocationIn, entryList, builder.build());
    }

    public static <T> ImmutableTag<T> empty(ResourceLocation id)
    {
        return new ImmutableTag<>(id,ImmutableList.of(),ImmutableSortedSet.of());
    }

    public static <T> ForgeTagBuilder<T> builder() {
        return new ForgeTagBuilder<T>(new TagFactory<T>() {
            @Override
            public Tag<T> build(ResourceLocation location, Set<ITagEntry<T>> iTagEntries, boolean preserveOrder) {
                return copyOf(location,iTagEntries,preserveOrder);
            }

            @Override
            public Tag<T> build(ResourceLocation location, Set<ITagEntry<T>> iTagEntries, Comparator<T> itemComparator) {
                return copyOf(location,iTagEntries,itemComparator,tagEntryComparator());
            }
        });
    }

    protected ImmutableTag(ResourceLocation resourceLocationIn, ImmutableList<ITagEntry<T>> entriesIn, ImmutableSet<T> taggedItems)
    {
        super(resourceLocationIn, entriesIn, taggedItems);
    }

    @Override
    public ImmutableSet<T> getAllElements()
    {
        return (ImmutableSet<T>) super.getAllElements();
    }

    @Override
    public ImmutableList<ITagEntry<T>> getEntries()
    {
        return (ImmutableList<ITagEntry<T>>) super.getEntries();
    }

    @Override
    public T getRandomElement(Random random)
    {
        List<T> list = getAllElements().asList();
        return list.get(random.nextInt(list.size()));
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
                for (T item : subElements)
                {
                    builder.append(item.toString());
                    builder.append(';');
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

}
