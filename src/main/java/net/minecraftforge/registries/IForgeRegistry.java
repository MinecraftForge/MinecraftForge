/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Main interface for the registry system. Use this to query the registry system.
 *
 * @param <V> The top level type for the registry
 */
public interface IForgeRegistry<V extends IForgeRegistryEntry<V>> extends Iterable<V>
{
    ResourceLocation getRegistryName();
    Class<V> getRegistrySuperType();

    void register(V value);

    @SuppressWarnings("unchecked")
    void registerAll(V... values);

    boolean containsKey(ResourceLocation key);
    boolean containsValue(V value);
    boolean isEmpty();

    @Nullable V getValue(ResourceLocation key);
    @Nullable ResourceLocation getKey(V value);
    @Nullable ResourceLocation getDefaultKey();
    @Nullable Optional<ResourceKey<V>> getResourceKey(V value);

    @Nonnull Set<ResourceLocation>         getKeys();
    @Nonnull Collection<V>                 getValues();
    @Nonnull Set<Entry<ResourceKey<V>, V>> getEntries();

    @Nonnull Codec<V> getCodec();

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
    @FunctionalInterface
    interface AddCallback<V extends IForgeRegistryEntry<V>>
    {
        void onAdd(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, V obj, @Nullable V oldObj);
    }

    /**
     * Callback fired when the registry is cleared. This is done before a registry is reloaded from client
     * or server.
     */
    @FunctionalInterface
    interface ClearCallback<V extends IForgeRegistryEntry<V>>
    {
        void onClear(IForgeRegistryInternal<V> owner, RegistryManager stage);
    }

    /**
     * Callback fired when a registry instance is created. Populate slave maps here.
     */
    @FunctionalInterface
    interface CreateCallback<V extends IForgeRegistryEntry<V>>
    {
        void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage);
    }

    /**
     * Callback fired when the registry contents are validated.
     */
    @FunctionalInterface
    interface ValidateCallback<V extends IForgeRegistryEntry<V>>
    {
        void onValidate(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, ResourceLocation key, V obj);
    }

    /**
     * Callback fired when the registry is done processing. Used to calculate state ID maps.
     */
    @FunctionalInterface
    interface BakeCallback<V extends IForgeRegistryEntry<V>>
    {
        void onBake(IForgeRegistryInternal<V> owner, RegistryManager stage);
    }

    /**
     * Factory for creating dummy entries, allowing worlds to be loaded and keep the missing block references.
     */
    @FunctionalInterface
    interface DummyFactory<V extends IForgeRegistryEntry<V>>
    {
        V createDummy(ResourceLocation key);
    }

    /**
     *
     */
    @FunctionalInterface
    interface MissingFactory<V extends IForgeRegistryEntry<V>>
    {
        V createMissing(ResourceLocation key, boolean isNetwork);
    }
}
