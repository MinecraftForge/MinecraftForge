/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.IntFunction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.util.LogMessageAdapter;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.Validate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraftforge.common.util.TablePrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@ApiStatus.Internal
public class ForgeRegistry<V> implements IForgeRegistryInternal<V>, IForgeRegistryModifiable<V>
{
    public static Marker REGISTRIES = MarkerManager.getMarker("REGISTRIES");
    private static Marker REGISTRYDUMP = MarkerManager.getMarker("REGISTRYDUMP");
    private static Logger LOGGER = LogManager.getLogger();
    private final RegistryManager stage;
    private final BiMap<Integer, V> ids = HashBiMap.create();
    private final BiMap<ResourceLocation, V> names = HashBiMap.create();
    private final BiMap<ResourceKey<V>, V> keys = HashBiMap.create();
    private final Map<ResourceLocation, ResourceLocation> aliases = Maps.newHashMap();
    final Map<ResourceLocation, ?> slaves = Maps.newHashMap();
    private final ResourceLocation defaultKey;
    private final ResourceKey<V> defaultResourceKey;
    private final CreateCallback<V> create;
    private final AddCallback<V> add;
    private final ClearCallback<V> clear;
    private final ValidateCallback<V> validate;
    private final BakeCallback<V> bake;
    private final MissingFactory<V> missing;
    private final BitSet availabilityMap;
    private final Set<Integer> blocked = Sets.newHashSet();
    private final Multimap<ResourceLocation, V> overrides = ArrayListMultimap.create();
    private final Map<ResourceLocation, Holder.Reference<V>> delegatesByName = Maps.newHashMap();
    private final Map<V, Holder.Reference<V>> delegatesByValue = Maps.newHashMap();
    private final BiMap<OverrideOwner<V>, V> owners = HashBiMap.create();
    private final ForgeRegistryTagManager<V> tagManager;
    private final int min;
    private final int max;
    private final boolean allowOverrides;
    private final boolean isModifiable;
    private final boolean hasWrapper;

    private V defaultValue = null;
    boolean isFrozen = false;

    private final ResourceLocation name;
    private final ResourceKey<Registry<V>> key;
    private final RegistryBuilder<V> builder;

    private final Codec<V> codec = new RegistryCodec();

    @SuppressWarnings("unchecked")
    ForgeRegistry(RegistryManager stage, ResourceLocation name, RegistryBuilder<V> builder)
    {
        this.name = name;
        this.key = ResourceKey.createRegistryKey(name);
        this.builder = builder;
        this.stage = stage;
        this.defaultKey = builder.getDefault();
        this.defaultResourceKey = ResourceKey.create(key, defaultKey);
        this.min = builder.getMinId();
        this.max = builder.getMaxId();
        this.availabilityMap = new BitSet(Math.min(max + 1, 0x0FFF));
        this.create = builder.getCreate();
        this.add = builder.getAdd();
        this.clear = builder.getClear();
        this.validate = builder.getValidate();
        this.bake = builder.getBake();
        this.missing = builder.getMissingFactory();
        this.allowOverrides = builder.getAllowOverrides();
        this.isModifiable = builder.getAllowModifications();
        this.hasWrapper = builder.getHasWrapper();
        this.tagManager = this.hasWrapper ? new ForgeRegistryTagManager<>(this) : null;
        if (this.create != null)
            this.create.onCreate(this, stage);
    }

    @Override
    public void register(String key, V value)
    {
        register(GameData.checkPrefix(key, true), value);
    }

    @Override
    public void register(ResourceLocation key, V value)
    {
        add(-1, key, value);
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>()
        {
            int cur = -1;
            V next = null;
            { next(); }

            @Override
            public boolean hasNext()
            {
                return next != null;
            }

            @Override
            public V next()
            {
                V ret = next;
                do {
                    cur = availabilityMap.nextSetBit(cur + 1);
                    next = ids.get(cur);
                } while (next == null && cur != -1); // nextSetBit returns -1 when none is found
                return ret;
            }
            //TODO add remove support?
        };
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return this.name;
    }

    @Override
    public ResourceKey<Registry<V>> getRegistryKey()
    {
        return this.key;
    }

    @NotNull
    public Codec<V> getCodec()
    {
        return this.codec;
    }

    @Override
    public boolean containsKey(ResourceLocation key)
    {
        while (key != null)
        {
            if (this.names.containsKey(key))
                return true;
            key = this.aliases.get(key);
        }
        return false;
    }

    @Override
    public boolean containsValue(V value)
    {
        return this.names.containsValue(value);
    }

    @Override
    public boolean isEmpty()
    {
        return this.names.isEmpty();
    }

    int size()
    {
        return this.names.size();
    }

    @Override
    public V getValue(ResourceLocation key)
    {
        V ret = this.names.get(key);
        key = this.aliases.get(key);
        while (ret == null && key != null)
        {
            ret = this.names.get(key);
            key = this.aliases.get(key);
        }
        return ret == null ? this.defaultValue : ret;
    }

    @Override
    public ResourceLocation getKey(V value)
    {
        return getResourceKey(value).map(ResourceKey::location).orElse(this.defaultKey);
    }

    @NotNull
    @Override
    public Optional<ResourceKey<V>> getResourceKey(V value)
    {
        // We use 'owners' here because we want to return the key for the inactive overridden items, not just the active set.
        return Optional.ofNullable(this.owners.inverse().get(value)).map(OverrideOwner::key);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    NamespacedWrapper<V> getWrapper()
    {
        if (!this.hasWrapper)
            return null;

        return this.defaultKey != null
                ? this.getSlaveMap(NamespacedDefaultedWrapper.Factory.ID, NamespacedDefaultedWrapper.class)
                : this.getSlaveMap(NamespacedWrapper.Factory.ID, NamespacedWrapper.class);
    }

    @NotNull
    NamespacedWrapper<V> getWrapperOrThrow()
    {
        NamespacedWrapper<V> wrapper = getWrapper();

        if (wrapper == null)
            throw new IllegalStateException("Cannot query wrapper for non-wrapped forge registry!");

        return wrapper;
    }

    void onBindTags(Map<TagKey<V>, HolderSet.Named<V>> tags, Set<TagKey<V>> defaultedTags)
    {
        if (this.tagManager != null)
            this.tagManager.bind(tags, defaultedTags);
    }

    @NotNull
    @Override
    public Optional<Holder<V>> getHolder(ResourceKey<V> key)
    {
        return Optional.ofNullable(this.getWrapper()).flatMap(wrapper -> wrapper.getHolder(key));
    }

    @NotNull
    @Override
    public Optional<Holder<V>> getHolder(ResourceLocation location)
    {
        return Optional.ofNullable(this.getWrapper()).flatMap(wrapper -> wrapper.getHolder(location));
    }

    @NotNull
    @Override
    public Optional<Holder<V>> getHolder(V value)
    {
        return Optional.ofNullable(this.getWrapper()).flatMap(wrapper -> wrapper.getHolder(value));
    }

    @Nullable
    @Override
    public ITagManager<V> tags()
    {
        return this.tagManager;
    }

    @NotNull
    @Override
    public Set<ResourceLocation> getKeys()
    {
        return Collections.unmodifiableSet(this.names.keySet());
    }

    @NotNull
    Set<ResourceKey<V>> getResourceKeys()
    {
        return Collections.unmodifiableSet(this.keys.keySet());
    }

    @NotNull
    @Override
    public Collection<V> getValues()
    {
        return Collections.unmodifiableSet(this.names.values());
    }

    @NotNull
    @Override
    public Set<Entry<ResourceKey<V>, V>> getEntries()
    {
        return Collections.unmodifiableSet(this.keys.entrySet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSlaveMap(ResourceLocation name, Class<T> type)
    {
        return (T)this.slaves.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setSlaveMap(ResourceLocation name, Object obj)
    {
        ((Map<ResourceLocation, Object>)this.slaves).put(name, obj);
    }

    public int getID(V value)
    {
        Integer ret = this.ids.inverse().get(value);
        if (ret == null && this.defaultValue != null)
            ret = this.ids.inverse().get(this.defaultValue);
        return ret == null ? -1 : ret.intValue();
    }

    public int getID(ResourceLocation name)
    {
        return getID(this.names.get(name));
    }
    private int getIDRaw(V value)
    {
        Integer ret = this.ids.inverse().get(value);
        return ret == null ? -1 : ret.intValue();
    }
    private int getIDRaw(ResourceLocation name)
    {
        return getIDRaw(this.names.get(name));
    }

    @Override
    public V getValue(int id)
    {
        V ret = this.ids.get(id);
        return ret == null ? this.defaultValue : ret;
    }

    @Nullable
    public ResourceKey<V> getKey(int id)
    {
        V value = getValue(id);
        return this.keys.inverse().get(value);
    }

    void validateKey()
    {
        if (this.defaultKey != null)
            Validate.notNull(this.defaultValue, "Missing default of ForgeRegistry: " + this.defaultKey + " Name: " + this.name);
    }

    @Nullable
    public ResourceLocation getDefaultKey()
    {
        return this.defaultKey;
    }

    ForgeRegistry<V> copy(RegistryManager stage)
    {
        return new ForgeRegistry<>(stage, name, builder);
    }

    @Override
    public void register(int id, ResourceLocation key, V value) {
        add(id, key, value, key.getNamespace());
    }

    int add(int id, ResourceLocation key, V value)
    {
        final String owner = ModLoadingContext.get().getActiveNamespace();
        return add(id, key, value, owner);
    }

    int add(int id, ResourceLocation key, V value, String owner)
    {
        Preconditions.checkNotNull(key, "Can't use a null-name for the registry, object %s.", value);
        Preconditions.checkNotNull(value, "Can't add null-object to the registry, name %s.", key);

        int idToUse = id;
        if (idToUse < 0 || availabilityMap.get(idToUse))
            idToUse = availabilityMap.nextClearBit(min);

        if (idToUse > max)
            throw new RuntimeException(String.format(Locale.ENGLISH, "Invalid id %d - maximum id range exceeded.", idToUse));

        V oldEntry = getRaw(key);
        if (oldEntry == value) // already registered, return prev registration's id
        {
            LOGGER.warn(REGISTRIES,"Registry {}: The object {} has been registered twice for the same name {}.", this.name, value, key);
            return this.getID(value);
        }
        if (oldEntry != null) // duplicate name
        {
            if (!this.allowOverrides)
                throw new IllegalArgumentException(String.format(Locale.ENGLISH, "The name %s has been registered twice, for %s and %s.", key, getRaw(key), value));
            if (owner == null)
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Could not determine owner for the override on %s. Value: %s", key, value));
            LOGGER.debug(REGISTRIES,"Registry {} Override: {} {} -> {}", this.name, key, oldEntry, value);
            idToUse = this.getID(oldEntry);
        }

        Integer foundId = this.ids.inverse().get(value); //Is this ever possible to trigger with otherThing being different?
        if (foundId != null)
        {
            V otherThing = this.ids.get(foundId);
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "The object %s{%x} has been registered twice, using the names %s and %s. (Other object at this id is %s{%x})", value, System.identityHashCode(value), getKey(value), key, otherThing, System.identityHashCode(otherThing)));
        }

        if (isLocked())
            throw new IllegalStateException(String.format(Locale.ENGLISH, "The object %s (name %s) is being added too late.", value, key));

        if (defaultKey != null && defaultKey.equals(key))
        {
            if (this.defaultValue != null)
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Attemped to override already set default value. This is not allowed: The object %s (name %s)", value, key));
            this.defaultValue = value;
        }

        ResourceKey<V> rkey = ResourceKey.create(this.key, key);
        this.names.put(key, value);
        this.keys.put(rkey, value);
        this.ids.put(idToUse, value);
        this.availabilityMap.set(idToUse);
        this.owners.put(new OverrideOwner<V>(owner == null ? key.getNamespace() : owner, rkey), value);

        if (hasWrapper)
        {
            Holder.Reference<V> delegate = bindDelegate(rkey, value);
            if (oldEntry != null)
            {
                if (!this.overrides.get(key).contains(oldEntry))
                    this.overrides.put(key, oldEntry);
                this.overrides.get(key).remove(value);
            }
        }

        if (this.add != null)
            this.add.onAdd(this, this.stage, idToUse, rkey, value, oldEntry);

        LOGGER.trace(REGISTRIES,"Registry {} add: {} {} {} (req. id {})", this.name, key, idToUse, value, id);

        return idToUse;
    }

    public V getRaw(ResourceLocation key)
    {
        V ret = this.names.get(key);
        key = this.aliases.get(key);
        while (ret == null && key != null)
        {
            ret = this.names.get(key);
            key = this.aliases.get(key);
        }
        return ret;
    }

    /**
     * Adds an alias that maps from the name specified by <code>src</code> to the name specified by <code>dst</code>.<p>
     * Any registry lookups that target the first name will resolve as the second name, if the first name is not present.
     * @param src The source registry name to alias from.
     * @param dst The target registry name to alias to.
     *
     * TODO: Add as public API in IForgeRegistry and DeferredRegister.
     */
    public void addAlias(ResourceLocation src, ResourceLocation dst)
    {
        if (this.isLocked())
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Attempted to register the alias %s -> %s too late", src, dst));

        if (src.equals(dst))
        {
            LOGGER.warn(REGISTRIES, "Registry {} Ignoring invalid alias: {} -> {}", this.name, src, dst);
            return;
        }

        this.aliases.put(src, dst);
        LOGGER.trace(REGISTRIES,"Registry {} alias: {} -> {}", this.name, src, dst);
    }

    @NotNull
    @Override
    public Optional<Holder.Reference<V>> getDelegate(ResourceKey<V> rkey)
    {
        return Optional.ofNullable(delegatesByName.get(rkey.location()));
    }

    @NotNull
    @Override
    public Holder.Reference<V> getDelegateOrThrow(ResourceKey<V> rkey)
    {
        return getDelegate(rkey).orElseThrow(() -> new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for key %s", rkey)));
    }

    @NotNull
    @Override
    public Optional<Holder.Reference<V>> getDelegate(ResourceLocation key)
    {
        return Optional.ofNullable(delegatesByName.get(key));
    }

    @NotNull
    @Override
    public Holder.Reference<V> getDelegateOrThrow(ResourceLocation key)
    {
        return getDelegate(key).orElseThrow(() -> new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for key %s", key)));
    }

    @NotNull
    @Override
    public Optional<Holder.Reference<V>> getDelegate(V value)
    {
        return Optional.ofNullable(delegatesByValue.get(value));
    }

    @NotNull
    @Override
    public Holder.Reference<V> getDelegateOrThrow(V value)
    {
        return getDelegate(value).orElseThrow(() -> new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for value %s", value)));
    }

    private Holder.Reference<V> bindDelegate(ResourceKey<V> rkey, V value)
    {
        Holder.Reference<V> delegate = delegatesByName.computeIfAbsent(rkey.location(), k -> Holder.Reference.createStandAlone(this.getWrapperOrThrow().holderOwner(), rkey));
        delegate.bindKey(rkey);
        delegate.bindValue(value);
        delegatesByValue.put(value, delegate);
        return delegate;
    }

    void resetDelegates()
    {
        if (!this.hasWrapper)
            return;

        for (Entry<ResourceKey<V>, V> entry : this.keys.entrySet())
            bindDelegate(entry.getKey(), entry.getValue());

        for (Entry<ResourceLocation, V> entry : this.overrides.entries())
            bindDelegate(ResourceKey.create(this.key, entry.getKey()), entry.getValue());
    }

    V getDefault()
    {
        return this.defaultValue;
    }


    void validateContent(ResourceLocation registryName)
    {
        try
        {
            ObfuscationReflectionHelper.findMethod(BitSet.class, "trimToSize").invoke(this.availabilityMap);
        }
        catch (Exception e)
        {
            //We don't care... Just a micro-optimization
        }

        for (V obj : this)
        {
            int id = getID(obj);
            ResourceLocation name = getKey(obj);

            // name lookup failed -> obj is not in the obj<->name map
            if (name == null)
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for %s %s, id %d, doesn't yield a name.", registryName, obj, id));

            // id is too high
            if (id > max)
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for %s %s, name %s uses the too large id %d.", registryName, obj, name, id));

            // id -> obj lookup is inconsistent
            if (getValue(id) != obj)
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for id %d, name %s, doesn't yield the expected %s %s.", id, name, registryName, obj));

            // name -> obj lookup is inconsistent
            if (getValue(name) != obj)
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for name %s, id %d, doesn't yield the expected %s %s.", name, id, registryName, obj));

            // name -> id lookup is inconsistent
            if (getID(name) != id)
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Registry entry for name %s doesn't yield the expected id %d.", name, id));

            /*
            // entry is blocked, thus should be empty
            if (blockedIds.contains(id))
                throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, name %s, marked as dangling.", registryName, obj, id, name));
             */

            // registry-specific validation
            if (this.validate != null)
                this.validate.onValidate(this, this.stage, id, name, obj);
        }
    }

    public void bake()
    {
        if (this.bake != null)
            this.bake.onBake(this, this.stage);
    }

    void sync(ResourceLocation name, ForgeRegistry<V> from)
    {
        LOGGER.debug(REGISTRIES,"Registry {} Sync: {} -> {}", this.name, this.stage.getName(), from.stage.getName());
        if (this == from)
            throw new IllegalArgumentException("WTF We are the same!?!?!");

        this.isFrozen = false;

        if (this.clear != null)
            this.clear.onClear(this, stage);

        /* -- Should never need to be copied
        this.defaultKey = from.defaultKey;
        this.max = from.max;
        this.min = from.min;
        */
        // Aliases from the active registry take priority over those from the staged (loaded) registry.
        for (var entry : from.aliases.entrySet())
        {
            if (!this.aliases.containsKey(entry.getKey()))
            {
                this.aliases.put(entry.getKey(), entry.getValue());
            }
        }

        this.ids.clear();
        this.names.clear();
        this.keys.clear();
        this.availabilityMap.clear(0, this.availabilityMap.length());
        this.defaultValue = null;
        this.overrides.clear();
        this.owners.clear();

        boolean errored = false;

        for (Entry<ResourceLocation, V> entry : from.names.entrySet())
        {
            List<V> overrides = Lists.newArrayList(from.overrides.get(entry.getKey()));
            int id = from.getID(entry.getKey());
            if (overrides.isEmpty())
            {
                int realId = add(id, entry.getKey(), entry.getValue());
                if (id != realId && id != -1)
                {
                    LOGGER.warn(REGISTRIES,"Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), id, realId);
                    errored = true;
                }
            }
            else
            {
                overrides.add(entry.getValue());
                for (V value : overrides)
                {
                    OverrideOwner<V> owner = from.owners.inverse().get(value);
                    if (owner == null)
                    {
                        LOGGER.warn(REGISTRIES,"Registry {}: Override did not have an associated owner object. Name: {} Value: {}", this.name, entry.getKey(), value);
                        errored = true;
                        continue;
                    }
                    int realId = add(id, entry.getKey(), value, owner.owner);
                    if (id != realId && id != -1)
                    {
                        LOGGER.warn(REGISTRIES,"Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), id, realId);
                        errored = true;
                    }
                }
            }
        }

        if (errored)
            throw new RuntimeException("One of more entry values did not copy to the correct id. Check log for details!");
    }

    @Override
    public void clear()
    {
        if (!this.isModifiable)
            throw new UnsupportedOperationException("Attempted to clear a non-modifiable Forge Registry");

        if (this.isLocked())
            throw new IllegalStateException("Attempted to clear the registry to late.");

        if (this.clear != null)
            this.clear.onClear(this, stage);


        this.aliases.clear();

        this.ids.clear();
        this.names.clear();
        this.keys.clear();
        this.availabilityMap.clear(0, this.availabilityMap.length());
    }

    @Override
    public V remove(ResourceLocation key)
    {
        if (!this.isModifiable)
            throw new UnsupportedOperationException("Attempted to remove from a non-modifiable Forge Registry");

        if (this.isLocked())
            throw new IllegalStateException("Attempted to remove from the registry to late.");

        V value = this.names.remove(key);
        if (value != null)
        {
            ResourceKey<V> rkey = this.keys.inverse().remove(value);
            if (rkey == null)
                throw new IllegalStateException("Removed a entry that did not have an associated RegistryKey: " + key + " " + value.toString() + " This should never happen unless hackery!");

            Integer id = this.ids.inverse().remove(value);
            if (id == null)
                throw new IllegalStateException("Removed a entry that did not have an associated id: " + key + " " + value.toString() + " This should never happen unless hackery!");

            LOGGER.trace(REGISTRIES,"Registry {} remove: {} {}", this.name, key, id);
        }

        return value;
    }

    void block(int id)
    {
        this.blocked.add(id);
        this.availabilityMap.set(id);
    }

    @Override
    public boolean isLocked()
    {
        return this.isFrozen;
    }

    /**
     * Used to control the times where people can modify this registry.
     * Users should only ever register things in the {@literal Register<?>} events!
     */
    public void freeze()
    {
        this.isFrozen = true;
    }

    public void unfreeze()
    {
        this.isFrozen = false;
    }

    void dump(ResourceLocation name)
    {
        // Building a good looking table is not cheap, so only do it if the debug logger is enabled.
        if (LOGGER.isDebugEnabled(REGISTRYDUMP))
        {
            TablePrinter<DumpRow> tab = new TablePrinter<DumpRow>()
                .header("ID",    r -> r.id)
                .header("Key",   r -> r.key)
                .header("Value", r -> r.value);

            LOGGER.debug(REGISTRYDUMP, ()-> LogMessageAdapter.adapt(sb ->
            {
                sb.append("Registry Name: ").append(name).append('\n');
                tab.clearRows();
                getKeys().stream().map(this::getID).sorted().map(id -> {
                    V val = getValue(id);
                    ResourceLocation key = getKey(val);
                    return new DumpRow(Integer.toString(id), getKey(val).toString(), val.toString());
                }).forEach(tab::add);
                tab.build(sb);
            }));
        }
    }

    private record DumpRow(String id, String key, String value) {}

    public void loadIds(Map<ResourceLocation, Integer> ids, Map<ResourceLocation, String> overrides, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, IdMappingEvent.IdRemapping> remapped, ForgeRegistry<V> old, ResourceLocation name)
    {
        Map<ResourceLocation, String> ovs = Maps.newHashMap(overrides);
        for (Map.Entry<ResourceLocation, Integer> entry : ids.entrySet())
        {
            ResourceLocation itemName = entry.getKey();

            int newId = entry.getValue();
            int currId = old.getIDRaw(itemName);

            if (currId == -1)
            {
                LOGGER.info(REGISTRIES,"Registry {}: Found a missing id from the world {}", this.name, itemName);
                missing.put(itemName, newId);
                continue; // no block/item -> nothing to add
            }
            else if (currId != newId)
            {
                LOGGER.debug(REGISTRIES,"Registry {}: Fixed {} id mismatch {}: {} (init) -> {} (map).", this.name, name, itemName, currId, newId);
                remapped.put(itemName, new IdMappingEvent.IdRemapping(currId, newId));
            }

            V obj = old.getRaw(itemName);
            Preconditions.checkState(obj != null, "objectKey has an ID but no object. Reflection/ASM hackery? Registry bug?");

            List<V> lst = Lists.newArrayList(old.overrides.get(itemName));
            String primaryName = null;
            if (old.overrides.containsKey(itemName))
            {
                if (!overrides.containsKey(itemName))
                {
                    lst.add(obj);
                    obj = old.overrides.get(itemName).iterator().next(); //Get the first one in the list, Which should be the first one registered
                    primaryName = old.owners.inverse().get(obj).owner;
                }
                else
                    primaryName = overrides.get(itemName);
            }

            for (V value : lst)
            {
                OverrideOwner<V> owner = old.owners.inverse().get(value);
                if (owner == null)
                {
                    LOGGER.warn(REGISTRIES,"Registry {}: Override did not have an associated owner object. Name: {} Value: {}", this.name, entry.getKey(), value);
                    continue;
                }

                if (primaryName.equals(owner.owner))
                    continue;

                int realId = add(newId, itemName, value, owner.owner);
                if (newId != realId)
                    LOGGER.warn(REGISTRIES,"Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), newId, realId);
            }

            int realId = add(newId, itemName, obj, primaryName == null ? itemName.getNamespace() : primaryName);
            if (realId != newId)
                LOGGER.warn(REGISTRIES,"Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), newId, realId);
            ovs.remove(itemName);
        }

        for (Map.Entry<ResourceLocation, String> entry :  ovs.entrySet())
        {
            ResourceLocation itemName = entry.getKey();
            String owner = entry.getValue();
            String current = this.owners.inverse().get(this.getRaw(itemName)).owner;
            if (!owner.equals(current))
            {
                V _new = this.owners.get(new OverrideOwner<V>(owner, ResourceKey.create(this.key, itemName)));
                if (_new == null)
                {
                    LOGGER.warn(REGISTRIES,"Registry {}: Skipping override for {}, Unknown owner {}", this.name, itemName, owner);
                    continue;
                }

                LOGGER.info(REGISTRIES,"Registry {}: Activating override {} for {}", this.name, owner, itemName);

                int newId = this.getID(itemName);
                int realId = this.add(newId, itemName, _new, owner);
                if (newId != realId)
                    LOGGER.warn(REGISTRIES,"Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.name, entry.getKey(), newId, realId);
            }
        }
    }

    //Public for tests
    public Snapshot makeSnapshot()
    {
        Snapshot ret = new Snapshot();
        this.ids.forEach((id, value) -> ret.ids.put(getKey(value), id));
        ret.aliases.putAll(this.aliases);
        ret.blocked.addAll(this.blocked);
        ret.overrides.putAll(getOverrideOwners());
        return ret;
    }

    Map<ResourceLocation, String> getOverrideOwners()
    {
        Map<ResourceLocation, String> ret = Maps.newHashMap();
        for (ResourceLocation key : this.overrides.keySet())
        {
            V obj = this.names.get(key);
            OverrideOwner<V> owner = this.owners.inverse().get(obj);
            if (owner == null)
                LOGGER.debug(REGISTRIES,"Registry {} {}: Invalid override {} {}", this.name, this.stage.getName(), key, obj);
            ret.put(key, owner.owner);
        }
        return ret;
    }

    private class RegistryCodec implements Codec<V>
    {
        @Override
        public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input)
        {
            if (ops.compressMaps())
            {
                return ops.getNumberValue(input).flatMap(n ->
                {
                    int id = n.intValue();
                    if (ids.get(id) == null)
                    {
                        return DataResult.error(() -> "Unknown registry id in " + ForgeRegistry.this.key + ": " + n);
                    }
                    V val = ForgeRegistry.this.getValue(id);
                    return DataResult.success(val);
                }).map(v -> Pair.of(v, ops.empty()));
            }
            else
            {
                return ResourceLocation.CODEC.decode(ops, input).flatMap(keyValuePair -> !ForgeRegistry.this.containsKey(keyValuePair.getFirst())
                        ? DataResult.error(() -> "Unknown registry key in " + ForgeRegistry.this.key + ": " + keyValuePair.getFirst())
                        : DataResult.success(keyValuePair.mapFirst(ForgeRegistry.this::getValue)));
            }
        }

        @Override
        public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix)
        {
            ResourceLocation key = getKey(input);
            if (key == null)
            {
                return DataResult.error(() -> "Unknown registry element in " + ForgeRegistry.this.key + ": " + input);
            }
            T toMerge = ops.compressMaps() ? ops.createInt(getID(input)) : ops.createString(key.toString());
            return ops.mergeToPrimitive(prefix, toMerge);
        }
    }

    public static class Snapshot
    {
        private static final Comparator<ResourceLocation> sorter = (a,b) -> a.compareNamespaced(b);
        public final Map<ResourceLocation, Integer> ids = Maps.newTreeMap(sorter);
        public final Map<ResourceLocation, ResourceLocation> aliases = Maps.newTreeMap(sorter);
        public final Set<Integer> blocked = Sets.newTreeSet();
        public final Map<ResourceLocation, String> overrides = Maps.newTreeMap(sorter);
        private FriendlyByteBuf binary = null;

        public CompoundTag write()
        {
            CompoundTag data = new CompoundTag();

            ListTag ids = new ListTag();
            this.ids.entrySet().stream().forEach(e ->
            {
                CompoundTag tag = new CompoundTag();
                tag.putString("K", e.getKey().toString());
                tag.putInt("V", e.getValue());
                ids.add(tag);
            });
            data.put("ids", ids);

            ListTag aliases = new ListTag();
            this.aliases.entrySet().stream().forEach(e ->
            {
                CompoundTag tag = new CompoundTag();
                tag.putString("K", e.getKey().toString());
                tag.putString("V", e.getValue().toString());
                aliases.add(tag);
            });
            data.put("aliases", aliases);

            ListTag overrides = new ListTag();
            this.overrides.entrySet().stream().forEach(e ->
            {
                CompoundTag tag = new CompoundTag();
                tag.putString("K", e.getKey().toString());
                tag.putString("V", e.getValue());
                overrides.add(tag);
            });
            data.put("overrides", overrides);

            int[] blocked = this.blocked.stream().mapToInt(x->x).sorted().toArray();
            data.putIntArray("blocked", blocked);

            return data;
        }

        public static Snapshot read(CompoundTag nbt)
        {
            Snapshot ret = new Snapshot();
            if (nbt == null)
            {
                return ret;
            }

            ListTag list = nbt.getList("ids", 10);
            list.forEach(e ->
            {
                CompoundTag comp = (CompoundTag)e;
                ret.ids.put(new ResourceLocation(comp.getString("K")), comp.getInt("V"));
            });

            list = nbt.getList("aliases", 10);
            list.forEach(e ->
            {
                CompoundTag comp = (CompoundTag)e;
                ret.aliases.put(new ResourceLocation(comp.getString("K")), new ResourceLocation(comp.getString("V")));
            });

            list = nbt.getList("overrides", 10);
            list.forEach(e ->
            {
                CompoundTag comp = (CompoundTag)e;
                ret.overrides.put(new ResourceLocation(comp.getString("K")), comp.getString("V"));
            });

            int[] blocked = nbt.getIntArray("blocked");
            for (int i : blocked)
            {
                ret.blocked.add(i);
            }

            return ret;
        }

        public synchronized FriendlyByteBuf getPacketData()
        {
            if (binary == null) {
                FriendlyByteBuf pkt = new FriendlyByteBuf(Unpooled.buffer());

                pkt.writeVarInt(this.ids.size());
                this.ids.forEach((k,v) -> {
                    pkt.writeResourceLocation(k);
                    pkt.writeVarInt(v);
                });

                pkt.writeVarInt(this.aliases.size());
                this.aliases.forEach((k, v) -> {
                    pkt.writeResourceLocation(k);
                    pkt.writeResourceLocation(v);
                });

                pkt.writeVarInt(this.overrides.size());
                this.overrides.forEach((k, v) -> {
                    pkt.writeResourceLocation(k);
                    pkt.writeUtf(v, 0x100);
                });

                pkt.writeVarInt(this.blocked.size());
                this.blocked.forEach(pkt::writeVarInt);

                this.binary = pkt;
            }

            return new FriendlyByteBuf(binary.slice());
        }

        public static Snapshot read(FriendlyByteBuf buff)
        {
            if (buff == null)
                return new Snapshot();

            Snapshot ret = new Snapshot();

            int len = buff.readVarInt();
            for (int x = 0; x < len; x++)
                ret.ids.put(buff.readResourceLocation(), buff.readVarInt());

            len = buff.readVarInt();
            for (int x = 0; x < len; x++)
                ret.aliases.put(buff.readResourceLocation(), buff.readResourceLocation());

            len = buff.readVarInt();
            for (int x = 0; x < len; x++)
                ret.overrides.put(buff.readResourceLocation(), buff.readUtf(0x100));

            len = buff.readVarInt();
            for (int x = 0; x < len; x++)
                ret.blocked.add(buff.readVarInt());

            return ret;
        }
    }

    @SuppressWarnings("unchecked")
    public MissingMappingsEvent getMissingEvent(ResourceLocation name, Map<ResourceLocation, Integer> map)
    {
        List<MissingMappingsEvent.Mapping<V>> lst = Lists.newArrayList();
        ForgeRegistry<V> pool = RegistryManager.ACTIVE.getRegistry(name);
        map.forEach((rl, id) -> lst.add(new MissingMappingsEvent.Mapping<>(this, pool, rl, id)));
        return new MissingMappingsEvent(ResourceKey.createRegistryKey(name), this, (Collection<MissingMappingsEvent.Mapping<?>>) (Collection<?>) lst);
    }

    void processMissingEvent(ResourceLocation name, ForgeRegistry<V> pool, List<MissingMappingsEvent.Mapping<V>> mappings, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies)
    {
        LOGGER.debug(REGISTRIES,"Processing missing event for {}:", name);
        int ignored = 0;

        for (MissingMappingsEvent.Mapping<V> remap : mappings)
        {
            MissingMappingsEvent.Action action = remap.action;

            if (action == MissingMappingsEvent.Action.REMAP)
            {
                // entry re-mapped, finish the registration with the new name/object, but the old id
                int currId = getID(remap.target);
                ResourceLocation newName = pool.getKey(remap.target);
                LOGGER.debug(REGISTRIES,"  Remapping {} -> {}.", remap.key, newName);

                missing.remove(remap.key);
                //I don't think this will work, but I dont think it ever worked.. the item is already in the map with a different id... we want to fix that..
                int realId = this.add(remap.id, newName, remap.target);
                if (realId != remap.id)
                    LOGGER.warn(REGISTRIES, "Registered object did not get ID it asked for. Name: {} Expected: {} Got: {}", newName, remap.id, realId);
                this.addAlias(remap.key, newName);


                if (currId != realId)
                {
                    LOGGER.info(REGISTRIES,"Fixed id mismatch {}: {} (init) -> {} (map).", newName, currId, realId);
                    remaps.put(newName, new IdMappingEvent.IdRemapping(currId, realId));
                }
            }
            else
            {
                // block item missing, warn as requested and block the id
                if (action == MissingMappingsEvent.Action.DEFAULT)
                {
                    V m = this.missing == null ? null : this.missing.createMissing(remap.key, injectNetworkDummies);
                    if (m == null)
                        defaulted.add(remap.key);
                    else
                        this.add(remap.id, remap.key, m, remap.key.getNamespace());
                }
                else if (action == MissingMappingsEvent.Action.IGNORE)
                {
                    LOGGER.debug(REGISTRIES,"Ignoring {}", remap.key);
                    ignored++;
                }
                else if (action == MissingMappingsEvent.Action.FAIL)
                {
                    LOGGER.debug(REGISTRIES,"Failing {}!", remap.key);
                    failed.add(remap.key);
                }
                else if (action == MissingMappingsEvent.Action.WARN)
                {
                    LOGGER.warn(REGISTRIES,"{} may cause world breakage!", remap.key);
                }
                this.block(remap.id);
            }
        }

        if (failed.isEmpty() && ignored > 0)
            LOGGER.debug(REGISTRIES,"There were {} missing mappings that have been ignored", ignored);
    }

    RegistryBuilder<V> getBuilder()
    {
        return this.builder;
    }

    private record OverrideOwner<V>(String owner, ResourceKey<V> key){};
}
