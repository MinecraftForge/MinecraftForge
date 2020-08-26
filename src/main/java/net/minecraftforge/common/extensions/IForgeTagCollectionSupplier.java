package net.minecraftforge.common.extensions;

import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IForgeTagCollectionSupplier
{
    //TODO: Nullable or error?
    default ITagCollection<?> getModdedCollection(ResourceLocation regName)
    {
        return ForgeTagHandler.getCustomTagTypes().get(regName);
    }

    //TODO: Nullable or error?
    default <T extends IForgeRegistryEntry<T>> ITagCollection<T> getModdedCollection(IForgeRegistry<T> reg)
    {
        return (ITagCollection<T>) getModdedCollection(reg.getRegistryName());
    }
}