package net.minecraft.util;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap
{
    // JAVADOC FIELD $$ field_76055_a
    private transient IntHashMap.Entry[] slots = new IntHashMap.Entry[16];
    // JAVADOC FIELD $$ field_76053_b
    private transient int count;
    // JAVADOC FIELD $$ field_76054_c
    private int threshold = 12;
    // JAVADOC FIELD $$ field_76051_d
    private final float growFactor = 0.75F;
    // JAVADOC FIELD $$ field_76052_e
    private transient volatile int versionStamp;
    // JAVADOC FIELD $$ field_76050_f
    private Set keySet = new HashSet();
    private static final String __OBFID = "CL_00001490";

    // JAVADOC METHOD $$ func_76044_g
    private static int computeHash(int par0)
    {
        par0 ^= par0 >>> 20 ^ par0 >>> 12;
        return par0 ^ par0 >>> 7 ^ par0 >>> 4;
    }

    // JAVADOC METHOD $$ func_76043_a
    private static int getSlotIndex(int par0, int par1)
    {
        return par0 & par1 - 1;
    }

    // JAVADOC METHOD $$ func_76041_a
    public Object lookup(int par1)
    {
        int j = computeHash(par1);

        for (IntHashMap.Entry entry = this.slots[getSlotIndex(j, this.slots.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.hashEntry == par1)
            {
                return entry.valueEntry;
            }
        }

        return null;
    }

    // JAVADOC METHOD $$ func_76037_b
    public boolean containsItem(int par1)
    {
        return this.lookupEntry(par1) != null;
    }

    // JAVADOC METHOD $$ func_76045_c
    final IntHashMap.Entry lookupEntry(int par1)
    {
        int j = computeHash(par1);

        for (IntHashMap.Entry entry = this.slots[getSlotIndex(j, this.slots.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.hashEntry == par1)
            {
                return entry;
            }
        }

        return null;
    }

    // JAVADOC METHOD $$ func_76038_a
    public void addKey(int par1, Object par2Obj)
    {
        this.keySet.add(Integer.valueOf(par1));
        int j = computeHash(par1);
        int k = getSlotIndex(j, this.slots.length);

        for (IntHashMap.Entry entry = this.slots[k]; entry != null; entry = entry.nextEntry)
        {
            if (entry.hashEntry == par1)
            {
                entry.valueEntry = par2Obj;
                return;
            }
        }

        ++this.versionStamp;
        this.insert(j, par1, par2Obj, k);
    }

    // JAVADOC METHOD $$ func_76047_h
    private void grow(int par1)
    {
        IntHashMap.Entry[] aentry = this.slots;
        int j = aentry.length;

        if (j == 1073741824)
        {
            this.threshold = Integer.MAX_VALUE;
        }
        else
        {
            IntHashMap.Entry[] aentry1 = new IntHashMap.Entry[par1];
            this.copyTo(aentry1);
            this.slots = aentry1;
            this.threshold = (int)((float)par1 * this.growFactor);
        }
    }

    // JAVADOC METHOD $$ func_76048_a
    private void copyTo(IntHashMap.Entry[] par1ArrayOfIntHashMapEntry)
    {
        IntHashMap.Entry[] aentry = this.slots;
        int i = par1ArrayOfIntHashMapEntry.length;

        for (int j = 0; j < aentry.length; ++j)
        {
            IntHashMap.Entry entry = aentry[j];

            if (entry != null)
            {
                aentry[j] = null;
                IntHashMap.Entry entry1;

                do
                {
                    entry1 = entry.nextEntry;
                    int k = getSlotIndex(entry.slotHash, i);
                    entry.nextEntry = par1ArrayOfIntHashMapEntry[k];
                    par1ArrayOfIntHashMapEntry[k] = entry;
                    entry = entry1;
                }
                while (entry1 != null);
            }
        }
    }

    // JAVADOC METHOD $$ func_76049_d
    public Object removeObject(int par1)
    {
        this.keySet.remove(Integer.valueOf(par1));
        IntHashMap.Entry entry = this.removeEntry(par1);
        return entry == null ? null : entry.valueEntry;
    }

    // JAVADOC METHOD $$ func_76036_e
    final IntHashMap.Entry removeEntry(int par1)
    {
        int j = computeHash(par1);
        int k = getSlotIndex(j, this.slots.length);
        IntHashMap.Entry entry = this.slots[k];
        IntHashMap.Entry entry1;
        IntHashMap.Entry entry2;

        for (entry1 = entry; entry1 != null; entry1 = entry2)
        {
            entry2 = entry1.nextEntry;

            if (entry1.hashEntry == par1)
            {
                ++this.versionStamp;
                --this.count;

                if (entry == entry1)
                {
                    this.slots[k] = entry2;
                }
                else
                {
                    entry.nextEntry = entry2;
                }

                return entry1;
            }

            entry = entry1;
        }

        return entry1;
    }

    // JAVADOC METHOD $$ func_76046_c
    public void clearMap()
    {
        ++this.versionStamp;
        IntHashMap.Entry[] aentry = this.slots;

        for (int i = 0; i < aentry.length; ++i)
        {
            aentry[i] = null;
        }

        this.count = 0;
    }

    // JAVADOC METHOD $$ func_76040_a
    private void insert(int par1, int par2, Object par3Obj, int par4)
    {
        IntHashMap.Entry entry = this.slots[par4];
        this.slots[par4] = new IntHashMap.Entry(par1, par2, par3Obj, entry);

        if (this.count++ >= this.threshold)
        {
            this.grow(2 * this.slots.length);
        }
    }

    static class Entry
        {
            // JAVADOC FIELD $$ field_76035_a
            final int hashEntry;
            // JAVADOC FIELD $$ field_76033_b
            Object valueEntry;
            // JAVADOC FIELD $$ field_76034_c
            IntHashMap.Entry nextEntry;
            // JAVADOC FIELD $$ field_76032_d
            final int slotHash;
            private static final String __OBFID = "CL_00001491";

            Entry(int par1, int par2, Object par3Obj, IntHashMap.Entry par4IntHashMapEntry)
            {
                this.valueEntry = par3Obj;
                this.nextEntry = par4IntHashMapEntry;
                this.hashEntry = par2;
                this.slotHash = par1;
            }

            // JAVADOC METHOD $$ func_76031_a
            public final int getHash()
            {
                return this.hashEntry;
            }

            // JAVADOC METHOD $$ func_76030_b
            public final Object getValue()
            {
                return this.valueEntry;
            }

            public final boolean equals(Object par1Obj)
            {
                if (!(par1Obj instanceof IntHashMap.Entry))
                {
                    return false;
                }
                else
                {
                    IntHashMap.Entry entry = (IntHashMap.Entry)par1Obj;
                    Integer integer = Integer.valueOf(this.getHash());
                    Integer integer1 = Integer.valueOf(entry.getHash());

                    if (integer == integer1 || integer != null && integer.equals(integer1))
                    {
                        Object object1 = this.getValue();
                        Object object2 = entry.getValue();

                        if (object1 == object2 || object1 != null && object1.equals(object2))
                        {
                            return true;
                        }
                    }

                    return false;
                }
            }

            public final int hashCode()
            {
                return IntHashMap.computeHash(this.hashEntry);
            }

            public final String toString()
            {
                return this.getHash() + "=" + this.getValue();
            }
        }
}