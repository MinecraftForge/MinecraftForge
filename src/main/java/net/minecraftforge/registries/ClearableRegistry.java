/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;

/**
 * A IRegistry implementation that allows for removing of objects. This is a internal helper
 * class used by Forge for managing things that reset during Server initialization.
 */
public class ClearableRegistry<T> extends MutableRegistry<T>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker REGISTRY = MarkerManager.getMarker("REGISTRY");

    private final IntIdentityHashBiMap<T> ids = new IntIdentityHashBiMap<>(256);
    private final BiMap<ResourceLocation, T> map = HashBiMap.create();
    private final Set<ResourceLocation> keys = Collections.unmodifiableSet(map.keySet());
    private List<T> values = new ArrayList<>();
    private Map<ResourceLocation, Set<T>> known = new HashMap<>();
    private final ResourceLocation name;
    private final boolean isDelegated;
    private int nextId = 0;

    public ClearableRegistry(ResourceLocation name) { this(name, null); }
    public ClearableRegistry(ResourceLocation name, Class<T> superType)
    {
        this.name = name;
        this.isDelegated =  superType != null && ForgeRegistryEntry.class.isAssignableFrom(superType); //TODO: Make this IDelegatedRegistryEntry?
    }

    @Override
    @Nullable
    public ResourceLocation getKey(T value)
    {
        return map.inverse().get(value);
    }

    @Override
    @Nullable
    public int getId(T value)
    {
        return ids.getId(value);
    }

    @Override
    @Nullable
    public T getByValue(int id)
    {
        return ids.getByValue(id);
    }

    @Override
    public Iterator<T> iterator()
    {
        return ids.iterator();
    }

    @Override
    @Nullable
    public T getOrDefault(ResourceLocation key)
    {
        return map.get(key);
    }

    @Override
    public <V extends T> V register(int id, ResourceLocation key, V value)
    {
        Validate.isTrue(id >= 0, "Invalid ID, can not be < 0");
        Validate.notNull(key);
        Validate.notNull(value);

        T old = map.get(key);
        if (old != null)
        {
            LOGGER.debug(REGISTRY, "{}: Adding duplicate key '{}' to registry. Old: {} New: {}", name, key, old, value);
            values.remove(old);
            if (isDelegated)
            {
                Set<T> others = known.computeIfAbsent(key, k -> new HashSet<>());
                others.add(old);
                others.forEach(e -> getDelegate(e).changeReference(value));
            }
        }

        map.put(key, value);
        ids.put(value, id);
        values.add(value);
        if (nextId <= id)
            nextId = id + 1;

        if (isDelegated)
            getDelegate(value).setName(key);

        return value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private RegistryDelegate<T> getDelegate(T thing)
    {
        if (isDelegated)
            return (RegistryDelegate<T>)((ForgeRegistryEntry)thing).delegate;
        else
            throw new IllegalStateException("Tried to get existing delegate from registry that is not delegated.");
    }

    @Override
    public <V extends T> V register(ResourceLocation key, V value)
    {
        return register(nextId, key, value);
    }

    @Override
    public Set<ResourceLocation> keySet()
    {
        return keys;
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    @Nullable
    public T getRandom(Random random)
    {
        return values.isEmpty() ? null : values.get(random.nextInt(values.size()));
    }

    @Override
    public boolean containsKey(ResourceLocation key)
    {
        return map.containsKey(key);
    }

    public void clear()
    {
        LOGGER.debug(REGISTRY, "{}: Clearing registry", name);
        if (isDelegated)
        {
            known.values().forEach(s -> {
                s.forEach(e -> getDelegate(e).changeReference(e));
                s.clear();
            });
            known.clear();
        }
        map.clear();
        values.clear();
        ids.clear();
        nextId = 0;
    }

    public int getNextId()
    {
        return nextId;
    }

    @Override
    public Optional<T> getValue(ResourceLocation key)
    {
        return Optional.ofNullable(map.get(key));
    }
}
