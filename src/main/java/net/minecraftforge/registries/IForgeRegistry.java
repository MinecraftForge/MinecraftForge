/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.registries;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Main interface for the registry system. Use this to query the registry system.
 *
 * @param <V> The top level type for the registry
 */
public interface IForgeRegistry<V extends IForgeRegistryEntry<V>> extends Iterable<V>
{
    Class<V> getRegistrySuperType();

    void register(V value);

    void registerAll(@SuppressWarnings("unchecked") V... values);

    boolean containsKey(ResourceLocation key);
    boolean containsValue(V value);

    @Nullable V getValue(ResourceLocation key);
    @Nullable ResourceLocation getKey(V value);

    @Nonnull Set<ResourceLocation>           getKeys();
    /** @deprecated use {@link #getValuesCollection} */
    @Deprecated // TODO: remove in 1.13
    @Nonnull List<V>                         getValues();
    @Nonnull
    default Collection<V>                    getValuesCollection() { // TODO rename this to getValues in 1.13
        return getValues();
    }
    @Nonnull Set<Entry<ResourceLocation, V>> getEntries();

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
    interface AddCallback<V extends IForgeRegistryEntry<V>>
    {
        void onAdd(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, V obj, @Nullable V oldObj);
    }

    /**
     * Callback fired when the registry is cleared. This is done before a registry is reloaded from client
     * or server.
     */
    interface ClearCallback<V extends IForgeRegistryEntry<V>>
    {
        void onClear(IForgeRegistryInternal<V> owner, RegistryManager stage);
    }

    /**
     * Callback fired when a registry instance is created. Populate slave maps here.
     */
    interface CreateCallback<V extends IForgeRegistryEntry<V>>
    {
        void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage);
    }

    /**
     * Callback fired when the registry contents are validated.
     */
    interface ValidateCallback<V extends IForgeRegistryEntry<V>>
    {
        void onValidate(IForgeRegistryInternal<V> owner, RegistryManager stage, int id, ResourceLocation key, V obj);
    }

    /**
     * Factory for creating dummy entries, allowing worlds to be loaded and keep the missing block references.
     */
    interface DummyFactory<V extends IForgeRegistryEntry<V>>
    {
        V createDummy(ResourceLocation key);
    }

    /**
     *
     */
    interface MissingFactory<V extends IForgeRegistryEntry<V>>
    {
        V createMissing(ResourceLocation key, boolean isNetwork);
    }
}
