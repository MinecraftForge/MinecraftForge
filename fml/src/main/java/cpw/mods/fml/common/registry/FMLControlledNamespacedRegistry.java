package cpw.mods.fml.common.registry;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.functions.GenericIterableFactory;
import cpw.mods.fml.common.registry.RegistryDelegate.Delegate;

public class FMLControlledNamespacedRegistry<I> extends RegistryNamespaced {
    public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("fml.debugRegistryEntries", "false"));
    private final Class<I> superType;
    private String optionalDefaultName;
    private I optionalDefaultObject;
    private int maxId;
    private int minId;
    private char discriminator;
    // aliases redirecting legacy names to the actual name, may need recursive application to find the final name.
    // these need to be registry specific, it's possible to only have a loosely linked item for a block which may get renamed by itself.
    private final Map<String, String> aliases = new HashMap<String, String>();
    private BiMap<String, I> persistentSubstitutions;
    private BiMap<String, I> activeSubstitutions = HashBiMap.create();

    FMLControlledNamespacedRegistry(String optionalDefault, int maxIdValue, int minIdValue, Class<I> type, char discriminator)
    {
        this.superType = type;
        this.discriminator = discriminator;
        this.optionalDefaultName = optionalDefault;
        this.maxId = maxIdValue;
        this.minId = minIdValue;
    }

    void validateContent(int maxId, String type, BitSet availabilityMap, Set<Integer> blockedIds, FMLControlledNamespacedRegistry<Block> iBlockRegistry)
    {
        for (I obj : typeSafeIterable())
        {
            int id = getId(obj);
            String name = getNameForObject(obj);
            boolean isSubstituted = activeSubstitutions.containsKey(name);

            // id lookup failed -> obj is not in the obj<->id map
            if (!isSubstituted && id < 0) throw new IllegalStateException(String.format("Registry entry for %s %s, name %s, doesn't yield an id.", type, obj, name));
            // id is too high
            if (id > maxId) throw new IllegalStateException(String.format("Registry entry for %s %s, name %s uses the too large id %d.", type, obj, name));
            // name lookup failed -> obj is not in the obj<->name map
            if (name == null) throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, doesn't yield a name.", type, obj, id));
            // empty name
            if (name.isEmpty()) throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, yields an empty name.", type, obj, id));
            // non-prefixed name
            if (name.indexOf(':') == -1) throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, has the non-prefixed name %s.", type, obj, id, name));
            // the rest of the tests don't really work for substituted items or blocks
            if (isSubstituted) continue;
            // id -> obj lookup is inconsistent
            if (getRaw(id) != obj) throw new IllegalStateException(String.format("Registry entry for id %d, name %s, doesn't yield the expected %s %s.", id, name, type, obj));
            // name -> obj lookup is inconsistent
            if (getRaw(name) != obj) throw new IllegalStateException(String.format("Registry entry for name %s, id %d, doesn't yield the expected %s %s.", name, id, type, obj));
            // name -> id lookup is inconsistent
            if (getId(name) != id) throw new IllegalStateException(String.format("Registry entry for name %s doesn't yield the expected id %d.", name, id));
            // id isn't marked as unavailable
            if (!availabilityMap.get(id)) throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, name %s, marked as empty.", type, obj, id, name));
            // entry is blocked, thus should be empty
            if (blockedIds.contains(id)) throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, name %s, marked as dangling.", type, obj, id, name));

            if (obj instanceof ItemBlock)
            {
                Block block = ((ItemBlock) obj).field_150939_a;

                // verify matching block entry
                if (iBlockRegistry.getId(block) != id)
                {
                    throw new IllegalStateException(String.format("Registry entry for ItemBlock %s, id %d, is missing or uses the non-matching id %d.", obj, id, iBlockRegistry.getId(block)));
                }
                // verify id range
                if (id > GameData.MAX_BLOCK_ID) throw new IllegalStateException(String.format("ItemBlock %s uses the id %d outside the block id range", name, id));
            }
        }

    }
    void set(FMLControlledNamespacedRegistry<I> registry)
    {
        if (this.superType != registry.superType) throw new IllegalArgumentException("incompatible registry");

        this.discriminator = registry.discriminator;
        this.optionalDefaultName = registry.optionalDefaultName;
        this.maxId = registry.maxId;
        this.minId = registry.minId;
        this.aliases.clear();
        this.aliases.putAll(registry.aliases);
        this.activeSubstitutions.clear();

        underlyingIntegerMap = new ObjectIntIdentityMap();
        registryObjects.clear();

        for (I thing : registry.typeSafeIterable())
        {
            int id = registry.getId(thing);
            addObjectRaw(id, registry.getNameForObject(thing), thing);
        }
        this.activeSubstitutions.putAll(registry.activeSubstitutions);
    }

    // public api

    /**
     * Add an object to the registry, trying to use the specified id.
     *
     * @deprecated register through {@link GameRegistry} instead.
     */
    @Override
    @Deprecated
    public void addObject(int id, String name, Object thing)
    {
        GameData.getMain().register(thing, name, id);
    }

    /**
     * DANGEROUS! EVIL! DO NOT USE!
     *
     * @deprecated register through {@link GameRegistry} instead.
     */
    @Override
    @Deprecated
    public void putObject(Object objName, Object obj)
    {
        String name = (String) objName;
        I thing = cast(obj);

        if (name == null) throw new NullPointerException("Can't use a null-name for the registry.");
        if (name.isEmpty()) throw new IllegalArgumentException("Can't use an empty name for the registry.");
        if (thing == null) throw new NullPointerException("Can't add null-object to the registry.");

        name = ensureNamespaced(name);
        String existingName = getNameForObject(thing);

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
     *
     * For blocks the default object is the air block, for items it's null.
     *
     * @param name Unique name identifying the object.
     * @return Registered object of the default object if it wasn't found-
     */
    @Override
    public I getObject(String name)
    {
        I object = getRaw(name);
        return object == null ? this.optionalDefaultObject : object;
    }

    /**
     * Fetch the object identified by the specified id or the default object.
     *
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
     * @deprecated use getObjectById instead
     */
    @Deprecated
    public I get(int id)
    {
        return getObjectById(id);
    }

    /**
     * @deprecated use getObject instead
     */
    @Deprecated
    public I get(String name)
    {
        return getObject(name);
    }

    /**
     * Get the id for the specified object.
     *
     * Don't hold onto the id across the world, it's being dynamically re-mapped as needed.
     *
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
        return cast(super.getObjectById(id));
    }

    /**
     * superType.cast appears to be expensive. Skip it for speed?
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    private I cast(Object obj)
    {
        return (I)(obj);
    }
    /**
     * Get the object identified by the specified name.
     *
     * @param name Block/Item name.
     * @return Block/Item object or null if it wasn't found.
     */
    public I getRaw(String name)
    {
        I ret = cast(super.getObject(name));

        if (ret == null) // no match, try aliases recursively
        {
            name = aliases.get(name);

            if (name != null) return getRaw(name);
        }

        return ret;
    }

    /**
     * Determine if the registry has an entry for the specified name.
     *
     * Aliased names will be resolved as well.
     *
     * @param name Object name to check.
     * @return true if a matching entry was found.
     */
    @Override
    public boolean containsKey(String name)
    {
        boolean ret = super.containsKey(name);

        if (!ret) // no match, try aliases recursively
        {
            name = aliases.get(name);

            if (name != null) return containsKey(name);
        }

        return ret;
    }

    /**
     * Get the id for the specified object.
     *
     * Don't hold onto the id across the world, it's being dynamically re-mapped as needed.
     *
     * Usually the name should be used instead of the id, if using the Block/Item object itself is
     * not suitable for the task.
     *
     * @param itemName Block/Item registry name.
     * @return Block/Item id or -1 if it wasn't found.
     */
    public int getId(String itemName)
    {
        I obj = getRaw(itemName);
        if (obj == null) return -1;

        return getId(obj);
    }

    /**
     * @deprecated use containsKey instead
     */
    @Deprecated
    public boolean contains(String itemName)
    {
        return containsKey(itemName);
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

    public void serializeInto(Map<String, Integer> idMapping) // for saving
    {
        for (I thing : this.typeSafeIterable())
        {
            idMapping.put(discriminator+getNameForObject(thing), getId(thing));
        }
    }

    public Map<String, String> getAliases() // for saving
    {
        return ImmutableMap.copyOf(aliases);
    }

    /**
     * Add the specified object to the registry.
     *
     * @param id ID to use if available, auto-assigned otherwise.
     * @param name Name to use, prefixed by the mod id.
     * @param thing Object to add.
     * @param availabilityMap Map marking available IDs for auto assignment.
     * @return ID eventually allocated.
     */
    int add(int id, String name, I thing, BitSet availabilityMap)
    {
        if (name == null) throw new NullPointerException(String.format("Can't use a null-name for the registry, object %s.", thing));
        if (name.isEmpty()) throw new IllegalArgumentException(String.format("Can't use an empty name for the registry, object %s.", thing));
        if (name.indexOf(':') == -1) throw new IllegalArgumentException(String.format("Can't add the name (%s) without a prefix, object %s", name, thing));
        if (thing == null) throw new NullPointerException(String.format("Can't add null-object to the registry, name %s.", name));
        if (name.equals(optionalDefaultName) && this.optionalDefaultObject == null)
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
            Object otherThing = getRaw(foundId);
            throw new IllegalArgumentException(String.format("The object %s{%x} has been registered twice, using the names %s and %s. (Other object at this id is %s{%x})", thing, System.identityHashCode(thing), getNameForObject(thing), name, otherThing, System.identityHashCode(otherThing)));
        }
        if (GameData.isFrozen(this))
        {
            FMLLog.bigWarning("The object %s (name %s) is being added too late.", thing, name);
        }

        if (activeSubstitutions.containsKey(name))
        {
            thing = activeSubstitutions.get(name);
        }
        addObjectRaw(idToUse, name, thing);

        if (DEBUG)
            FMLLog.finer("Registry add: %s %d %s (req. id %d)", name, idToUse, thing, id);
        return idToUse;
    }

    void addAlias(String from, String to)
    {
        aliases.put(from, to);
        if (DEBUG)
            FMLLog.finer("Registry alias: %s -> %s", from, to);
    }

    Map<String,Integer> getEntriesNotIn(FMLControlledNamespacedRegistry<I> registry)
    {
        Map<String,Integer> ret = new HashMap<String, Integer>();

        for (I thing : this.typeSafeIterable())
        {
            if (!registry.field_148758_b.containsKey(thing))
            {
                if (!registry.activeSubstitutions.containsKey(getNameForObject(thing)))
                {
                    ret.put(getNameForObject(thing), getId(thing));
                }
            }
        }

        return ret;
    }

    void dump()
    {
        if (!DEBUG)
            return;

        List<Integer> ids = new ArrayList<Integer>();

        for (I thing : this.typeSafeIterable())
        {
            ids.add(getId(thing));
        }

        // sort by id
        Collections.sort(ids);

        for (int id : ids)
        {
            I thing = getRaw(id);
            FMLLog.finer("Registry: %d %s %s", id, getNameForObject(thing), thing);
        }
    }

    /**
     * Version of addObject not using the API restricting overrides.
     */
    private void addObjectRaw(int id, String name, I thing)
    {
        if (name == null) throw new NullPointerException("The name to be added to the registry is null. This can only happen with a corrupted registry state. Reflection/ASM hackery? Registry bug?");
        if (thing == null) throw new NullPointerException("The object to be added to the registry is null. This can only happen with a corrupted registry state. Reflection/ASM hackery? Registry bug?");
        if (!superType.isInstance(thing)) throw new IllegalArgumentException("The object to be added to the registry is not of the right type. Reflection/ASM hackery? Registry bug?");

        underlyingIntegerMap.func_148746_a(thing, id); // obj <-> id
        super.putObject(name, thing); // name <-> obj
    }

    public I getDefaultValue()
    {
        return optionalDefaultObject;
    }

    public RegistryDelegate<I> getDelegate(I thing, Class<I> clazz) {
        return GameData.buildDelegate(thing, clazz);
    }

    void activateSubstitution(String nameToReplace)
    {
        if (getPersistentSubstitutions().containsKey(nameToReplace))
        {
            I original = getRaw(nameToReplace);
            if (superType == Item.class) {
                Item sub = (Item) getPersistentSubstitutions().get(nameToReplace);
                if (original == null) {
                    // When we're activated from the server side, we need to set the delegate on the original instance to
                    // point to us. Go to the "default state" registry to get it
                    original = (I)GameData.getItemRegistry().getRaw(nameToReplace);
                }
                FMLLog.log(Level.DEBUG, "Replacing %s with %s (name %s)", original, sub, nameToReplace);
                Delegate<Item> delegate = (Delegate<Item>)((Item)original).delegate;
                delegate.changeReference(sub);
                ((Delegate<Item>)sub.delegate).setName(nameToReplace);
            }
            activeSubstitutions.put(nameToReplace, getPersistentSubstitutions().get(nameToReplace));
        }
    }

    void addSubstitutionAlias(String modId, String nameToReplace, Object toReplace) throws ExistingSubstitutionException {
        if (getPersistentSubstitutions().containsKey(nameToReplace) || getPersistentSubstitutions().containsValue(toReplace))
        {
            FMLLog.severe("The substitution of %s has already occured. You cannot duplicate substitutions", nameToReplace);
            throw new ExistingSubstitutionException(nameToReplace, toReplace);
        }
        I replacement = cast(toReplace);
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

    public void serializeSubstitutions(Set<String> blockSubs)
    {
        blockSubs.addAll(activeSubstitutions.keySet());
    }

    private BiMap<String, I> getPersistentSubstitutions()
    {
        if (persistentSubstitutions == null)
        {
            persistentSubstitutions = GameData.getMain().getPersistentSubstitutionMap(superType);
        }
        return persistentSubstitutions;
    }

    /*
     * This iterator is used by some regular MC methods to visit all blocks, we need to include substitutions
     * Compare #typeSafeIterable()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Iterator<I> iterator()
    {
        return Iterators.concat(super.iterator(),getPersistentSubstitutions().values().iterator());
    }

    // ONLY CALLED ON ITEM registry
    void resetSubstitutionDelegates()
    {
        for (I item: typeSafeIterable()) {
            Delegate<Item> delegate = (Delegate<Item>)((Item)item).delegate;
            delegate.changeReference((Item)item);
        }
    }
}
