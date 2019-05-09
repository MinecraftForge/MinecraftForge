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

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class NamespacedDefaultedWrapper<V extends IForgeRegistryEntry<V>> extends RegistryNamespacedDefaultedByKey<V> implements ILockableRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean locked = false;
    private ForgeRegistry<V> delegate;

    private NamespacedDefaultedWrapper(ForgeRegistry<V> owner)
    {
        super(null);
        this.delegate = owner;
    }

    @Override
    public void register(int id, ResourceLocation key, V value)
    {
        if (locked)
            throw new IllegalStateException("Can not register to a locked registry. Modder should use Forge Register methods.");
        Validate.notNull(value);

        if (value.getRegistryName() == null)
            value.setRegistryName(key);

        int realId = this.delegate.add(id, value);
        if (realId != id && id != -1)
            LOGGER.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", key, value.getRegistryType().getName(), id, realId);
    }

    @Override
    public void put(ResourceLocation key, V value)
    {
        register(-1, key, value);
    }

    // Reading Functions
    @Override
    @Nullable
    public V func_212608_b(@Nullable ResourceLocation name)
    {
        return this.delegate.getRaw(name); //get without default
    }

    @Override
    @Nullable
    public V get(@Nullable ResourceLocation name)
    {
        return this.delegate.getValue(name); //getOrDefault
    }

    @Override
    @Nullable
    public ResourceLocation getKey(V value)
    {
        return this.delegate.getKey(value);
    }

    @Override
    public boolean func_212607_c(ResourceLocation key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public int getId(@Nullable V value)
    {
        return this.delegate.getID(value);
    }

    @Override
    @Nullable
    public V get(int id)
    {
        return this.delegate.getValue(id);
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.delegate.iterator();
    }

    @Override
    public Set<ResourceLocation> getKeys()
    {
        return this.delegate.getKeys();
    }

    @Override
    @Nullable
    public V getRandom(Random random)
    {
        Collection<V> values = this.delegate.getValues();
        return values.stream().skip(random.nextInt(values.size())).findFirst().orElse(this.delegate.getDefault());
    }

    @Override
    public ResourceLocation func_212609_b()
    {
        return this.delegate.getDefaultKey();
    }

    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    //internal
    @Override
    public void lock(){ this.locked = true; }

    public static class Factory<V extends IForgeRegistryEntry<V>> implements IForgeRegistry.CreateCallback<V>
    {
        public static final ResourceLocation ID = new ResourceLocation("forge", "registry_defaulted_wrapper");
        @Override
        public void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage)
        {
            owner.setSlaveMap(ID, new NamespacedDefaultedWrapper<V>((ForgeRegistry<V>)owner));
        }
    }
}
