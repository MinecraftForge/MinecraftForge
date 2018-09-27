package net.minecraftforge.common.tags;

import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class UnmodifiableTagWrapper<T extends IForgeRegistryEntry<T>> extends ForgeTagWrapper<T> {

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, IForgeRegistry<T> registry, @Nullable UnaryOperator<Tag<T>> tagTransformer)
    {
        super(resourceLocationIn, registry, tagTransformer);
    }

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, IForgeRegistry<T> registry)
    {
        super(resourceLocationIn, registry);
    }

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, ResourceLocation registry)
    {
        this(resourceLocationIn, Objects.requireNonNull(RegistryManager.ACTIVE.getRegistry(registry), "Registry is required for updating the cachedTag"));
    }

    public UnmodifiableTagWrapper(Tag<T> tag, IForgeRegistry<T> registry)
    {
        super(tag.getId(), registry);
        setCachedTagTransformed(tag);
    }

    public UnmodifiableTagWrapper(Tag<T> tag, ResourceLocation registry)
    {
        this(tag, Objects.requireNonNull(RegistryManager.ACTIVE.getRegistry(registry), "Registry is required for updating the cachedTag"));
    }

    @Override
    public Collection<T> getAllElements()
    {
        return Collections.unmodifiableCollection(super.getAllElements());
    }

    @Override
    public Collection<ITagEntry<T>> getEntries()
    {
        return Collections.unmodifiableCollection(super.getEntries());
    }
}
