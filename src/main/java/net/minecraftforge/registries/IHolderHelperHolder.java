package net.minecraftforge.registries;

interface IHolderHelperHolder<T extends IForgeRegistryEntry<T>>
{
    NamespacedHolderHelper<T> getHolderHelper();
}
