package net.minecraftforge.common.tags;

import com.google.gson.JsonObject;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Builder;
import net.minecraft.tags.Tag.ITagEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class SortedTagBuilder<T extends IForgeRegistryEntry<T>> extends Builder<T> {
    private BiFunction<ResourceLocation,Set<ITagEntry<T>>,Tag<T>> tagFactory;

    public static <T extends IForgeRegistryEntry<T>> SortedTagBuilder<T> newInstance() {
        return new SortedTagBuilder<>((location,entries) -> new Tag<>(location,entries,true));
    }

    public static <T extends IForgeRegistryEntry<T>> SortedTagBuilder<T> withTagInstance(BiFunction<ResourceLocation,Set<ITagEntry<T>>,Tag<T>> function) {
        return new SortedTagBuilder<>(function);
    }

    protected SortedTagBuilder(BiFunction<ResourceLocation, Set<ITagEntry<T>>, Tag<T>> tagFactory) {
        this.tagFactory = tagFactory;
        this.preserveOrder = false;
    }

    @Override
    public SortedTagBuilder<T> add(ITagEntry<T> entry) {
        return (SortedTagBuilder<T>) super.add(entry);
    }

    @Override
    public SortedTagBuilder<T> add(T itemIn) {
        return (SortedTagBuilder<T>) super.add(itemIn);
    }

    @Override
    public SortedTagBuilder<T> addAll(Collection<T> itemsIn) {
        return (SortedTagBuilder<T>) super.addAll(itemsIn);
    }

    /**
     *
     * @param itemsIn An {@link Iterable} of ResourceLocations describing tags, which should be added
     * @return The Builder instance for Method chaining
     */
    public SortedTagBuilder<T> addAllTags(Iterable<ResourceLocation> itemsIn) {
        for (ResourceLocation location: itemsIn) {
            add(location);
        }
        return this;
    }

    @Override
    public SortedTagBuilder<T> add(ResourceLocation resourceLocationIn) {
        return (SortedTagBuilder<T>) super.add(resourceLocationIn);
    }

    @Override
    public SortedTagBuilder<T> add(Tag<T> tagIn) {
        return (SortedTagBuilder<T>) super.add(tagIn);
    }


    /**
     * Notice that setting this to true will prevent the {@code SortedTagBuilder} from sorting the Result before creating the Tag.
     * False by default.
     * @param preserveOrderIn whether or not the Order in which TagEntries were added should be preserved
     * @return The Builder instance for Method chaining
     */
    @Override
    public SortedTagBuilder<T> ordered(boolean preserveOrderIn) {
        return (SortedTagBuilder<T>) super.ordered(preserveOrderIn);
    }

    @Override
    public SortedTagBuilder<T> deserialize(Predicate<ResourceLocation> isValueKnownPredicate, Function<ResourceLocation, T> objectGetter, JsonObject json) {
        return (SortedTagBuilder<T>) super.deserialize(isValueKnownPredicate, objectGetter, json);
    }

    public Set<ResourceLocation> findResolveFailures(Function<ResourceLocation, Tag<T>> resourceLocationToTag) {
        Set<ResourceLocation> tags = new LinkedHashSet<>(); //preserve Order
        for (Tag.ITagEntry<T> entry: this.entries)
        {
            if ((entry instanceof Tag.TagEntry) && !entry.resolve(resourceLocationToTag))
                tags.add(((Tag.TagEntry<T>) entry).getSerializedId());
        }
        return tags;
    }

    @Override
    public Tag<T> build(ResourceLocation resourceLocationIn) {
        if (!this.preserveOrder) {
            Set<ITagEntry<T>> sortedEntries = TagHelper.sortTagEntries(this.entries);
            return tagFactory.apply(resourceLocationIn,sortedEntries);
        }
        else {
            return tagFactory.apply(resourceLocationIn,this.entries);
        }
    }
}
