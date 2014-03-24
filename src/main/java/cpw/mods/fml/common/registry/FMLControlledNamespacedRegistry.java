package cpw.mods.fml.common.registry;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class FMLControlledNamespacedRegistry<I> extends RegistryNamespaced {
    private final Class<I> superType;
    private String optionalDefaultName;
    private I optionalDefaultObject;
    private int maxId;
    private int minId;
    private char discriminator;
    // aliases redirecting legacy names to the actual name, may need recursive application to find the final name
    private final Map<String, String> aliases = new HashMap<String, String>();

    FMLControlledNamespacedRegistry(String optionalDefault, int maxIdValue, int minIdValue, Class<I> type, char discriminator)
    {
        this.superType = type;
        this.discriminator = discriminator;
        this.optionalDefaultName = optionalDefault;
        this.maxId = maxIdValue;
        this.minId = minIdValue;
    }

    void set(FMLControlledNamespacedRegistry<I> registry)
    {
        if (this.superType != registry.superType) throw new IllegalArgumentException("incompatible registry");

        this.discriminator = registry.discriminator;
        this.optionalDefaultName = registry.optionalDefaultName;
        this.maxId = registry.maxId;
        this.minId = registry.minId;
        this.aliases.putAll(registry.aliases);
        field_148759_a = new ObjectIntIdentityMap();
        field_82596_a.clear();

        for (Iterator<Object> it = registry.iterator(); it.hasNext(); )
        {
            I obj = (I) it.next();

            super.func_148756_a(registry.getId(obj), registry.func_148750_c(obj), obj);
        }
    }

    // public api

    /**
     * Add an object to the registry, trying to use the specified id.
     *
     * @deprecated register through {@link GameRegistry} instead.
     */
    @Override
    @Deprecated
    public void func_148756_a(int id, String name, Object thing)
    {
        GameData.getMain().register(thing, name, id);
    }

    @Override
    public I func_82594_a(String name)
    {
        I object = getRaw(name);
        return object == null ? this.optionalDefaultObject : object;
    }

    @Override
    public I func_148754_a(int id)
    {
        I object = getRaw(id);
        return object == null ? this.optionalDefaultObject : object;
    }

    /**
     * Get the object identified by the specified id.
     *
     * The default object is the air block for the block registry or null for the item registry.
     *
     * @param id Block/Item id.
     * @return Block/Item object or the default object if it wasn't found.
     */
    public I get(int id)
    {
        return func_148754_a(id);
    }

    /**
     * Get the object identified by the specified name.
     *
     * The default object is the air block for the block registry or null for the item registry.
     *
     * @param name Block/Item name.
     * @return Block/Item object or the default object if it wasn't found.
     */
    public I get(String name)
    {
        return func_82594_a(name);
    }

    /**
     * Get the id for the specified object.
     *
     * Don't hold onto the id across the world, it's being dynamically re-mapped as needed.
     *
     * Usually the name should be used instead of the id, if using the Block/Item object itself is
     * not suitable for the task.
     *
     * @param think Block/Item object.
     * @return Block/Item id or -1 if it wasn't found.
     */
    public int getId(I thing)
    {
        return func_148757_b(thing);
    }

    /**
     * Get the object identified by the specified id.
     *
     * @param id Block/Item id.
     * @return Block/Item object or null if it wasn't found.
     */
    public I getRaw(int id)
    {
        return superType.cast(super.func_148754_a(id));
    }

    /**
     * Get the object identified by the specified name.
     *
     * @param name Block/Item name.
     * @return Block/Item object or null if it wasn't found.
     */
    public I getRaw(String name)
    {
        I ret = superType.cast(super.func_82594_a(name));

        if (ret == null) // no match, try aliases recursively
        {
            name = aliases.get(name);

            if (name != null) return getRaw(name);
        }

        return ret;
    }

    @Override
    public boolean func_148741_d(String name)
    {
        boolean ret = super.func_148741_d(name);

        if (!ret) // no match, try aliases recursively
        {
            name = aliases.get(name);

            if (name != null) return func_148741_d(name);
        }

        return ret;
    }

    public int getId(String itemName)
    {
        I obj = getRaw(itemName);
        if (obj == null) return -1;

        return getId(obj);
    }

    public boolean contains(String itemName)
    {
        return func_148741_d(itemName);
    }

    // internal

    public void serializeInto(Map<String, Integer> idMapping)
    {
        for (Iterator<Object> it = iterator(); it.hasNext(); )
        {
            I thing = (I) it.next();
            idMapping.put(discriminator+func_148750_c(thing), getId(thing));
        }
    }

    public Map<String, String> getAliases()
    {
        return ImmutableMap.copyOf(aliases);
    }

    int add(int id, String name, I thing, BitSet availabilityMap)
    {
        if (name.equals(optionalDefaultName))
        {
            this.optionalDefaultObject = thing;
        }

        int idToUse = id;
        if (id == 0 || availabilityMap.get(id))
        {
            idToUse = availabilityMap.nextClearBit(minId);
        }
        if (idToUse >= maxId)
        {
            throw new RuntimeException(String.format("Invalid id %s - not accepted",id));
        }

        ModContainer mc = Loader.instance().activeModContainer();
        if (mc != null)
        {
            String prefix = mc.getModId();
            name = prefix + ":"+ name;
        }
        super.func_148756_a(idToUse, name, thing);
        FMLLog.finer("Add : %s %d %s", name, idToUse, thing);
        return idToUse;
    }

    void addAlias(String from, String to)
    {
        aliases.put(from, to);
    }

    Map<String,Integer> getEntriesNotIn(FMLControlledNamespacedRegistry<I> registry)
    {
        Map<String,Integer> ret = new HashMap<String, Integer>();

        for (Iterator<Object> it = iterator(); it.hasNext(); )
        {
            I thing = (I) it.next();
            if (!registry.field_148758_b.containsKey(thing)) ret.put(func_148750_c(thing), getId(thing));
        }

        return ret;
    }

    void dump()
    {
        List<Integer> ids = new ArrayList<Integer>();

        for (Iterator<Object> it = iterator(); it.hasNext(); )
        {
            ids.add(getId((I) it.next()));
        }

        // sort by id
        Collections.sort(ids);

        for (int id : ids)
        {
            I thing = getRaw(id);
            FMLLog.finer("Registry : %s %d %s", func_148750_c(thing), id, thing);
        }
    }
}
