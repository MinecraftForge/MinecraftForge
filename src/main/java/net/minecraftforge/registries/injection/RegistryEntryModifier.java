/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.registries.injection;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all merge and injection operations to be applied to the given registry entry type's data.
 *
 * @param <E> The type of the registry entry that this modifier applies to.
 */
public class RegistryEntryModifier<E>
{
    private final RegistryKey<Registry<E>> registryKey;
    private final List<Merger<E>> mergers;
    private final List<Injector<E>> injectors;

    private RegistryEntryModifier(Builder<E> builder)
    {
        this.registryKey = builder.registryKey;
        this.mergers = ImmutableList.copyOf(builder.mergers);
        this.injectors = ImmutableList.copyOf(builder.injectors);
    }

    public boolean isEmpty()
    {
        return mergers.isEmpty() && injectors.isEmpty();
    }

    public boolean hasMergers()
    {
        return !mergers.isEmpty();
    }

    public boolean hasInjections()
    {
        return !injectors.isEmpty();
    }

    /**
     * Applies this modifier's merge operations on the given 'src' and 'dest' json data providing its
     * entryKey is a valid type for this modifier.
     *
     * @param entryKey The RegistryKey of the entry being merged.
     * @param dest     The json data that content will be merged into.
     * @param src      The json data from which content will be merged.
     * @throws IOException When an error occurs during the merge operations or if the entryKey is of an incorrect type.
     */
    public void mergeRaw(RegistryKey<?> entryKey, JsonElement dest, JsonElement src) throws IOException
    {
        RegistryKey<E> typedEntryKey = getTypedKey(entryKey);

        for (Merger<E> merger : mergers)
        {
            merger.merge(typedEntryKey, dest, src);
        }
    }

    /**
     * Applies this modifier's inject operations on the registry entry's json data providing its
     * entryKey is a valid type for this modifier.
     *
     * @param entryKey The RegistryKey of the entry being merged.
     * @param entryData The json data for the registry entry.
     * @throws IOException When an error occurs during the inject operations or if the entryKey is of an incorrect type.
     */
    public void injectRaw(RegistryKey<?> entryKey, JsonElement entryData) throws IOException
    {
        RegistryKey<E> typedEntryKey = getTypedKey(entryKey);

        for (Injector<E> injector : injectors)
        {
            injector.inject(typedEntryKey, entryData);
        }
    }

    // Returns the type-bound instance of the registry key.
    private RegistryKey<E> getTypedKey(RegistryKey<?> rawEntryKey) throws IOException
    {
        RegistryKey<E> typedEntryKey = RegistryKey.getOrCreateKey(registryKey, rawEntryKey.getLocation());

        // Assert that the provided raw key was valid for this modifier.
        if (typedEntryKey != rawEntryKey) throw new IOException("Mismatching registry keys!");

        return typedEntryKey;
    }

    public static <E> Builder<E> builder(RegistryKey<Registry<E>> registryKey)
    {
        return new Builder<>(registryKey);
    }

    public static class Builder<E>
    {
        private final RegistryKey<Registry<E>> registryKey;
        private final List<Merger<E>> mergers = new ArrayList<>();
        private final List<Injector<E>> injectors = new ArrayList<>();

        private Builder(RegistryKey<Registry<E>> registryKey)
        {
            this.registryKey = registryKey;
        }

        public Builder<E> add(Merger<E> merger)
        {
            mergers.add(merger);
            return this;
        }

        public Builder<E> add(Injector<E> injector)
        {
            injectors.add(injector);
            return this;
        }

        public RegistryEntryModifier<E> build()
        {
            return new RegistryEntryModifier<>(this);
        }
    }
}
