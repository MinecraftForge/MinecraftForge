package net.minecraftforge.common.tags;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collections;

public final class ImmutableTag<T extends IForgeRegistryEntry<T>> extends Tag<T> {
    public static <T extends IForgeRegistryEntry<T>> ImmutableTag<T> copyOf(Tag<T> tag)
    {
        ImmutableSortedSet<T> items = ImmutableSortedSet.copyOf((o1, o2) -> {
            if (o1 == o2) return 0;
            if (o1.getRegistryName() == null)
            {
                if (o2.getRegistryName() == null)
                {
                    return 0;
                } else
                {
                    return -1;
                }
            } else if (o2.getRegistryName() == null)
            {
                return 1;
            } else
            {
                return o1.getRegistryName().compareTo(o2.getRegistryName());
            }
        }, tag.getAllElements());
        ImmutableList.Builder<Tag.ITagEntry<T>> tagEntryBuilder = ImmutableList.builder();
        for (T item : items)
        {
            tagEntryBuilder.add(new Tag.ListEntry<>(Collections.singleton(item)));
        }
        return new ImmutableTag<T>(tag.getId(), items, tagEntryBuilder.build());
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
}
