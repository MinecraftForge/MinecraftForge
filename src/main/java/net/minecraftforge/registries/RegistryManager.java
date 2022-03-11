/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.network.HandshakeMessages;
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
    private static Set<ResourceLocation> vanillaRegistryKeys = Set.of();

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

    public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(ResourceKey<? extends Registry<V>> key)
    {
        return getRegistry(key.location());
    }

    /**
     * @see #getRegistry(ResourceLocation)
     * @see #getRegistry(ResourceKey)
     * @deprecated The uniqueness of registry super types will not be guaranteed starting in 1.19.
     */
    @Deprecated(forRemoval = true, since = "1.18.2")
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
        if (registries.containsKey(name))
            throw new IllegalArgumentException("Attempted to register a registry for " + name + " with type "
                    + builder.getType().getName() + " but it already exists as " + registries.get(name).getRegistrySuperType().getName());
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

    @SuppressWarnings("unchecked")
    private static <V extends IForgeRegistryEntry<V>> void registerToRootRegistry(ForgeRegistry<V> forgeReg)
    {
        WritableRegistry<Registry<V>> registry = (WritableRegistry<Registry<V>>) Registry.REGISTRY;
        Registry<V> wrapper = forgeReg.getWrapper();
        if (wrapper != null)
            registry.register(forgeReg.getRegistryKey(), wrapper, Lifecycle.experimental());
    }

    // TODO 1.19: Remove this in favor of fixing up the IModStateTransition system
    private static final RegistryEvent.NewRegistry NEW_REGISTRY_EVENT = new RegistryEvent.NewRegistry();

    public static RegistryEvent.NewRegistry newRegistryEventGenerator(ModContainer modContainer)
    {
        return NEW_REGISTRY_EVENT;
    }


    public static CompletableFuture<List<Throwable>> preNewRegistryEvent(final Executor executor,
            final IModStateTransition.EventGenerator<? extends RegistryEvent.NewRegistry> eventGenerator)
    {
        return CompletableFuture.runAsync(() -> vanillaRegistryKeys = Set.copyOf(Registry.REGISTRY.keySet()), executor)
                .handle((v, t) -> t != null ? Collections.singletonList(t) : Collections.emptyList());
    }

    public static CompletableFuture<List<Throwable>> postNewRegistryEvent(final Executor executor,
            final IModStateTransition.EventGenerator<? extends RegistryEvent.NewRegistry> eventGenerator)
    {
        return CompletableFuture.runAsync(() -> {
            Map<RegistryBuilder<?>, IForgeRegistry<?>> registries = NEW_REGISTRY_EVENT.getRegistryBuilders()
                    .collect(Collectors.toMap(Function.identity(), RegistryBuilder::create, (a, b) -> a, IdentityHashMap::new));

            registries.forEach((builder, reg) -> {
                if (builder.getHasWrapper() && !Registry.REGISTRY.containsKey(reg.getRegistryName()))
                    registerToRootRegistry((ForgeRegistry<?>) reg);
            });

            NEW_REGISTRY_EVENT.fill(registries);
        }, executor).handle((v, t) -> t != null ? Collections.singletonList(t) : Collections.emptyList());
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

    public static List<Pair<String, HandshakeMessages.S2CRegistry>> generateRegistryPackets(boolean isLocal)
    {
        return !isLocal ? ACTIVE.takeSnapshot(false).entrySet().stream().
                map(e->Pair.of("Registry " + e.getKey(), new HandshakeMessages.S2CRegistry(e.getKey(), e.getValue()))).
                collect(Collectors.toList()) : Collections.emptyList();
    }

    public static List<ResourceLocation> getRegistryNamesForSyncToClient()
    {
        return ACTIVE.registries.keySet().stream().
                filter(resloc -> ACTIVE.synced.contains(resloc)).
                collect(Collectors.toList());
    }

    public static Set<ResourceLocation> getVanillaRegistryKeys()
    {
        return vanillaRegistryKeys;
    }
}
