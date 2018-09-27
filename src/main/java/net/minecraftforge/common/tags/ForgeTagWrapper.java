package net.minecraftforge.common.tags;

import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeTagManager;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;

public class ForgeTagWrapper<T extends IForgeRegistryEntry<T>> extends Tag<T> {
    private final IForgeRegistry<T> reg;
    private int lastGeneration;
    private Tag<T> cachedTag;

    public ForgeTagWrapper(ResourceLocation resourceLocationIn, IForgeRegistry<T> registry)
    {
        super(resourceLocationIn);
        reg = Objects.requireNonNull(registry);
        if (!reg.supportsTagging())
        {
            throw new IllegalArgumentException("Cannot construct ForgeTagWrapper for Registry without tags!");
        }
        lastGeneration = -1;
        cachedTag = null;
    }

    protected IForgeRegistry<T> getRegistry()
    {
        return reg;
    }

    protected int getLastGeneration()
    {
        return lastGeneration;
    }

    @Nullable
    protected Tag<T> getCachedTag()
    {
        return cachedTag;
    }

    protected void setLastGeneration(int lastGeneration)
    {
        this.lastGeneration = lastGeneration;
    }

    protected void setCachedTag(Tag<T> cachedTag)
    {
        this.cachedTag = cachedTag;
    }

    @Override
    public Collection<T> getAllElements()
    {
        updateTag();
        assert getCachedTag() != null;
        return getCachedTag().getAllElements();
    }

    @Override
    public Collection<ITagEntry<T>> getEntries()
    {
        updateTag();
        assert getCachedTag() != null;
        return getCachedTag().getEntries();
    }

    /* Tag patch makes this call getAllElements - no need to override it
    @Override
    public boolean contains(T itemIn)
    {
        updateTag();
        assert getCachedTag()!=null;
        return getCachedTag().contains(itemIn);
    }*/

    /* Tag patch makes this call getEntries - no need to override it
    @Override
    public JsonObject serialize(Function<T, ResourceLocation> getNameForObject)
    {
        updateTag();
        assert getCachedTag()!=null;
        return getCachedTag().serialize(getNameForObject);
    }*/

    /* Because super implementation calls getAllElements() there is no need to override it (left here for documentation)
    @Override
    public T getRandomElement(Random random)
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
            setCachedTag(getRegistry().getTagProvider().getOrCreate(getId()));
        }
    }
}
