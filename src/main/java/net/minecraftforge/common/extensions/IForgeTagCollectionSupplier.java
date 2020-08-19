package net.minecraftforge.common.extensions;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IForgeTagCollectionSupplier
{

    default Map<ResourceLocation, ITagCollection<?>> modded()
    {
        return ImmutableMap.of();
    }

    //TODO: Nullable or error?
    default ITagCollection<?> getModdedCollection(ResourceLocation regName)
    {
        return modded().get(regName);
    }

    //TODO: Nullable or error?
    default <T extends IForgeRegistryEntry<T>> ITagCollection<T> getModdedCollection(IForgeRegistry<T> reg)
    {
        return (ITagCollection<T>) getModdedCollection(reg.getRegistryName());
    }
}