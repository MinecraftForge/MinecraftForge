package cpw.mods.fml.common.registry;

import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;

public class FMLControlledNamespacedRegistry<I> extends RegistryNamespaced {
    public class FMLObjectIntIdentityMap extends ObjectIntIdentityMap {
        private TIntIntHashMap oldMap;
        private TIntIntHashMap newMap;
        private ArrayList<Integer> oldIndex;
        private ArrayList<Integer> newIndex;

        public FMLObjectIntIdentityMap()
        {
        }

        boolean containsID(int id)
        {
            return func_148744_b(id);
        }

        Object get(int id)
        {
            return func_148745_a(id);
        }

        int get(Object obj)
        {
            return func_148747_b(obj);
        }

        @SuppressWarnings("unchecked")
        void beginSwap()
        {
            oldMap = field_148749_a;
            newMap  = new TIntIntHashMap(256, 0.5F, -1, -1);
            oldIndex = (ArrayList<Integer>) field_148748_b;
            newIndex = new ArrayList<Integer>(oldIndex.size());
        }

        void completeSwap()
        {
            field_148749_a = newMap;
            field_148748_b = newIndex;
            oldIndex = newIndex = null;
            oldMap = newMap = null;
        }

        void putNew(int id, Object item)
        {
            field_148749_a = newMap;
            field_148748_b = newIndex;
            super.func_148746_a(item, id);
            field_148749_a = oldMap;
            field_148748_b = oldIndex;
        }
    }

    private final Class<I> superType;
    private String optionalDefaultName;
    private I optionalDefaultObject;

    private BiMap<String,Integer> namedIds = HashBiMap.create();
    private BitSet availabilityMap;
    private int maxId;
    private int minId;

    public FMLControlledNamespacedRegistry(String optionalDefault, int maxIdValue, int minIdValue, Class<I> type)
    {
        this.superType = type;
        this.optionalDefaultName = optionalDefault;
        this.availabilityMap = new BitSet(maxIdValue);
        this.maxId = maxIdValue;
        this.minId = minIdValue;
        this.field_148759_a = new FMLObjectIntIdentityMap();
    }

    @Override
    public void func_148756_a(int id, String name, Object thing)
    {
        add(id, name, superType.cast(thing));
    }

    public void add(int id, String name, I thing)
    {
        if (name.equals(optionalDefaultName))
        {
            this.optionalDefaultObject = thing;
        }

        int idToUse = id;
        if (availabilityMap.get(id))
        {
            idToUse = availabilityMap.nextClearBit(minId);
        }
        if (idToUse >= maxId)
        {
            throw new RuntimeException(String.format("Invalid id %s - not accepted",id));
        }
        namedIds.put(name,idToUse);

        ModContainer mc = Loader.instance().activeModContainer();
        String prefix = mc.getModId();
        name = prefix + ":"+ name;
        super.func_148756_a(idToUse, name, thing);
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


    void injectIdMapping(BiMap<String,Integer> values)
    {
        MapDifference<String, Integer> difference = Maps.difference(values, namedIds);
        for (Entry<String, Integer> missing : difference.entriesOnlyOnLeft().entrySet())
        {
            String name = missing.getKey();
            Integer id = missing.getValue();
            String[] parts = name.split(":");
            ModContainer modContainer = GameData.findModOwner(parts[0]);
        }
        @SuppressWarnings("unchecked")
        BiMap<String,Object> nameMap = (BiMap<String,Object>) field_82596_a;
        FMLObjectIntIdentityMap idMap = (FMLObjectIntIdentityMap) field_148759_a;
        idMap.beginSwap();
        for (Entry<String, Integer> entry : values.entrySet())
        {
            String name = entry.getKey();
            Integer id = entry.getValue();
            Object item = nameMap.get(name);
            idMap.putNew(id, item);
        }

        idMap.completeSwap();
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
    public void serializeInto(Map<String, Integer> idMapping)
    {
        for (Entry<String, Integer> id: namedIds.entrySet())
        {
            idMapping.put(id.getKey(), id.getValue());
        }
    }
}
