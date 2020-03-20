package net.minecraftforge.common.extensions;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;

import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.IOptionalTagEntry;

public interface IForgeTagBuilder<T>
{    
    default Tag.Builder<T> addOptional(Function<ResourceLocation, Optional<T>> valueGetter, Collection<ResourceLocation> locations)
    {
        return ((Tag.Builder<T>)this).add(new OptionalListEntry<>(locations, valueGetter));
    }

    default Tag.Builder<T> addOptionalTag(ResourceLocation tag)
    {
        return ((Tag.Builder<T>)this).add(new OptionalTagEntry<>(tag));
    }
}

class OptionalListEntry<T> implements IOptionalTagEntry<T>
{
    private final Collection<ResourceLocation> names;
    private final Function<ResourceLocation, Optional<T>> valueGetter;

    OptionalListEntry(Collection<ResourceLocation> names, Function<ResourceLocation, Optional<T>> valueGetter)
    {
        this.names = names;
        this.valueGetter = valueGetter;
    }

    @Override
    public void populate(Collection<T> itemsIn)
    {
        for (ResourceLocation rl : names)
        {
            valueGetter.apply(rl).ifPresent(itemsIn::add);
        }
    }

    @Override
    public void serialize(JsonArray array, Function<T, ResourceLocation> getNameForObject)
    {
        for (ResourceLocation rl : names)
        {
            array.add(rl.toString());
        }
    }
}

class OptionalTagEntry<T> extends Tag.TagEntry<T> implements IOptionalTagEntry<T>
{
    private Tag<T> resolvedTag = null;

    OptionalTagEntry(ResourceLocation referent)
    {
        super(referent);
    }

    @Override
    public boolean resolve(@Nonnull Function<ResourceLocation, Tag<T>> resolver)
    {
        if (this.resolvedTag == null)
        {
            this.resolvedTag = resolver.apply(this.getSerializedId());
        }
        return true; // never fail if resolver returns null
    }

    @Override
    public void populate(@Nonnull Collection<T> items)
    {
        if (this.resolvedTag != null)
        {
            items.addAll(this.resolvedTag.getAllElements());
        }
    }
}
