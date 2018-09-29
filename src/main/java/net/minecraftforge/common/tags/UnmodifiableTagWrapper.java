package net.minecraftforge.common.tags;

import com.google.common.base.Suppliers;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class UnmodifiableTagWrapper<T extends IForgeRegistryEntry<T>> extends ForgeTagWrapper<T> {

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, Supplier<IForgeRegistry<T>> registry, @Nullable UnaryOperator<Tag<T>> tagTransformer)
    {
        super(resourceLocationIn, registry, tagTransformer);
    }

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, Supplier<IForgeRegistry<T>> registry)
    {
        super(resourceLocationIn, registry);
    }

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, IForgeRegistry<T> registry, @Nullable UnaryOperator<Tag<T>> tagTransformer)
    {
        super(resourceLocationIn, Suppliers.ofInstance(registry),tagTransformer);
    }

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, ResourceLocation registry, @Nullable UnaryOperator<Tag<T>> tagTransformer)
    {
        this(resourceLocationIn, TagHelper.lazyRegistrySupplier(Objects.requireNonNull(registry, "Registry is required for updating the cachedTag")), tagTransformer);
    }

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, IForgeRegistry<T> registry)
    {
        super(resourceLocationIn, Suppliers.ofInstance(registry));
    }

    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, ResourceLocation registry)
    {
        this(resourceLocationIn, TagHelper.lazyRegistrySupplier(Objects.requireNonNull(registry, "Registry is required for updating the cachedTag")));
    }

    public UnmodifiableTagWrapper(Tag<T> tag, IForgeRegistry<T> registry)
    {
        this(tag.getId(), registry);
        setCachedTagTransformed(tag);
    }

    public UnmodifiableTagWrapper(Tag<T> tag, ResourceLocation registry)
    {
        this(tag.getId(), registry);
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
