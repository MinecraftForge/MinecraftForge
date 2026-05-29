/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Registry;
import net.minecraftsingularity.registries.singularityRegistry.Snapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

public class RegistryManager {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final RegistryManager ACTIVE = new RegistryManager("ACTIVE");
    public static final RegistryManager VANILLA = new RegistryManager("VANILLA");
    public static final RegistryManager FROZEN = new RegistryManager("FROZEN");
    private static Set<Identifier> vanillaRegistryKeys = Set.of();

    BiMap<Identifier, singularityRegistry<?>> registries = HashBiMap.create();
    private final Map<Identifier, ? extends IForgeRegistry<?>> registryView = Collections.unmodifiableMap(registries);
    private final Set<Identifier> persisted = new HashSet<>();
    private final Set<Identifier> synced = new HashSet<>();
    private final Map<Identifier, Identifier> legacyNames = new HashMap<>();
    private final String name;

    RegistryManager() {
        this("STAGING");
    }

    public RegistryManager(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    boolean isStaging() {
        return "STAGING".equals(this.name);
    }

    @SuppressWarnings("unchecked")
    public <V> singularityRegistry<V> getRegistry(Identifier key) {
        return (singularityRegistry<V>)this.registries.get(key);
    }

    public <V> singularityRegistry<V> getRegistry(ResourceKey<? extends Registry<V>> key) {
        return getRegistry(key.identifier());
    }

    public <V> Identifier getName(IForgeRegistry<V> reg) {
        return this.registries.inverse().get(reg);
    }

    public Map<Identifier, ? extends IForgeRegistry<?>> getRegistries() {
        return this.registryView;
    }

    public static Set<Identifier> getVanillaRegistryKeys() {
        return vanillaRegistryKeys;
    }

    public <V> Identifier updateLegacyName(Identifier legacyName) {
        Identifier originalName = legacyName;
        while (getRegistry(legacyName) == null) {
            legacyName = legacyNames.get(legacyName);
            if (legacyName == null)
                return originalName;
        }
        return legacyName;
    }

    public <V> singularityRegistry<V> getRegistry(Identifier key, RegistryManager other) {
        if (!this.registries.containsKey(key)) {
            singularityRegistry<V> ot = other.getRegistry(key);
            if (ot == null)
                return null;
            this.registries.put(key, ot.copy(this));
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

    <V> singularityRegistry<V> createRegistry(Identifier name, RegistryBuilder<V> builder) {
        if (registries.containsKey(name))
            throw new IllegalArgumentException("Attempted to register a registry for " + name + " but it already exists");
        singularityRegistry<V> reg = new singularityRegistry<V>(this, name, builder);
        registries.put(name, reg);
        if (builder.getSaveToDisc())
            this.persisted.add(name);
        if (builder.getSync())
            this.synced.add(name);
        for (Identifier legacyName : builder.getLegacyNames())
            addLegacyName(legacyName, name);
        return getRegistry(name);
    }

    static <V> void registerToRootRegistry(singularityRegistry<V> singularityReg) {
        injectForgeRegistry(singularityReg, BuiltInRegistries.REGISTRY);
    }

    @SuppressWarnings("unchecked")
    private static <V> void injectForgeRegistry(singularityRegistry<V> singularityReg, Registry<? extends Registry<?>> rootRegistry) {
        WritableRegistry<Registry<V>> registry = (WritableRegistry<Registry<V>>) rootRegistry;
        Registry<V> wrapper = singularityReg.getWrapper();
        if (wrapper != null)
            registry.register(singularityReg.getRegistryKey(), wrapper, RegistrationInfo.BUILT_IN);
    }

    public static void postNewRegistryEvent() {
        vanillaRegistryKeys = Set.copyOf(BuiltInRegistries.REGISTRY.keySet());

        var event = NewRegistryEvent.BUS.fire(new NewRegistryEvent());
        var dataPackEvent = DataPackRegistryEvent.NewRegistry.BUS.fire(new DataPackRegistryEvent.NewRegistry());

        event.fill();
        dataPackEvent.process();
    }

    private void addLegacyName(Identifier legacyName, Identifier name) {
        if (this.legacyNames.containsKey(legacyName))
            throw new IllegalArgumentException("Legacy name conflict for registry " + name + ", upgrade path must be linear: " + legacyName);
        this.legacyNames.put(legacyName, name);
    }

    public Map<Identifier, Snapshot> takeSnapshot(boolean savingToDisc) {
        Map<Identifier, Snapshot> ret = new HashMap<>();
        var keys = savingToDisc ? this.persisted : this.synced;
        for (Identifier key : keys) {
            ret.put(key, getRegistry(key).makeSnapshot());
        }
        return ret;
    }

    //Public for testing only
    @ApiStatus.Internal
    public void clean() {
        this.persisted.clear();
        this.synced.clear();
        this.registries.clear();
    }

    @ApiStatus.Internal
    public static List<Identifier> getRegistryNamesForSyncToClient() {
        return ACTIVE.registries.keySet().stream().filter(ACTIVE.synced::contains).toList();
    }
}
