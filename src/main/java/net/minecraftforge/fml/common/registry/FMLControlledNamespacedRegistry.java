package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Level;

import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.functions.GenericIterableFactory;
import net.minecraftforge.fml.common.registry.RegistryDelegate.Delegate;

public class FMLControlledNamespacedRegistry<I> extends RegistryNamespacedDefaultedByKey<ResourceLocation, I>
{
    public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("fml.debugRegistryEntries", "false"));
    private final Class<I> superType;
    private final boolean isDelegated;
    private final Field delegateAccessor;
    private ResourceLocation optionalDefaultKey;
    private I optionalDefaultObject;
    private int maxId;
    private int minId;
    /**
     * Aliases are resource location to resource location pointers, allowing for alternative names to be supplied
     * pointing at the same thing. They are used to allow programmatic migration of an ID.
     */
    private final Map<ResourceLocation, ResourceLocation> aliases = Maps.newHashMap();
    /**
     * Persistent substitutions are the mechanism to allow mods to override specific behaviours with new behaviours.
     */
    private final BiMap<ResourceLocation, I> persistentSubstitutions = HashBiMap.create();
    /**
     * This is the current active substitution set for a particular world. It will change as worlds come and go.
     */
    private final BiMap<ResourceLocation, I> activeSubstitutions = HashBiMap.create();
    /**
     * The list of IDs blocked for this world. IDs will never be allocated in this set.
     */
    private final Set<Integer> blockedIds = Sets.newHashSet();

    private final Set<ResourceLocation> dummiedLocations = Sets.newHashSet();

    private final BitSet availabilityMap;

    private final AddCallback<I> addCallback;

    public interface AddCallback<T>
    {
        public void onAdd(T obj, int id);
    }

    FMLControlledNamespacedRegistry(ResourceLocation defaultKey, int maxIdValue, int minIdValue, Class<I> type, boolean isDelegated)
    {
        this(defaultKey, maxIdValue, minIdValue, type, isDelegated, null);
    }

    FMLControlledNamespacedRegistry(ResourceLocation defaultKey, int maxIdValue, int minIdValue, Class<I> type, boolean isDelegated, AddCallback<I> callback)
    {
        super(defaultKey);
        this.superType = type;
        this.optionalDefaultKey = defaultKey;
        this.maxId = maxIdValue;
        this.minId = minIdValue;
        this.availabilityMap = new BitSet(maxIdValue + 1);
        this.isDelegated = isDelegated;
        if (this.isDelegated)
        {
            try
            {
                this.delegateAccessor = type.getField("delegate");
            } catch (NoSuchFieldException e)
            {
                FMLLog.log(Level.ERROR, e, "Delegate class identified with missing delegate field");
                throw Throwables.propagate(e);
            }
        }
        else
        {
            this.delegateAccessor = null;
        }
        this.addCallback = callback;
    }

    void validateContent(ResourceLocation registryName)
    {
        for (I obj : typeSafeIterable())
        {
            int id = getId(obj);
            ResourceLocation name = getNameForObject(obj);
            boolean isSubstituted = activeSubstitutions.containsKey(name);

            // name lookup failed -> obj is not in the obj<->name map
            if (name == null)
            {
                throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, doesn't yield a name.", registryName, obj, id));
            }
            // id lookup failed -> obj is not in the obj<->id map
            if (!isSubstituted && id < 0)
            {
                throw new IllegalStateException(String.format("Registry entry for %s %s, name %s, doesn't yield an id.", registryName, obj, name));
            }
            // id is too high
            if (id > maxId)
            {
                throw new IllegalStateException(String.format("Registry entry for %s %s, name %s uses the too large id %d.", registryName, obj, name, id));
            }
            // the rest of the tests don't really work for substituted items or blocks
            if (isSubstituted)
            {
                continue;
            }
            // id -> obj lookup is inconsistent
            if (getRaw(id) != obj)
            {
                throw new IllegalStateException(String.format("Registry entry for id %d, name %s, doesn't yield the expected %s %s.", id, name, registryName, obj));
            }
            // name -> obj lookup is inconsistent
            if (getRaw(name) != obj)
            {
                throw new IllegalStateException(String.format("Registry entry for name %s, id %d, doesn't yield the expected %s %s.", name, id, registryName, obj));
            }
            // name -> id lookup is inconsistent
            if (getId(name) != id)
            {
                throw new IllegalStateException(String.format("Registry entry for name %s doesn't yield the expected id %d.", name, id));
            }
/*
            // entry is blocked, thus should be empty
            if (blockedIds.contains(id))
            {
                throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, name %s, marked as dangling.", registryName, obj, id, name));
            }
*/
        }

    }

    void set(FMLControlledNamespacedRegistry<I> otherRegistry)
    {
        if (this.superType != otherRegistry.superType)
        {
            throw new IllegalArgumentException("incompatible registry");
        }

        this.optionalDefaultKey = otherRegistry.optionalDefaultKey;
        this.maxId = otherRegistry.maxId;
        this.minId = otherRegistry.minId;
        this.aliases.clear();
        this.aliases.putAll(otherRegistry.aliases);
        this.persistentSubstitutions.clear();
        this.persistentSubstitutions.putAll(otherRegistry.getPersistentSubstitutions());
        this.activeSubstitutions.clear();
        this.dummiedLocations.clear();
        this.dummiedLocations.addAll(otherRegistry.dummiedLocations);

        underlyingIntegerMap = new ObjectIntIdentityMap<I>();
        registryObjects.clear();

        for (I thing : otherRegistry.typeSafeIterable())
        {
            addObjectRaw(otherRegistry.getId(thing), otherRegistry.getNameForObject(thing), thing);
        }
        this.activeSubstitutions.putAll(otherRegistry.activeSubstitutions);
    }

    // public api

    /**
     * Add an object to the registry, trying to use the specified id.
     * This is required, to re-route vanilla block and item registration through to
     * the {@link #add} method.
     *
     * @deprecated register through {@link GameRegistry} instead.
     */
    @Override
    @Deprecated
    public void register(int id, ResourceLocation name, I thing)
    {
        add(id, name, thing);
    }

    /**
     * DANGEROUS! EVIL! DO NOT USE!
     *
     * @deprecated register through {@link GameRegistry} instead.
     */
    @Override
    @Deprecated
    public void putObject(ResourceLocation name, I thing)
    {

        if (name == null)
        {
            throw new NullPointerException("Can't use a null-name for the registry.");
        }
        if (thing == null)
        {
            throw new NullPointerException("Can't add null-object to the registry.");
        }

        ResourceLocation existingName = getNameForObject(thing);

        if (existingName == null)
        {
            FMLLog.bigWarning("Ignoring putObject(%s, %s), not resolvable", name, thing);
        }
        else if (existingName.equals(name))
        {
            FMLLog.bigWarning("Ignoring putObject(%s, %s), already added", name, thing);
        }
        else
        {
            FMLLog.bigWarning("Ignoring putObject(%s, %s), adding alias to %s instead", name, thing, existingName);
            addAlias(name, existingName);
        }
    }

    /**
     * Fetch the object identified by the specified name or the default object.
     * <p/>
     * For blocks the default object is the air block, for items it's null.
     *
     * @param name Unique name identifying the object.
     * @return Registered object of the default object if it wasn't found-
     */
    @Override
    public I getObject(ResourceLocation name)
    {
        I object = getRaw(name);
        return object == null ? this.optionalDefaultObject : object;
    }

    /**
     * Fetch the object identified by the specified id or the default object.
     * <p/>
     * For blocks the default object is the air block, for items it's null.
     *
     * @param id ID identifying the object.
     * @return Registered object of the default object if it wasn't found-
     */
    @Override
    public I getObjectById(int id)
    {
        I object = getRaw(id);
        return object == null ? this.optionalDefaultObject : object;
    }

    /**
     * Get the id for the specified object.
     * <p/>
     * Don't hold onto the id across the world, it's being dynamically re-mapped as needed.
     * <p/>
     * Usually the name should be used instead of the id, if using the Block/Item object itself is
     * not suitable for the task.
     *
     * @param thing Block/Item object.
     * @return Block/Item id or -1 if it wasn't found.
     */
    public int getId(I thing)
    {
        return getIDForObject(thing);
    }

    /**
     * Get the object identified by the specified id.
     *
     * @param id Block/Item id.
     * @return Block/Item object or null if it wasn't found.
     */
    public I getRaw(int id)
    {
        return super.getObjectById(id);
    }

    /**
     * Get the object identified by the specified name.
     *
     * @param name Block/Item name.
     * @return Block/Item object or null if it wasn't found.
     */
    private I getRaw(ResourceLocation name)
    {
        I ret = super.getObject(name);

        if (ret == null) // no match, try aliases recursively
        {
            name = aliases.get(name);

            if (name != null)
            {
                return getRaw(name);
            }
        }

        return ret;
    }

    /**
     * Determine if the registry has an entry for the specified name.
     * <p/>
     * Aliased names will be resolved as well.
     *
     * @param name Object name to check.
     * @return true if a matching entry was found.
     */
    @Override
    public boolean containsKey(ResourceLocation name)
    {
        boolean ret = super.containsKey(name);

        if (!ret) // no match, try aliases recursively
        {
            name = aliases.get(name);

            if (name != null)
            {
                return containsKey(name);
            }
        }

        return ret;
    }

    /**
     * Get the id for the specified object.
     * <p/>
     * Don't hold onto the id across the world, it's being dynamically re-mapped as needed.
     * <p/>
     * Usually the name should be used instead of the id, if using the Block/Item object itself is
     * not suitable for the task.
     *
     * @param itemName Block/Item registry name.
     * @return Block/Item id or -1 if it wasn't found.
     */
    public int getId(ResourceLocation itemName)
    {
        I obj = getRaw(itemName);
        if (obj == null)
        {
            return -1;
        }

        return getId(obj);
    }

    /*
     * This iterator is used by FML to visit the actual block sets, it should use the super.iterator method instead
     * Compare #iterator()
     */
    public Iterable<I> typeSafeIterable()
    {
        return GenericIterableFactory.newCastingIterable(super.iterator(), superType);
    }

    // internal

    public void serializeIds(Map<ResourceLocation, Integer> idMapping) // for saving
    {
        for (I thing : this.typeSafeIterable())
        {
            idMapping.put(getNameForObject(thing), getId(thing));
        }
    }

    public void serializeAliases(Map<ResourceLocation, ResourceLocation> map)
    {
        map.putAll(this.aliases);
    }

    public void serializeSubstitutions(Set<ResourceLocation> set)
    {
        set.addAll(activeSubstitutions.keySet());
    }

    public void serializeDummied(Set<ResourceLocation> set) { set.addAll(this.dummiedLocations); }


    /**
     * Add the specified object to the registry.
     *
     * @param id    ID to use if available, auto-assigned otherwise.
     * @param name  Name to use, prefixed by the mod id.
     * @param thing Object to add.
     * @return ID eventually allocated.
     */
    int add(int id, ResourceLocation name, I thing)
    {
        if (name == null)
        {
            throw new NullPointerException(String.format("Can't use a null-name for the registry, object %s.", thing));
        }
        if (thing == null)
        {
            throw new NullPointerException(String.format("Can't add null-object to the registry, name %s.", name));
        }
        if (optionalDefaultKey != null && optionalDefaultKey.equals(name) && this.optionalDefaultObject == null)
        {
            this.optionalDefaultObject = thing;
        }
        if (getPersistentSubstitutions().containsValue(thing))
        {
            throw new IllegalArgumentException(String.format("The object %s (%s) cannot be added to the registry. It is already being used as a substitute for %s", thing.getClass(), name, getPersistentSubstitutions().inverse().get(thing)));
        }
        int idToUse = id;
        if (idToUse < 0 || availabilityMap.get(idToUse))
        {
            idToUse = availabilityMap.nextClearBit(minId);
        }
        if (idToUse > maxId)
        {
            throw new RuntimeException(String.format("Invalid id %d - maximum id range exceeded.", idToUse));
        }

        if (getRaw(name) == thing) // already registered, return prev registration's id
        {
            FMLLog.bigWarning("The object %s has been registered twice for the same name %s.", thing, name);
            return getId(thing);
        }
        if (getRaw(name) != null) // duplicate name
        {
            throw new IllegalArgumentException(String.format("The name %s has been registered twice, for %s and %s.", name, getRaw(name), thing));
        }
        if (getId(thing) >= 0) // duplicate object - but only if it's not being substituted
        {
            int foundId = getId(thing);
            I otherThing = getRaw(foundId);
            throw new IllegalArgumentException(String.format("The object %s{%x} has been registered twice, using the names %s and %s. (Other object at this id is %s{%x})", thing, System.identityHashCode(thing), getNameForObject(thing), name, otherThing, System.identityHashCode(otherThing)));
        }
        if (PersistentRegistryManager.isFrozen(this))
        {
            FMLLog.bigWarning("The object %s (name %s) is being added too late.", thing, name);
        }

        if (activeSubstitutions.containsKey(name))
        {
            I oldThing = thing;
            thing = activeSubstitutions.get(name);
            if (DEBUG)
            {
                FMLLog.getLogger().log(Level.DEBUG, "Active substitution: {} {}@{} -> {}@{}", name, oldThing.getClass().getName(), System.identityHashCode(oldThing), thing.getClass().getName(), System.identityHashCode(thing));
            }
        }


        addObjectRaw(idToUse, name, thing);
        if (isDelegated)
        {
            getExistingDelegate(thing).setResourceName(name);
        }

        if (this.dummiedLocations.remove(name) && DEBUG)
        {
            FMLLog.fine("Registry Dummy Remove: %s", name);
        }

        if (DEBUG)
        {
            FMLLog.finer("Registry add: %s %d %s (req. id %d)", name, idToUse, thing, id);
        }
        return idToUse;
    }

    void markDummy(ResourceLocation rl, Integer id, I thing)
    {
        if (DEBUG)
        {
            FMLLog.finer("Registry Dummy Add: %s %d -> %s", rl, id, thing);
        }
        this.dummiedLocations.add(rl);
        this.addObjectRaw(id, rl, thing);
    }

    void addAlias(ResourceLocation from, ResourceLocation to)
    {
        aliases.put(from, to);
        if (DEBUG)
        {
            FMLLog.finer("Registry alias: %s -> %s", from, to);
        }
    }

    Map<ResourceLocation, Integer> getEntriesNotIn(FMLControlledNamespacedRegistry<I> registry)
    {
        Map<ResourceLocation, Integer> ret = new HashMap<ResourceLocation, Integer>();

        for (I thing : this.typeSafeIterable())
        {
            if (!registry.inverseObjectRegistry.containsKey(thing))
            {
                if (!registry.activeSubstitutions.containsKey(getNameForObject(thing)))
                {
                    ret.put(getNameForObject(thing), getId(thing));
                }
            }
        }

        return ret;
    }

    void dump(ResourceLocation registryName)
    {
        if (!DEBUG)
        {
            return;
        }

        List<Integer> ids = new ArrayList<Integer>();

        for (I thing : this.typeSafeIterable())
        {
            ids.add(getId(thing));
        }

        // sort by id
        Collections.sort(ids);
        FMLLog.finer("Registry Name : {}", registryName);
        for (int id : ids)
        {
            I thing = getRaw(id);
            FMLLog.finer("Registry: %d %s %s", id, getNameForObject(thing), thing);
        }
    }

    /**
     * Version of addObject not using the API restricting overrides.
     */
    private void addObjectRaw(int id, ResourceLocation name, I thing)
    {
        if (name == null)
        {
            throw new NullPointerException("The name to be added to the registry is null. This can only happen with a corrupted registry state. Reflection/ASM hackery? Registry bug?");
        }
        if (thing == null)
        {
            throw new NullPointerException("The object to be added to the registry is null. This can only happen with a corrupted registry state. Reflection/ASM hackery? Registry bug?");
        }
        if (!superType.isInstance(thing))
        {
            throw new IllegalArgumentException("The object to be added to the registry is not of the right type. Reflection/ASM hackery? Registry bug?");
        }

        underlyingIntegerMap.put(thing, id); // obj <-> id
        super.putObject(name, thing); // name <-> obj
        availabilityMap.set(id);
        if (addCallback != null)
        {
            addCallback.onAdd(thing, id);
        }
    }

    public I getDefaultValue()
    {
        return optionalDefaultObject;
    }

    public RegistryDelegate<I> getDelegate(I thing, Class<I> clazz)
    {
        return new RegistryDelegate.Delegate<I>(thing, clazz);
    }

    @SuppressWarnings("unchecked")
    public Delegate<I> getExistingDelegate(I thing)
    {
        try
        {
            return (Delegate<I>)delegateAccessor.get(thing);
        } catch (IllegalAccessException e)
        {
            FMLLog.log(Level.ERROR, e, "Illegal attempt to access delegate");
            throw Throwables.propagate(e);
        }
    }

    I activateSubstitution(ResourceLocation nameToReplace)
    {
        if (getPersistentSubstitutions().containsKey(nameToReplace))
        {
            I original = getRaw(nameToReplace);
            I sub = getPersistentSubstitutions().get(nameToReplace);
            getExistingDelegate(original).changeReference(sub);
            activeSubstitutions.put(nameToReplace, sub);
            return original;
        }
        return null;
    }

    void addSubstitutionAlias(String modId, ResourceLocation nameToReplace, I replacement) throws ExistingSubstitutionException
    {
        if (getPersistentSubstitutions().containsKey(nameToReplace) || getPersistentSubstitutions().containsValue(replacement))
        {
            FMLLog.severe("The substitution of %s has already occurred. You cannot duplicate substitutions", nameToReplace);
            throw new ExistingSubstitutionException(nameToReplace, replacement);
        }
        I original = getRaw(nameToReplace);
        if (original == null)
        {
            throw new NullPointerException("The replacement target is not present. This won't work");
        }
        if (!original.getClass().isAssignableFrom(replacement.getClass()))
        {
            FMLLog.severe("The substitute %s for %s (type %s) is type incompatible. This won't work", replacement.getClass().getName(), nameToReplace, original.getClass().getName());
            throw new IncompatibleSubstitutionException(nameToReplace, replacement, original);
        }
        int existingId = getId(replacement);
        if (existingId != -1)
        {
            FMLLog.severe("The substitute %s for %s is registered into the game independently. This won't work", replacement.getClass().getName(), nameToReplace);
            throw new IllegalArgumentException("The object substitution is already registered. This won't work");
        }
        FMLLog.log(Level.DEBUG, "Adding substitution %s with %s (name %s)", original, replacement, nameToReplace);
        getPersistentSubstitutions().put(nameToReplace, replacement);
    }

    BiMap<ResourceLocation, I> getPersistentSubstitutions()
    {
        return persistentSubstitutions;
    }

    @Override
    public void validateKey()
    {
        if (this.optionalDefaultKey != null)
        {
            Validate.notNull(this.optionalDefaultObject);
        }
    }

    /*
     * This iterator is used by some regular MC methods to visit all blocks, we need to include substitutions
     * Compare #typeSafeIterable()
     */
    @Override
    public Iterator<I> iterator()
    {
        return Iterators.concat(super.iterator(), getPersistentSubstitutions().values().iterator());
    }


    FMLControlledNamespacedRegistry<I> makeShallowCopy()
    {
        return new FMLControlledNamespacedRegistry<I>(optionalDefaultKey, maxId, minId, superType, isDelegated);
    }

    void resetSubstitutionDelegates()
    {
        if (!isDelegated)
        {
            return;
        }
        for (I obj : typeSafeIterable())
        {
            Delegate<I> delegate = getExistingDelegate(obj);
            delegate.changeReference(obj);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> FMLControlledNamespacedRegistry<T> asType(Class<? extends T> type)
    {
        return (FMLControlledNamespacedRegistry<T>)this;
    }

    public void serializeBlockList(Set<Integer> blocked)
    {
        blocked.addAll(this.blockedIds);
    }

    public Set<? extends ResourceLocation> getActiveSubstitutions()
    {
        return activeSubstitutions.keySet();
    }

    public void loadAliases(Map<ResourceLocation, ResourceLocation> aliases)
    {
        for (Map.Entry<ResourceLocation, ResourceLocation> alias : aliases.entrySet())
        {
            addAlias(alias.getKey(), alias.getValue());
        }
    }

    public void loadSubstitutions(Set<ResourceLocation> substitutions)
    {
        for (ResourceLocation rl : substitutions)
        {
            activateSubstitution(rl);
        }
    }

    public void loadBlocked(Set<Integer> blocked)
    {
        for (Integer id : blocked)
        {
            blockedIds.add(id);
            availabilityMap.set(id);
        }
    }

    public void loadDummied(Set<ResourceLocation> dummied)
    {
        if (DEBUG && dummied.size() > 0)
        {
            FMLLog.fine("Registry Dummy Load: [%s]", Joiner.on(", ").join(dummied));
        }
        this.dummiedLocations.addAll(dummied);
    }

    public void loadIds(Map<ResourceLocation, Integer> ids, Map<ResourceLocation, Integer> missingIds, Map<ResourceLocation, Integer[]> remappedIds, FMLControlledNamespacedRegistry<I> currentRegistry, ResourceLocation registryName)
    {
        for (Map.Entry<ResourceLocation, Integer> entry : ids.entrySet())
        {
            ResourceLocation itemName = entry.getKey();
            int newId = entry.getValue();
            int currId = currentRegistry.getId(itemName);

            if (currId == -1)
            {
                FMLLog.info("Found a missing id from the world %s", itemName);
                missingIds.put(entry.getKey(), newId);
                continue; // no block/item -> nothing to add
            }
            else if (currId != newId)
            {
                FMLLog.fine("Fixed %s id mismatch %s: %d (init) -> %d (map).", registryName, itemName, currId, newId);
                remappedIds.put(itemName, new Integer[] {currId, newId});
            }
            I obj = currentRegistry.getRaw(itemName);

            add(newId, itemName, obj);
        }
    }

    public void blockId(int id)
    {
        blockedIds.add(id);
    }

    public void notifyCallbacks()
    {
        if (addCallback == null)
            return;

        for (I i : this.underlyingIntegerMap)
        {
            addCallback.onAdd(i, this.underlyingIntegerMap.get(i));
        }
    }

    @Override
    public ResourceLocation getNameForObject(I p_177774_1_)
    {
        ResourceLocation rl = super.getNameForObject(p_177774_1_);
        if (rl == null)
        {
            rl = activeSubstitutions.inverse().get(p_177774_1_);
        }
        return rl;
    }
}
