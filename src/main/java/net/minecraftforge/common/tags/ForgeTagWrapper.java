package net.minecraftforge.common.tags;

import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeTagManager;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class ForgeTagWrapper<V extends IForgeRegistryEntry<V>> extends Tag<V> {
    private final IForgeRegistry<V> reg;
    private int lastGeneration;
    private Tag<V> cachedTag;
    private UnaryOperator<Tag<V>> tagTransformer;

    public ForgeTagWrapper(ResourceLocation resourceLocationIn, IForgeRegistry<V> registry, @Nullable UnaryOperator<Tag<V>> tagTransformer)
    {
        super(resourceLocationIn);
        reg = Objects.requireNonNull(registry);
        if (!reg.supportsTagging())
        {
            throw new IllegalArgumentException("Cannot construct ForgeTagWrapper for Registry without tags!");
        }
        lastGeneration = -1;
        cachedTag = null;
        this.tagTransformer = tagTransformer != null ? tagTransformer : UnaryOperator.identity();
    }

    public ForgeTagWrapper(ResourceLocation resourceLocationIn, IForgeRegistry<V> registry)
    {
        this(resourceLocationIn, registry, null);
    }

    protected IForgeRegistry<V> getRegistry()
    {
        return reg;
    }

    protected int getLastGeneration()
    {
        return lastGeneration;
    }

    @Nullable
    protected Tag<V> getCachedTag()
    {
        return cachedTag;
    }

    protected void setLastGeneration(int lastGeneration)
    {
        this.lastGeneration = lastGeneration;
    }

    protected void setCachedTag(Tag<V> cachedTag)
    {
        this.cachedTag = cachedTag;
    }

    protected void setCachedTagTransformed(Tag<V> cachedTag)
    {
        setCachedTag(applyTransformer(cachedTag));
    }

    protected Tag<V> applyTransformer(Tag<V> tag)
    {
        return tagTransformer.apply(tag);
    }

    protected void setTagTransformer(UnaryOperator<Tag<V>> tagTransformer)
    {
        this.tagTransformer = tagTransformer;
    }

    @Override
    public Collection<V> getAllElements()
    {
        updateTag();
        assert getCachedTag() != null;
        return getCachedTag().getAllElements();
    }

    @Override
    public Collection<ITagEntry<V>> getEntries()
    {
        updateTag();
        assert getCachedTag() != null;
        return getCachedTag().getEntries();
    }

    /* Tag patch makes this call getAllElements - no need to override it
    @Override
    public boolean contains(V itemIn)
    {
        updateTag();
        assert getCachedTag()!=null;
        return getCachedTag().contains(itemIn);
    }*/

    /* Tag patch makes this call getEntries - no need to override it
    @Override
    public JsonObject serialize(Function<V, ResourceLocation> getNameForObject)
    {
        updateTag();
        assert getCachedTag()!=null;
        return getCachedTag().serialize(getNameForObject);
    }*/

    /* Because super implementation calls getAllElements() there is no need to override it (left here for documentation)
    @Override
    public V getRandomElement(Random random)
    {
        updateTag();
        assert getCachedTag()!=null;
        return getCachedTag().getRandomElement(random);
    }*/

    protected void updateTag()
    {
        if (getCachedTag() == null || ForgeTagManager.getGeneration() != getLastGeneration())
        {
            assert getRegistry().supportsTagging(); //should not have changed, since it was constructed
            setCachedTagTransformed(getRegistry().getTagProvider().getOrCreate(getId()));
        }
    }
}
