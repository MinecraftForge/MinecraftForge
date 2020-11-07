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

package net.minecraftforge.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.registries.ForgeRegistry.Snapshot;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final RegistryManager ACTIVE = new RegistryManager("ACTIVE");
    public static final RegistryManager VANILLA = new RegistryManager("VANILLA");
    public static final RegistryManager FROZEN = new RegistryManager("FROZEN");

    BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> registries = HashBiMap.create();
    private BiMap<Class<? extends IForgeRegistryEntry<?>>, ResourceLocation> superTypes = HashBiMap.create();
    private Set<ResourceLocation> persisted = Sets.newHashSet();
    private Set<ResourceLocation> synced = Sets.newHashSet();
    private Map<ResourceLocation, ResourceLocation> legacyNames = new HashMap<>();
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

    public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(RegistryKey<? extends Registry<V>> key)
    {
        return getRegistry(key.getLocation());
    }

    public <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> getRegistry(Class<? super V> cls)
    {
        return getRegistry(superTypes.get(cls));
    }

    public <V extends IForgeRegistryEntry<V>> ResourceLocation getName(IForgeRegistry<V> reg)
    {
        return this.registries.inverse().get(reg);
    }

    public <V extends IForgeRegistryEntry<V>> ResourceLocation updateLegacyName(ResourceLocation legacyName)
    {
        ResourceLocation originalName = legacyName;
        while (getRegistry(legacyName) == null)
        {
            legacyName = legacyNames.get(legacyName);
            if (legacyName == null)
            {
                return originalName;
            }
        }
        return legacyName;
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
            if (other.synced.contains(key))
                this.synced.add(key);
            other.legacyNames.entrySet().stream()
                 .filter(e -> e.getValue().equals(key))
                 .forEach(e -> addLegacyName(e.getKey(), e.getValue()));
        }
        return getRegistry(key);
    }

    <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> createRegistry(ResourceLocation name, RegistryBuilder<V> builder)
    {
        Set<Class<?>> parents = Sets.newHashSet();
        findSuperTypes(builder.getType(), parents);
        SetView<Class<?>> overlappedTypes = Sets.intersection(parents, superTypes.keySet());
        if (!overlappedTypes.isEmpty())
        {
            Class<?> foundType = overlappedTypes.iterator().next();
            LOGGER.error("Found existing registry of type {} named {}, you cannot create a new registry ({}) with type {}, as {} has a parent of that type",
                    foundType, superTypes.get(foundType), name, builder.getType(), builder.getType());
            throw new IllegalArgumentException("Duplicate registry parent type found - you can only have one registry for a particular super type");
        }
        ForgeRegistry<V> reg = new ForgeRegistry<V>(this, name, builder);
        registries.put(name, reg);
        superTypes.put(builder.getType(), name);
        if (builder.getSaveToDisc())
            this.persisted.add(name);
        if (builder.getSync())
            this.synced.add(name);
        for (ResourceLocation legacyName : builder.getLegacyNames())
            addLegacyName(legacyName, name);
        return getRegistry(name);
    }

    private void addLegacyName(ResourceLocation legacyName, ResourceLocation name)
    {
        if (this.legacyNames.containsKey(legacyName))
        {
            throw new IllegalArgumentException("Legacy name conflict for registry " + name + ", upgrade path must be linear: " + legacyName);
        }
        this.legacyNames.put(legacyName, name);
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
        Set<ResourceLocation> keys = savingToDisc ? this.persisted : this.synced;
        keys.forEach(name -> ret.put(name, getRegistry(name).makeSnapshot()));
        return ret;
    }

    //Public for testing only
    public void clean()
    {
        this.persisted.clear();
        this.synced.clear();
        this.registries.clear();
        this.superTypes.clear();
    }

    public static List<Pair<String, FMLHandshakeMessages.S2CRegistry>> generateRegistryPackets(boolean isLocal)
    {
        return !isLocal ? ACTIVE.takeSnapshot(false).entrySet().stream().
                map(e->Pair.of("Registry " + e.getKey(), new FMLHandshakeMessages.S2CRegistry(e.getKey(), e.getValue()))).
                collect(Collectors.toList()) : Collections.emptyList();
    }

    public static List<ResourceLocation> getRegistryNamesForSyncToClient()
    {
        return ACTIVE.registries.keySet().stream().
                filter(resloc -> ACTIVE.synced.contains(resloc)).
                collect(Collectors.toList());
    }
}
