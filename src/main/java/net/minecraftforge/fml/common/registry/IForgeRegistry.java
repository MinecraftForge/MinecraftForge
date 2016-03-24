package net.minecraftforge.fml.common.registry;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public interface IForgeRegistry<V extends IForgeRegistryEntry<V>> extends Iterable<V>
{
    void register(V value);

    boolean contains(ResourceLocation key);
    boolean contains(V value);

    V                get(ResourceLocation key);
    ResourceLocation get(V value);

    Set<ResourceLocation>           getKeys();
    List<V>                         getValues();
    Set<Entry<ResourceLocation, V>> getEntries();
}
