package net.minecraft.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class WeightedRandom
{
    private static final String __OBFID = "CL_00001503";

    // JAVADOC METHOD $$ func_76272_a
    public static int getTotalWeight(Collection par0Collection)
    {
        int i = 0;
        WeightedRandom.Item item;

        for (Iterator iterator = par0Collection.iterator(); iterator.hasNext(); i += item.itemWeight)
        {
            item = (WeightedRandom.Item)iterator.next();
        }

        return i;
    }

    // JAVADOC METHOD $$ func_76273_a
    public static WeightedRandom.Item getRandomItem(Random par0Random, Collection par1Collection, int par2)
    {
        if (par2 <= 0)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            int j = par0Random.nextInt(par2);
            Iterator iterator = par1Collection.iterator();
            WeightedRandom.Item item;

            do
            {
                if (!iterator.hasNext())
                {
                    return null;
                }

                item = (WeightedRandom.Item)iterator.next();
                j -= item.itemWeight;
            }
            while (j >= 0);

            return item;
        }
    }

    // JAVADOC METHOD $$ func_76271_a
    public static WeightedRandom.Item getRandomItem(Random par0Random, Collection par1Collection)
    {
        // JAVADOC METHOD $$ func_76273_a
        return getRandomItem(par0Random, par1Collection, getTotalWeight(par1Collection));
    }

    // JAVADOC METHOD $$ func_76270_a
    public static int getTotalWeight(WeightedRandom.Item[] par0ArrayOfWeightedRandomItem)
    {
        int i = 0;
        WeightedRandom.Item[] aitem = par0ArrayOfWeightedRandomItem;
        int j = par0ArrayOfWeightedRandomItem.length;

        for (int k = 0; k < j; ++k)
        {
            WeightedRandom.Item item = aitem[k];
            i += item.itemWeight;
        }

        return i;
    }

    // JAVADOC METHOD $$ func_76269_a
    public static WeightedRandom.Item getRandomItem(Random par0Random, WeightedRandom.Item[] par1ArrayOfWeightedRandomItem, int par2)
    {
        if (par2 <= 0)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            int j = par0Random.nextInt(par2);
            WeightedRandom.Item[] aitem = par1ArrayOfWeightedRandomItem;
            int k = par1ArrayOfWeightedRandomItem.length;

            for (int l = 0; l < k; ++l)
            {
                WeightedRandom.Item item = aitem[l];
                j -= item.itemWeight;

                if (j < 0)
                {
                    return item;
                }
            }

            return null;
        }
    }

    // JAVADOC METHOD $$ func_76274_a
    public static WeightedRandom.Item getRandomItem(Random par0Random, WeightedRandom.Item[] par1ArrayOfWeightedRandomItem)
    {
        // JAVADOC METHOD $$ func_76269_a
        return getRandomItem(par0Random, par1ArrayOfWeightedRandomItem, getTotalWeight(par1ArrayOfWeightedRandomItem));
    }

    public static class Item
        {
            // JAVADOC FIELD $$ field_76292_a
            public int itemWeight;
            private static final String __OBFID = "CL_00001504";

            public Item(int par1)
            {
                this.itemWeight = par1;
            }
        }
}