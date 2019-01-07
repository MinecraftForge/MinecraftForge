package net.minecraftforge.common.tags;

import com.google.gson.JsonObject;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class TagWrapper<T> extends Tag<T>
{
    private int lastKnownGeneration = -1;
    private Tag<T> cachedTag;
    private Supplier<TagCollection<T>> collectionProvider;

    public TagWrapper(ResourceLocation resourceLocationIn, Supplier<TagCollection<T>> collectionProvider)
    {
        super(resourceLocationIn);
        this.lastKnownGeneration = ForgeTagManager.getInstance().getGeneration() - 1;
        this.collectionProvider = collectionProvider;
    }

    public int getLastKnownGeneration()
    {
        return lastKnownGeneration;
    }

    protected void setLastKnownGeneration(int lastKnownGeneration)
    {
        this.lastKnownGeneration = lastKnownGeneration;
    }

    public Tag<T> getCachedTag()
    {
        return cachedTag;
    }

    protected void setCachedTag(Tag<T> cachedTag)
    {
        this.cachedTag = cachedTag;
    }

    public Supplier<TagCollection<T>> getCollectionProvider()
    {
        return collectionProvider;
    }

    private void updateTag()
    {
        if (this.getLastKnownGeneration() != ForgeTagManager.getInstance().getGeneration())
        {
            this.setCachedTag(getCollectionProvider().get().get(getId()));
            this.setLastKnownGeneration(ForgeTagManager.getInstance().getGeneration());
        }
    }

    public boolean contains(T itemIn)
    {
        updateTag();
        return this.getCachedTag().contains(itemIn);
    }

    public Collection<T> getAllElements()
    {
        updateTag();
        return this.getCachedTag().getAllElements();
    }

    public Collection<Tag.ITagEntry<T>> getEntries()
    {
        updateTag();
        return this.getCachedTag().getEntries();
    }

    @Override
    public JsonObject serialize(Function<T, ResourceLocation> getNameForObject)
    {
        updateTag();
        return getCachedTag().serialize(getNameForObject);
    }

    @Override
    public T getRandomElement(Random random)
    {
        updateTag();
        return getCachedTag().getRandomElement(random);
    }
}
