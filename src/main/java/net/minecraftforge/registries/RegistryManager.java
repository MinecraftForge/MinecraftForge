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

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.registries.ForgeRegistry.Snapshot;
import net.minecraftforge.registries.IForgeRegistry.*;

public class RegistryManager
{
    public static final RegistryManager ACTIVE = new RegistryManager("ACTIVE");
    public static final RegistryManager VANILLA = new RegistryManager("VANILLA");
    public static final RegistryManager FROZEN = new RegistryManager("FROZEN");

    BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> registries = HashBiMap.create();
    private BiMap<Class<? extends IForgeRegistryEntry<?>>, ResourceLocation> superTypes = HashBiMap.create();
    private Set<ResourceLocation> persisted = Sets.newHashSet();
    private final String name;

    public RegistryManager(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    @SuppressWarnings("unchecked")
    public <V extends IForgeRegistryEntry<V>> Class<V> getSuperType(ResourceLocation key)
    {
        return (Class<V>)superTypes.inverse().get(key);
    }

    @SuppressWarnings("unchecked")
    public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(ResourceLocation key)
    {
        return (ForgeRegistry<V>)this.registries.get(key);
    }

    public <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> getRegistry(Class<V> cls)
    {
        return getRegistry(superTypes.get(cls));
    }

    public <V extends IForgeRegistryEntry<V>> ResourceLocation getName(IForgeRegistry<V> reg)
    {
        return this.registries.inverse().get(reg);
    }

    public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(ResourceLocation key, RegistryManager other)
    {
        if (!this.registries.containsKey(key))
        {
            ForgeRegistry<V> ot = other.getRegistry(key);
            if (ot == null)
                return null;
            this.registries.put(key, ot.copy(this));
            this.superTypes.put(ot.getRegistrySuperType(), key);
            if (other.persisted.contains(key))
                this.persisted.add(key);
        }
        return getRegistry(key);
    }

    <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> createRegistry(ResourceLocation name, Class<V> type, ResourceLocation defaultKey, int min, int max,
            @Nullable AddCallback<V> add, @Nullable ClearCallback<V> clear, @Nullable CreateCallback<V> create, @Nullable ValidateCallback<V> validate,
            boolean persisted, boolean allowOverrides, boolean isModifiable, @Nullable DummyFactory<V> dummyFactory, @Nullable MissingFactory<V> missing)
    {
        Set<Class<?>> parents = Sets.newHashSet();
        findSuperTypes(type, parents);
        SetView<Class<?>> overlappedTypes = Sets.intersection(parents, superTypes.keySet());
        if (!overlappedTypes.isEmpty())
        {
            Class<?> foundType = overlappedTypes.iterator().next();
            FMLLog.log.error("Found existing registry of type {} named {}, you cannot create a new registry ({}) with type {}, as {} has a parent of that type", foundType, superTypes.get(foundType), name, type, type);
            throw new IllegalArgumentException("Duplicate registry parent type found - you can only have one registry for a particular super type");
        }
        ForgeRegistry<V> reg = new ForgeRegistry<V>(type, defaultKey, min, max, create, add, clear, validate, this, allowOverrides, isModifiable, dummyFactory, missing);
        registries.put(name, reg);
        superTypes.put(type, name);
        if (persisted)
            this.persisted.add(name);
        return getRegistry(name);
    }

    private void findSuperTypes(Class<?> type, Set<Class<?>> types)
    {
        if (type == null || type == Object.class)
        {
            return;
        }
        types.add(type);
        for (Class<?> interfac : type.getInterfaces())
        {
            findSuperTypes(interfac, types);
        }
        findSuperTypes(type.getSuperclass(), types);
    }

    public Map<ResourceLocation, Snapshot> takeSnapshot(boolean savingToDisc)
    {
        Map<ResourceLocation, Snapshot> ret = Maps.newHashMap();
        Set<ResourceLocation> keys = savingToDisc ? this.persisted : this.registries.keySet();
        keys.forEach(name -> ret.put(name, getRegistry(name).makeSnapshot()));
        return ret;
    }

    //Public for testing only
    public void clean()
    {
        this.persisted.clear();
        this.registries.clear();
        this.superTypes.clear();
    }
}
