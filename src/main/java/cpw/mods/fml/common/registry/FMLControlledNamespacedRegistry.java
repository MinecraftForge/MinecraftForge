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
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class FMLControlledNamespacedRegistry<I> extends RegistryNamespaced {
    private final Class<I> superType;
    private String optionalDefaultName;
    private I optionalDefaultObject;

    int maxId;
    private int minId;
    private char discriminator;

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
        field_148759_a = new ObjectIntIdentityMap();
        field_82596_a.clear();

        for (Iterator<Object> it = registry.iterator(); it.hasNext(); )
        {
            I obj = (I) it.next();

            super.func_148756_a(registry.getId(obj), registry.func_148750_c(obj), obj);
        }
    }

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

    @Override
    public I func_82594_a(String name)
    {
        I object = superType.cast(super.func_82594_a(name));
        return object == null ? this.optionalDefaultObject : object;
    }

    @Override
    public I func_148754_a(int id)
    {
        I object = superType.cast(super.func_148754_a(id));
        return object == null ? this.optionalDefaultObject : object;
    }


    private ObjectIntIdentityMap idMap()
    {
        return field_148759_a;
    }

    @SuppressWarnings("unchecked")
    private BiMap<String,I> nameMap()
    {
        return (BiMap<String,I>) field_82596_a;
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

    public I get(int id)
    {
        return func_148754_a(id);
    }

    public I get(String name)
    {
        return func_82594_a(name);
    }

    public int getId(I thing)
    {
        return func_148757_b(thing);
    }

    public I getRaw(int id)
    {
        return superType.cast(super.func_148754_a(id));
    }

    public I getRaw(String name)
    {
        return superType.cast(super.func_82594_a(name));
    }

    public void serializeInto(Map<String, Integer> idMapping)
    {
        for (Iterator<Object> it = iterator(); it.hasNext(); )
        {
            I thing = (I) it.next();
            idMapping.put(discriminator+func_148750_c(thing), getId(thing));
        }
    }

    public int getId(String itemName)
    {
        I obj = getRaw(itemName);
        if (obj == null) return -1;

        return getId(obj);
    }

    public boolean contains(String itemName)
    {
        return field_82596_a.containsKey(itemName);
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
