package net.minecraftforge.common.tags;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.Collections;

public class UnmodifiableTagWrapper<T extends IForgeRegistryEntry<T>> extends ForgeTagWrapper<T> {
    public UnmodifiableTagWrapper(ResourceLocation resourceLocationIn, IForgeRegistry<T> registry)
    {
        super(resourceLocationIn, registry);
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
