package net.minecraftforge.fml.common.registry;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

/**
 * Main interface for the registry system. Use this to query the registry system.
 *
 * @param <V> The top level type for the registry
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface IForgeRegistry<V extends IForgeRegistryEntry<V>> extends Iterable<V>
{
    public Class<V> getRegistrySuperType();

    void register(V value);

    boolean containsKey(ResourceLocation key);
    boolean containsValue(V value);

    V getValue(ResourceLocation key);
    ResourceLocation getKey(V value);

    Set<ResourceLocation>           getKeys();
    List<V>                         getValues();
    Set<Entry<ResourceLocation, V>> getEntries();

    /**
     * Retrieve the slave map of type T from the registry.
     * Slave maps are maps which are dependent on registry content in some way.
     * @param slaveMapName The name of the slavemap
     * @param type The type
     * @param <T> Type to return
     * @return The slavemap if present
     */
    <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type);

    /**
     * Callback fired when objects are added to the registry. This will fire when the registry is rebuilt
     * on the client side from a server side synchronization, or when a world is loaded.
     */
    interface AddCallback<V>
    {
        void onAdd(V obj, int id, Map<ResourceLocation, ?> slaveset);
    }

    /**
     * Callback fired when the registry is cleared. This is done before a registry is reloaded from client
     * or server.
     */
    interface ClearCallback<V>
    {
        void onClear(Map<ResourceLocation, ?> slaveset);
    }

    /**
     * Callback fired when a registry instance is created. Populate slave maps here.
     */
    interface CreateCallback<V>
    {
        void onCreate(Map<ResourceLocation, ?> slaveset);
    }
}
