package cpw.mods.fml.common.registry;

import gnu.trove.map.hash.TIntIntHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class FMLControlledNamespacedRegistry<I> extends RegistryNamespaced {
    static class FMLObjectIntIdentityMap extends ObjectIntIdentityMap {
        private TIntIntHashMap frozenMap;
        private TIntIntHashMap oldMap;
        private TIntIntHashMap newMap;
        private ArrayList<Integer> frozenIndex;
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

        @SuppressWarnings("unchecked")
        void freezeMap()
        {
            frozenMap = new TIntIntHashMap(field_148749_a);
            frozenIndex = new ArrayList<Integer>(field_148748_b);
        }

        void revertToFrozen()
        {
            field_148749_a = frozenMap;
            field_148748_b = frozenIndex;
        }
        void completeSwap()
        {
            field_148749_a = newMap;
            field_148748_b = newIndex;
            oldIndex = newIndex = null;
            oldMap = newMap = null;
        }

        void revertSwap()
        {
            field_148749_a = oldMap;
            field_148748_b = oldIndex;
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

        List<Integer> usedIds()
        {
            return Ints.asList(field_148749_a.keys());
        }

    }

    private final Class<I> superType;
    private String optionalDefaultName;
    private I optionalDefaultObject;

    private BiMap<String,Integer> namedIds = HashBiMap.create();
    private BiMap<String,Integer> frozenIds;
    private Map<String,Integer> transactionalNamedIds;
    private BitSet transactionalAvailabilityMap;
    private BitSet availabilityMap;
    int maxId;
    private int minId;
    private char discriminator;

    public FMLControlledNamespacedRegistry(String optionalDefault, int maxIdValue, int minIdValue, Class<I> type, char discriminator)
    {
        this.superType = type;
        this.discriminator = discriminator;
        this.optionalDefaultName = optionalDefault;
        this.availabilityMap = new BitSet(maxIdValue);
        this.maxId = maxIdValue;
        this.minId = minIdValue;
        this.field_148759_a = new FMLObjectIntIdentityMap();
    }

    @Override
    public void func_148756_a(int id, String name, Object thing)
    {
        FMLLog.finer("Add : %s %d %s", name, id, thing);
        add(id, name, superType.cast(thing));
    }

    int swap(int id, String name, I thing)
    {
        FMLLog.fine("Swap : %s %d %s", name, id, thing);
        BitSet temporary = availabilityMap;
        availabilityMap = transactionalAvailabilityMap;

        int idToUse = id;
        if (id == 0 || availabilityMap.get(id))
        {
            idToUse = availabilityMap.nextClearBit(minId);
        }
        if (idToUse >= maxId)
        {
            throw new RuntimeException(String.format("Invalid id %s - not accepted",id));
        }

        namedIds.forcePut(func_148755_c(name),idToUse);
        reassignMapping(name, idToUse);
        useSlot(idToUse);
        availabilityMap = temporary;
        FMLLog.fine("Swap : %s %d %s", name, idToUse, thing);
        return idToUse;
    }
    public int add(int id, String name, I thing)
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
        namedIds.forcePut(func_148755_c(name),idToUse);
        super.func_148756_a(idToUse, name, thing);
        useSlot(idToUse);
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


    private FMLObjectIntIdentityMap idMap()
    {
        return (FMLObjectIntIdentityMap) field_148759_a;
    }

    @SuppressWarnings("unchecked")
    private BiMap<String,I> nameMap()
    {
        return (BiMap<String,I>) field_82596_a;
    }

    void beginIdSwap()
    {
        idMap().beginSwap();
        transactionalNamedIds = Maps.newHashMap();
        transactionalAvailabilityMap = new BitSet();
    }

    void reassignMapping(String name, int newId)
    {
        Object item = nameMap().get(name);
        idMap().putNew(newId, item);
        transactionalNamedIds.put(name,newId);
        transactionalAvailabilityMap.set(newId);
    }

    void freezeMap()
    {
        if (frozenIds == null)
        {
            frozenIds = ImmutableBiMap.copyOf(namedIds);
            idMap().freezeMap();
        }
    }

    void revertToFrozen()
    {
        namedIds = HashBiMap.create(frozenIds);
        idMap().revertToFrozen();
    }

    Map<String,Integer> getMissingMappings()
    {
        return Maps.difference(frozenIds, transactionalNamedIds).entriesOnlyOnLeft();
    }
    void completeIdSwap()
    {
        idMap().completeSwap();
        namedIds.clear();
        namedIds.putAll(transactionalNamedIds);
        transactionalNamedIds = null;
    }

    void revertSwap()
    {
        idMap().revertSwap();
        transactionalNamedIds = null;
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
            idMapping.put(discriminator+id.getKey(), id.getValue());
        }
    }

    public void useSlot(int id)
    {
        if (id >= maxId) return;
        availabilityMap.set(id);
    }

    List<Integer> usedIds()
    {
        return ((FMLObjectIntIdentityMap)field_148759_a).usedIds();
    }

    public int getId(String itemName)
    {
        if (namedIds.containsKey(itemName))
        {
            return namedIds.get(itemName);
        }
        else
        {
            return -1;
        }
    }

    public boolean contains(String itemName)
    {
        return namedIds.containsKey(itemName);
    }

    void dump()
    {
        for (Entry<String, Integer> entry : namedIds.entrySet())
        {
            String name = entry.getKey();
            Object thing = idMap().get(entry.getValue().intValue());
            FMLLog.finer("Registry : %s %d %s", name, entry.getValue(), thing);
        }
    }
    
    BitSet slots()
    {
    	return (BitSet) availabilityMap.clone();
    }
}
