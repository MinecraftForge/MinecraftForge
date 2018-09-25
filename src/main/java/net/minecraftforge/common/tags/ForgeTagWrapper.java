package net.minecraftforge.common.tags;

import com.google.gson.JsonObject;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeTagManager;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class ForgeTagWrapper<T extends IForgeRegistryEntry<T>> extends Tag<T> {
    @Nonnull
    private final IForgeRegistry<T> reg;
    private int lastGeneration;
    private Tag<T> cachedTag;

    public ForgeTagWrapper(@Nonnull ResourceLocation resourceLocationIn, IForgeRegistry<T> registry) {
        super(resourceLocationIn);
        reg = Objects.requireNonNull(registry);
        if (reg.getTagDelegate() == null)
            throw new IllegalArgumentException("Cannot construct ForgeTagWrapper for Registry without tags!");
        lastGeneration = -1;
        cachedTag = null;
    }

    @Nonnull
    protected IForgeRegistry<T> getRegistry() {
        return reg;
    }

    protected int getLastGeneration() {
        return lastGeneration;
    }

    protected Tag<T> getCachedTag() {
        return cachedTag;
    }

    protected void setLastGeneration(int lastGeneration) {
        this.lastGeneration = lastGeneration;
    }

    protected void setCachedTag(Tag<T> cachedTag) {
        this.cachedTag = cachedTag;
    }

    @Override
    public boolean contains(T itemIn) {
        updateTag();
        return getCachedTag().contains(itemIn);
    }

    @Override
    public Collection<T> getAllElements() {
        updateTag();
        return getCachedTag().getAllElements();
    }

    @Override
    public Collection<ITagEntry<T>> getEntries() {
        updateTag();
        return getCachedTag().getEntries();
    }

    @Override
    public JsonObject serialize(Function<T, ResourceLocation> getNameForObject) {
        updateTag();
        return getCachedTag().serialize(getNameForObject);
    }

    /* Because super implementation calls getAllElements() there is no need to override it (left here for documentation)
    @Override
    public T getRandomElement(Random random) {
        updateTag();
        return getCachedTag().getRandomElement(random);
    }*/

    private void updateTag() {
        if (getCachedTag() == null || ForgeTagManager.getGeneration() != getLastGeneration()) {
            assert getRegistry().getTagDelegate() != null; //should not have changed, since it was constructed
            setCachedTag(getRegistry().getTagDelegate().getOrCreate(getId()));
        }
    }
}
