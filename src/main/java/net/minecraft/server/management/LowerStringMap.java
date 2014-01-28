package net.minecraft.server.management;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowerStringMap implements Map
{
    private final Map internalMap = new LinkedHashMap();
    private static final String __OBFID = "CL_00001488";

    public int size()
    {
        return this.internalMap.size();
    }

    public boolean isEmpty()
    {
        return this.internalMap.isEmpty();
    }

    public boolean containsKey(Object par1Obj)
    {
        return this.internalMap.containsKey(par1Obj.toString().toLowerCase());
    }

    public boolean containsValue(Object par1Obj)
    {
        return this.internalMap.containsKey(par1Obj);
    }

    public Object get(Object par1Obj)
    {
        return this.internalMap.get(par1Obj.toString().toLowerCase());
    }

    public Object put(String par1Str, Object par2Obj)
    {
        return this.internalMap.put(par1Str.toLowerCase(), par2Obj);
    }

    public Object remove(Object par1Obj)
    {
        return this.internalMap.remove(par1Obj.toString().toLowerCase());
    }

    public void putAll(Map par1Map)
    {
        Iterator iterator = par1Map.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            this.put((String)entry.getKey(), entry.getValue());
        }
    }

    public void clear()
    {
        this.internalMap.clear();
    }

    public Set keySet()
    {
        return this.internalMap.keySet();
    }

    public Collection values()
    {
        return this.internalMap.values();
    }

    public Set entrySet()
    {
        return this.internalMap.entrySet();
    }

    public Object put(Object par1Obj, Object par2Obj)
    {
        return this.put((String)par1Obj, par2Obj);
    }
}