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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.fml.common.FMLLog;

class NamespacedWrapper<V extends IForgeRegistryEntry<V>> extends RegistryNamespaced<ResourceLocation, V> implements ILockableRegistry
{
    private boolean locked = false;
    private ForgeRegistry<V> delegate;

    public NamespacedWrapper(ForgeRegistry<V> owner)
    {
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
            FMLLog.log.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", key, value.getRegistryType().getName(), id, realId);
    }

    @Override
    public void putObject(ResourceLocation key, V value)
    {
        register(-1, key, value);
    }


    // Reading Functions
    @Override
    @Nullable
    public V getObject(@Nullable ResourceLocation name)
    {
        return this.delegate.getValue(name);
    }

    @Override
    @Nullable
    public ResourceLocation getNameForObject(V value)
    {
        return this.delegate.getKey(value);
    }

    @Override
    public boolean containsKey(ResourceLocation key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public int getIDForObject(@Nullable V value)
    {
        return this.delegate.getID(value);
    }

    @Override
    @Nullable
    public V getObjectById(int id)
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
    public V getRandomObject(Random random)
    {
        Collection<V> values = this.delegate.getValuesCollection();
        return values.stream().skip(random.nextInt(values.size())).findFirst().orElse(null);
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
            owner.setSlaveMap(ID, new NamespacedWrapper<V>((ForgeRegistry<V>)owner));
        }
    }
}
