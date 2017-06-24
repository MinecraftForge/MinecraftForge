package net.minecraftforge.registries;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ForgeRegistry<V extends IForgeRegistryEntry<V>> implements IForgeRegistryInternal<V>, IForgeRegistryModifiable<V>
{
    public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugRegistryEntries", "false"));
    private final RegistryManager stage;
    private final BiMap<Integer, V> ids = HashBiMap.create();
    private final BiMap<ResourceLocation, V> names = HashBiMap.create();
    private final Class<V> superType;
    private final Map<ResourceLocation, ResourceLocation> aliases = Maps.newHashMap();
    final Map<ResourceLocation, ?> slaves = Maps.newHashMap();
    private final ResourceLocation defaultKey;
    private final CreateCallback<V> create;
    private final AddCallback<V> add;
    private final ClearCallback<V> clear;
    private final BitSet availabilityMap;
    private final Set<ResourceLocation> dummies = Sets.newHashSet();
    private final Set<Integer> blocked = Sets.newHashSet();
    private final Multimap<ResourceLocation, V> overrides = ArrayListMultimap.create();
    private final BiMap<OverrideOwner, V> override_owners = HashBiMap.create();
    private final DummyFactory<V> dummyFactory;
    private final boolean isDelegated;
    private final int min;
    private final int max;
    private final boolean allowOverrides;
    private final boolean isModifiable;

    private V defaultValue = null;
    boolean isFrozen = false;

    ForgeRegistry(Class<V> superType, ResourceLocation defaultKey, int min, int max, @Nullable CreateCallback<V> create, @Nullable AddCallback<V> add, @Nullable ClearCallback<V> clear, RegistryManager stage, boolean allowOverrides, boolean isModifiable, @Nullable DummyFactory<V> dummyFactory)
    {
        this.stage = stage;
        this.superType = superType;
        this.defaultKey = defaultKey;
        this.min = min;
        this.max = max;
        this.availabilityMap = new BitSet(Math.min(max + 1, 0x0FFF));
        this.create = create;
        this.add = add;
        this.clear = clear;
        this.isDelegated = IForgeRegistryEntry.Impl.class.isAssignableFrom(superType); //TODO: Make this IDelegatedRegistryEntry?
        this.allowOverrides = allowOverrides;
        this.isModifiable = isModifiable;
        this.dummyFactory = dummyFactory;
        if (this.create != null)
            this.create.onCreate(this, stage);
    }

    @Override
    public void register(V value)
    {
        add(-1, value);
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
    public Class<V> getRegistrySuperType()
    {
        return superType;
    }

    @Override
    public void registerAll(@SuppressWarnings("unchecked") V... values)
    {
        for (V value : values)
            register(value);
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
        return this.names.inverse().get(value);
    }

    @Override
    public Set<ResourceLocation> getKeys()
    {
        return ImmutableSet.copyOf(this.names.keySet());
    }

    @Override
    public List<V> getValues()
    {
        return ImmutableList.copyOf(this.names.values());
    }

    @Override
    public Set<Entry<ResourceLocation, V>> getEntries()
    {
        return ImmutableSet.copyOf(this.names.entrySet());
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

    public V getValue(int id)
    {
        V ret = this.ids.get(id);
        return ret == null ? this.defaultValue : ret;
    }

    void validateKey()
    {
        if (this.defaultKey != null)
            Validate.notNull(this.defaultValue, "Missing default of ForgeRegistry: " + this.defaultKey + " Type: " + this.superType);
    }

    ForgeRegistry<V> copy(RegistryManager stage)
    {
        return new ForgeRegistry<V>(superType, defaultKey, min, max, create, add, clear, stage, allowOverrides, isModifiable, dummyFactory);
    }

    int add(int id, V value)
    {
        ModContainer mc = Loader.instance().activeModContainer();
        String owner = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer)mc).wrappedContainer instanceof FMLContainer) ? null : mc.getModId().toLowerCase();
        return add(id, value, owner);
    }

    int add(int id, V value, String owner)
    {
        ResourceLocation key = value == null ? null : value.getRegistryName();
        Preconditions.checkNotNull(key, "Can't use a null-name for the registry, object %s.", value);
        Preconditions.checkNotNull(value, "Can't add null-object to the registry, name %s.", key);

        int idToUse = id;
        if (idToUse < 0 || availabilityMap.get(idToUse))
            idToUse = availabilityMap.nextClearBit(min);

        if (idToUse > max)
            throw new RuntimeException(String.format("Invalid id %d - maximum id range exceeded.", idToUse));

        V oldEntry = getRaw(key);
        if (oldEntry == value) // already registered, return prev registration's id
        {
            FMLLog.bigWarning("The object {} has been registered twice for the same name {}.", value, key);
            return this.getID(value);
        }
        if (oldEntry != null) // duplicate name
        {
            if (!this.allowOverrides)
                throw new IllegalArgumentException(String.format("The name %s has been registered twice, for %s and %s.", key, getRaw(key), value));
            if (owner == null)
                throw new IllegalStateException(String.format("Could not determine owner for the override on %s. Value: %s", key, value));
            this.override_owners.put(new OverrideOwner(owner, key), value);
            idToUse = this.getID(oldEntry);
            if (!this.override_owners.containsValue(oldEntry))
                this.override_owners.put(new OverrideOwner(key.getResourceDomain(), key), oldEntry);
        }

        Integer foundId = this.ids.inverse().get(value); //Is this ever possible to trigger with otherThing being different?
        if (foundId != null)
        {
            V otherThing = this.ids.get(foundId);
            throw new IllegalArgumentException(String.format("The object %s{%x} has been registered twice, using the names %s and %s. (Other object at this id is %s{%x})", value, System.identityHashCode(value), getKey(value), key, otherThing, System.identityHashCode(otherThing)));
        }

        if (isLocked())
            throw new IllegalStateException(String.format("The object %s (name %s) is being added too late.", value, key));

        if (defaultKey != null && defaultKey.equals(key))
        {
            if (this.defaultValue != null)
                throw new IllegalStateException(String.format("Attemped to override already set default value. This is not allowed: The object %s (name %s)", value, key));
            this.defaultValue = value;
        }

        this.names.put(key, value);
        this.ids.put(idToUse, value);
        this.availabilityMap.set(idToUse);

        if (isDelegated)
        {
            getDelegate(value).setName(key);
            if (oldEntry != null)
            {
                this.overrides.put(key, oldEntry);
                if (this.stage == RegistryManager.ACTIVE)
                    getDelegate(oldEntry).changeReference(value);
            }
        }

        if (this.add != null)
            this.add.onAdd(this, this.stage, idToUse, value);

        if (this.dummies.remove(key) && DEBUG)
            FMLLog.log.debug("Registry Dummy Remove: {}", key);

        if (DEBUG)
            FMLLog.log.trace("Registry add: {} {} {} (req. id {})", key, idToUse, value, id);

        return idToUse;
    }

    private V getRaw(ResourceLocation key)
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

    @Deprecated //Public for ByteByfUtils only!
    public V getRaw(int id)
    {
        return this.ids.get(id);
    }

    void addAlias(ResourceLocation from, ResourceLocation to)
    {
        if (this.isLocked())
            throw new IllegalStateException(String.format("Attempted to register the alias %s -> %s to late", from, to));
        this.aliases.put(from, to);
        if (DEBUG)
            FMLLog.log.trace("Registry alias: {} -> {}", from, to);
    }

    void addDummy(ResourceLocation key)
    {
        if (this.isLocked())
            throw new IllegalStateException(String.format("Attempted to register the dummy %s to late", key));
        this.dummies.add(key);
        if (DEBUG)
            FMLLog.log.trace("Registry dummy: {}", key);
    }

    private RegistryDelegate<V> getDelegate(V thing)
    {
        if (isDelegated)
            return (RegistryDelegate<V>)((IForgeRegistryEntry.Impl<V>)thing).delegate;
        else
            throw new IllegalStateException("Tried to get existing delegate from registry that is not delegated.");
    }

    void resetDelegates()
    {
        if (!this.isDelegated)
            return;

        for (V value : this)
            getDelegate(value).changeReference(value);

        for (V value: this.overrides.values())
            getDelegate(value).changeReference(value);
    }

    V getDefault()
    {
        return this.defaultValue;
    }

    boolean isDummied(ResourceLocation key)
    {
        return this.dummies.contains(key);
    }


    void validateContent(ResourceLocation registryName)
    {
        try
        {
            ReflectionHelper.findMethod(BitSet.class, "trimToSize", null).invoke(this.availabilityMap);
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
                throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, doesn't yield a name.", registryName, obj, id));

            // id is too high
            if (id > max)
                throw new IllegalStateException(String.format("Registry entry for %s %s, name %s uses the too large id %d.", registryName, obj, name, id));

            // id -> obj lookup is inconsistent
            if (getValue(id) != obj)
                throw new IllegalStateException(String.format("Registry entry for id %d, name %s, doesn't yield the expected %s %s.", id, name, registryName, obj));

            // name -> obj lookup is inconsistent
            if (getValue(name) != obj)
                throw new IllegalStateException(String.format("Registry entry for name %s, id %d, doesn't yield the expected %s %s.", name, id, registryName, obj));

            // name -> id lookup is inconsistent
            if (getID(name) != id)
                throw new IllegalStateException(String.format("Registry entry for name %s doesn't yield the expected id %d.", name, id));

            /*
            // entry is blocked, thus should be empty
            if (blockedIds.contains(id))
                throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, name %s, marked as dangling.", registryName, obj, id, name));
             */
        }
    }

    void sync(ResourceLocation name, ForgeRegistry<V> from)
    {
        if (from.superType != this.superType)
            throw new IllegalArgumentException("Attempted to copy to incompatible registry: " + name + " " + from.superType + " -> " + this.superType);

        this.isFrozen = false;

        if (this.clear != null)
            this.clear.onClear(this, stage);

        /* -- Should never need to be copied
        this.defaultKey = from.defaultKey;
        this.max = from.max;
        this.min = from.min;
        */
        this.aliases.clear();
        from.aliases.forEach((f, t) -> this.addAlias(f, t));

        this.ids.clear();
        this.names.clear();
        this.availabilityMap.clear(0, this.availabilityMap.length());
        this.defaultValue = null;
        this.override_owners.clear();
        this.override_owners.putAll(from.override_owners);

        boolean errored = false;

        for (Entry<ResourceLocation, V> entry : from.names.entrySet())
        {
            List<V> overrides = Lists.newArrayList(from.overrides.get(entry.getKey()));
            int id = from.getID(entry.getKey());
            if (overrides.isEmpty())
            {
                int realId = add(id, entry.getValue());
                if (id != realId && id != -1)
                {
                    FMLLog.log.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", entry.getKey(), this.getRegistrySuperType().getName(), id, realId);
                    errored = true;
                }
            }
            else
            {
                overrides.add(entry.getValue());
                for (V value : overrides)
                {
                    OverrideOwner owner = from.override_owners.inverse().get(value);
                    if (owner == null)
                    {
                        FMLLog.log.warn("Registered override did not have an associated owner object. Name: {} Type: {} Value: {}", entry.getKey(), this.getRegistrySuperType().getName(), value);
                        errored = true;
                        continue;
                    }
                    int realId = add(id, value, owner.owner);
                    if (id != realId && id != -1)
                    {
                        FMLLog.log.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", entry.getKey(), this.getRegistrySuperType().getName(), id, realId);
                        errored = true;
                    }
                }
            }
        }

        //Needs to be below add so that dummies are persisted
        this.dummies.clear();
        from.dummies.forEach(dummy -> this.addDummy(dummy));

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
        this.dummies.clear();

        this.ids.clear();
        this.names.clear();
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
            Integer id = this.ids.inverse().remove(value);
            if (id == null)
                throw new IllegalStateException("Removed a entry that did not have an associated id: " + key + " " + value.toString() + " This should never happen unless hackery!");
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
     * Users should only ever register things in the Register<?> events!
     */
    public void freeze()
    {
        this.isFrozen = true;
    }

    public void unfreeze()
    {
        this.isFrozen = false;
    }

    RegistryEvent.Register<V> getRegisterEvent(ResourceLocation name)
    {
        return new RegistryEvent.Register<V>(name, this);
    }

    void dump(ResourceLocation name)
    {
        List<Integer> ids = Lists.newArrayList();
        getKeys().forEach(n -> ids.add(getID(n)));

        Collections.sort(ids);

        FMLLog.log.trace("Registry Name : {}", name);
        ids.forEach(id -> FMLLog.log.trace("Registry: {} {} {}", id, getKey(getValue(id)), getValue(id)));
    }

    public void loadIds(Map<ResourceLocation, Integer> ids, Map<ResourceLocation, String> overrides, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, Integer[]> remapped, ForgeRegistry<V> old, ResourceLocation name)
    {
        for (Map.Entry<ResourceLocation, Integer> entry : ids.entrySet())
        {
            ResourceLocation itemName = entry.getKey();
            int newId = entry.getValue();
            int currId = old.getIDRaw(itemName);

            if (currId == -1)
            {
                FMLLog.log.info("Found a missing id from the world {}", itemName);
                missing.put(itemName, newId);
                continue; // no block/item -> nothing to add
            }
            else if (currId != newId)
            {
                FMLLog.log.debug("Fixed {} id mismatch {}: {} (init) -> {} (map).", name, itemName, currId, newId);
                remapped.put(itemName, new Integer[] {currId, newId});
            }
            V obj = old.getRaw(itemName);
            Preconditions.checkState(obj != null, "objectKey has an ID but no object. Reflection/ASM hackery? Registry bug?");

            String primaryName = null;
            if (!overrides.containsKey(itemName) && old.overrides.containsKey(itemName))
            {
                obj = old.overrides.get(itemName).iterator().next(); //Get the first one in the list, Which should be the first one registered
                primaryName = old.override_owners.inverse().get(obj).owner;
            }
            else
                primaryName = overrides.get(itemName);

            for (V value : old.overrides.get(itemName))
            {
                OverrideOwner owner = old.override_owners.inverse().get(value);
                if (owner == null)
                {
                    FMLLog.log.warn("Registered override did not have an associated owner object. Name: {} Type: {} Value: {}", entry.getKey(), this.getRegistrySuperType().getName(), value);
                    continue;
                }

                if (primaryName.equals(owner.owner))
                    continue;

                int realId = add(newId, value, owner.owner);
                if (newId != realId)
                    FMLLog.log.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", entry.getKey(), this.getRegistrySuperType().getName(), newId, realId);
            }

            int realId = add(newId, obj, primaryName == null ? itemName.getResourceDomain() : primaryName);
            if (realId != newId)
                FMLLog.log.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", entry.getKey(), this.getRegistrySuperType().getName(), newId, realId);
        }
    }

    boolean markDummy(ResourceLocation key, int id)
    {
        if (this.dummyFactory == null)
            return false;

        V dummy = this.dummyFactory.createDummy(key);
        if (DEBUG)
            FMLLog.log.trace("Registry Dummy Add: {} {} -> {}", key, id, dummy);

        //It was blocked before so we need to unset the blocking map
        this.availabilityMap.clear(id);

        int realId = this.add(id, dummy);
        if (realId != id)
            FMLLog.log.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", key, dummy.getRegistryType().getName(), id, realId);
        this.dummies.add(key);

        return true;
    }

    //Public for tests
    public Snapshot makeSnapshot()
    {
        Snapshot ret = new Snapshot();
        this.ids.forEach((id, value) -> ret.ids.put(getKey(value), id));
        this.aliases.forEach((from, to) -> ret.aliases.put(from, to));
        this.blocked.forEach(id -> ret.blocked.add(id));
        this.dummies.forEach(name -> ret.dummied.add(name));
        ret.overrides.putAll(getOverrideOwners());
        return ret;
    }

    Map<ResourceLocation, String> getOverrideOwners()
    {
        Map<ResourceLocation, String> ret = Maps.newHashMap();
        for (ResourceLocation key : this.overrides.keySet())
        {
            V obj = this.names.get(key);
            ret.put(key, this.override_owners.inverse().get(obj).owner);
        }
        return ret;
    }

    public static class Snapshot
    {
        public final Map<ResourceLocation, Integer> ids = Maps.newHashMap();
        public final Map<ResourceLocation, ResourceLocation> aliases = Maps.newHashMap();
        public final Set<Integer> blocked = Sets.newHashSet();
        public final Set<ResourceLocation> dummied = Sets.newHashSet();
        public final Map<ResourceLocation, String> overrides = Maps.newHashMap();
    }

    public MissingMappings<?> getMissingEvent(ResourceLocation name, Map<ResourceLocation, Integer> map)
    {
        List<MissingMappings.Mapping<V>> lst = Lists.newArrayList();
        map.forEach((rl, id) -> lst.add(new MissingMappings.Mapping<V>(this, rl, id)));
        return new MissingMappings<V>(name, this, lst);
    }

    void processMissingEvent(ResourceLocation name, ForgeRegistry<V> pool, List<MissingMappings.Mapping<V>> mappings, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, Integer[]> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed)
    {
        FMLLog.log.debug("Processing missing event for {}:", name);
        int ignored = 0;

        for (MissingMappings.Mapping<V> remap : mappings)
        {
            MissingMappings.Action action = remap.getAction();

            if (action == MissingMappings.Action.REMAP)
            {
                // entry re-mapped, finish the registration with the new name/object, but the old id
                int currId = getID(remap.getTarget());
                ResourceLocation newName = pool.getKey(remap.getTarget());
                FMLLog.log.debug("  Remapping {} -> {}.", remap.key, newName);

                missing.remove(remap.key);
                //I don't think this will work, but I dont think it ever worked.. the item is already in the map with a different id... we want to fix that..
                int realId = this.add(remap.id, remap.getTarget());
                if (realId != remap.id)
                    FMLLog.log.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", newName, this.getRegistrySuperType(), remap.id, realId);
                this.addAlias(remap.key, newName);


                if (currId != realId)
                {
                    FMLLog.log.info("  Fixed id mismatch {}: {} (init) -> {} (map).", newName, currId, realId);
                    remaps.put(newName, new Integer[] {currId, realId});
                }
            }
            else
            {
                // block item missing, warn as requested and block the id
                if (action == MissingMappings.Action.DEFAULT)
                {
                    defaulted.add(remap.key);
                }
                else if (action == MissingMappings.Action.IGNORE)
                {
                    FMLLog.log.debug("  Ignoring {}", remap.key);
                    ignored++;
                }
                else if (action == MissingMappings.Action.FAIL)
                {
                    FMLLog.log.debug("  Failing {}!", remap.key);
                    failed.add(remap.key);
                }
                else if (action == MissingMappings.Action.WARN)
                {
                    FMLLog.log.warn("  {} may cause world breakage!", remap.key);
                }
                this.block(remap.id);
            }
        }

        if (failed.isEmpty() && ignored > 0)
            FMLLog.log.debug("There were {} missing mappings that have been ignored", ignored);
    }

    private static class OverrideOwner
    {
        final String owner;
        final ResourceLocation key;
        private OverrideOwner(String owner, ResourceLocation key)
        {
            this.owner = owner;
            this.key = key;
        }

        public boolean equals(Object o)
        {
            if (this == o)
                return true;

            if (!(o instanceof OverrideOwner))
                return false;

            OverrideOwner oo = (OverrideOwner)o;
            return this.owner.equals(oo.owner) && this.key.equals(oo.key);
        }

        public int hashCode()
        {
            return 31 * this.key.hashCode() + this.owner.hashCode();
        }
    }
}
