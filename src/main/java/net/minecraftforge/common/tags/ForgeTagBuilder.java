package net.minecraftforge.common.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.ITagEntry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ForgeTagBuilder<T> extends Tag.Builder<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean resolved;
    private final TagFactory<T> factory;

    public static <T> ForgeTagBuilder<T> vanillaTagBuilder() {
        return new ForgeTagBuilder<>(new TagFactory<T>() {
            @Override
            public Tag<T> build(ResourceLocation location, Set<ITagEntry<T>> iTagEntries, boolean preserveOrder) {
                return new Tag<>(location,iTagEntries,preserveOrder);
            }

            @Override
            public Tag<T> build(ResourceLocation location, Set<ITagEntry<T>> iTagEntries, Comparator<T> itemComparator) {
                return new Tag<>(location,iTagEntries,false);
            }
        });
    }

    public static <T> ForgeTagBuilder<T> immutableTagBuilder() {
        return ImmutableTag.builder();
    }

    public ForgeTagBuilder(TagFactory<T> factory)
    {
        this.resolved = false;
        this.factory = factory;
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

    @Override
    public ForgeTagBuilder<T> add(ITagEntry<T> entry)
    {
        onChange();
        super.add(entry);
        return this;
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
        onChange();
        for (ResourceLocation location:itemsIn)
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
     * @implNote Notice that this doesn't have any influence on the resulting ImmutableTag, because ImmutableTags require to be sorted.
     */
    @Override
    public ForgeTagBuilder<T> ordered(boolean preserveOrderIn)
    {
        onChange();
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

        if (!missingEntries.isEmpty()) {
            throw new MissingEntriesException(missingEntries);
        }

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
        if (!resolved) LOGGER.trace("Building unresolved Tag!");
        return factory.build(resourceLocationIn,entries,preserveOrder);
    }

    public Tag<T> build(ResourceLocation resourceLocationIn, Comparator<T> itemComparator)
    {
        if (!resolved) LOGGER.trace("Building unresolved Tag!");
        return factory.build(resourceLocationIn,entries,itemComparator);
    }

    public interface TagFactory<T> {
        public Tag<T> build(ResourceLocation location, Set<ITagEntry<T>> entries, boolean preserveOrder);

        public Tag<T> build(ResourceLocation location, Set<ITagEntry<T>> entries, Comparator<T> itemComparator);
    }
}
