package net.minecraftforge.oredict;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ItemCondition;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SensitiveOreDict
{
    private static final HashMap<String, List<ItemCondition>> nameToConditions = new HashMap<String, List<ItemCondition>>(1000);
    private static final HashMap<ItemCondition, List<String>> conditionToNames = new HashMap<ItemCondition, List<String>>(5000);

    public static void registerSensitiveOre(String name, ItemCondition condition)
    {
        if ("Unknown".equals(name))
        {
            return;
        }

        putNewOrAdd(nameToConditions, name, condition);
        putNewOrAdd(conditionToNames, condition, name);
        MinecraftForge.EVENT_BUS.post(new SensitiveOreRegisterEvent(name, condition));
    }

    @SuppressWarnings("unchecked")
    private static <K, V> void putNewOrAdd(Map<K, List<V>> map, K key, V value)
    {
        if (map.containsKey(key))
        {
            map.get(key).add(value);
        }
        else
        {
            map.put(key, Lists.newArrayList(value));
        }
    }

    /**
     * Returns if the item stack has the name according to this class and {@linkplain net.minecraftforge.oredict.OreDictionary}.
     *
     * @param itemStack
     * @param oreName
     * @return if the item stack has the name
     */
    public static boolean hasName(ItemStack itemStack, String oreName)
    {
        for (ItemCondition cond : getOres(oreName))
        {
            if (cond.check(itemStack))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all the ore names according this class and {@linkplain net.minecraftforge.oredict.OreDictionary}.
     * If nothing is found, returns empty list.
     * @param itemStack
     * @return All the names. Empty list if nothing found.
     */
    public static List<String> getNames(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return new ArrayList<String>(0);
        }

        List<String> names = new ArrayList<String>();
        for (ItemCondition cond: conditionToNames.keySet())
        {
            if (cond.check(itemStack))
            {
                names.addAll(conditionToNames.get(cond));
            }
        }
        return names;
    }

    public static ImmutableList<ItemCondition> getOres(String name)
    {
        return !nameToConditions.containsKey(name) ? ImmutableList.<ItemCondition>of() : ImmutableList.copyOf(nameToConditions.get(name));
    }

    public static class SensitiveOreRegisterEvent extends Event
    {
        public final String name;
        public final ItemCondition condition;

        public SensitiveOreRegisterEvent(String name, ItemCondition condition)
        {
            this.name = name;
            this.condition = condition;
        }
    }
}
