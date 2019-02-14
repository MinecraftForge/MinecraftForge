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
import java.util.Iterator;
import java.util.List;
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
import net.minecraft.util.registry.IRegistry;

/**
 * A IRegistry implementation that allows for removing of objects. This is a internal helper
 * class used by Forge for managing things that reset during Server initialization.
 */
public class ClearableRegistry<V> implements IRegistry<V>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker REGISTRY = MarkerManager.getMarker("REGISTRY");

    private final IntIdentityHashBiMap<V> ids = new IntIdentityHashBiMap<>(256);
    private final BiMap<ResourceLocation, V> map = HashBiMap.create();
    private final Set<ResourceLocation> keys = Collections.unmodifiableSet(map.keySet());
    private List<V> values = new ArrayList<>();
    private final ResourceLocation name;
    private int nextId = 0;

    public ClearableRegistry(ResourceLocation name)
    {
        this.name = name;
    }

    @Override
    @Nullable
    public ResourceLocation getKey(V value)
    {
        return map.inverse().get(value);
    }

    @Override
    public V get(ResourceLocation key)
    {
        throw new UnsupportedOperationException("No default value");
    }

    @Override
    public ResourceLocation func_212609_b()
    {
        throw new UnsupportedOperationException("No default key");
    }

    @Override
    @Nullable
    public int getId(V value)
    {
        return ids.getId(value);
    }

    @Override
    @Nullable
    public V get(int id)
    {
        return ids.get(id);
    }

    @Override
    public Iterator<V> iterator()
    {
        return ids.iterator();
    }

    @Override
    @Nullable
    public V func_212608_b(ResourceLocation key)
    {
        return map.get(key);
    }

    @Override
    public void register(int id, ResourceLocation key, V value)
    {
        Validate.isTrue(id >= 0, "Invalid ID, can not be < 0");
        Validate.notNull(key);
        Validate.notNull(value);

        V old = map.get(key);
        if (old != null)
        {
            LOGGER.debug(REGISTRY, "{}: Adding suplicate key '{}' to registry. Old: {} New: {}", name, key, old, value);
            values.remove(old);
        }

        map.put(key, value);
        ids.put(value, id);
        values.add(value);
        if (nextId <= id)
            nextId = id + 1;
    }

    @Override
    public void put(ResourceLocation key, V value)
    {
        register(nextId, key, value);
    }

    @Override
    public Set<ResourceLocation> getKeys()
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
    public V getRandom(Random random)
    {
        return values.isEmpty() ? null : values.get(random.nextInt(values.size()));
    }

    @Override
    public boolean func_212607_c(ResourceLocation key)
    {
        return map.containsKey(key);
    }

    public void clear()
    {
        LOGGER.debug(REGISTRY, "{}: Clearing registry", name);
        map.clear();
        values.clear();
        ids.clear();
        nextId = 0;
    }

    public int getNextId()
    {
        return nextId;
    }
}
