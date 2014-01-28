package net.minecraft.util;

public class LongHashMap
{
    // JAVADOC FIELD $$ field_76169_a
    private transient LongHashMap.Entry[] hashArray = new LongHashMap.Entry[16];
    // JAVADOC FIELD $$ field_76167_b
    private transient int numHashElements;
    // JAVADOC FIELD $$ field_76168_c
    private int capacity = 12;
    // JAVADOC FIELD $$ field_76165_d
    private final float percentUseable = 0.75F;
    // JAVADOC FIELD $$ field_76166_e
    private transient volatile int modCount;
    private static final String __OBFID = "CL_00001492";

    // JAVADOC METHOD $$ func_76155_g
    private static int getHashedKey(long par0)
    {
        // JAVADOC METHOD $$ func_76157_a
        return hash((int)(par0 ^ par0 >>> 32));
    }

    // JAVADOC METHOD $$ func_76157_a
    private static int hash(int par0)
    {
        par0 ^= par0 >>> 20 ^ par0 >>> 12;
        return par0 ^ par0 >>> 7 ^ par0 >>> 4;
    }

    // JAVADOC METHOD $$ func_76158_a
    private static int getHashIndex(int par0, int par1)
    {
        return par0 & par1 - 1;
    }

    public int getNumHashElements()
    {
        return this.numHashElements;
    }

    // JAVADOC METHOD $$ func_76164_a
    public Object getValueByKey(long par1)
    {
        int j = getHashedKey(par1);

        for (LongHashMap.Entry entry = this.hashArray[getHashIndex(j, this.hashArray.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == par1)
            {
                return entry.value;
            }
        }

        return null;
    }

    public boolean containsItem(long par1)
    {
        return this.getEntry(par1) != null;
    }

    final LongHashMap.Entry getEntry(long par1)
    {
        int j = getHashedKey(par1);

        for (LongHashMap.Entry entry = this.hashArray[getHashIndex(j, this.hashArray.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == par1)
            {
                return entry;
            }
        }

        return null;
    }

    // JAVADOC METHOD $$ func_76163_a
    public void add(long par1, Object par3Obj)
    {
        int j = getHashedKey(par1);
        int k = getHashIndex(j, this.hashArray.length);

        for (LongHashMap.Entry entry = this.hashArray[k]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == par1)
            {
                entry.value = par3Obj;
                return;
            }
        }

        ++this.modCount;
        this.createKey(j, par1, par3Obj, k);
    }

    // JAVADOC METHOD $$ func_76153_b
    private void resizeTable(int par1)
    {
        LongHashMap.Entry[] aentry = this.hashArray;
        int j = aentry.length;

        if (j == 1073741824)
        {
            this.capacity = Integer.MAX_VALUE;
        }
        else
        {
            LongHashMap.Entry[] aentry1 = new LongHashMap.Entry[par1];
            this.copyHashTableTo(aentry1);
            this.hashArray = aentry1;
            this.capacity = (int)((float)par1 * this.percentUseable);
        }
    }

    // JAVADOC METHOD $$ func_76154_a
    private void copyHashTableTo(LongHashMap.Entry[] par1ArrayOfLongHashMapEntry)
    {
        LongHashMap.Entry[] aentry = this.hashArray;
        int i = par1ArrayOfLongHashMapEntry.length;

        for (int j = 0; j < aentry.length; ++j)
        {
            LongHashMap.Entry entry = aentry[j];

            if (entry != null)
            {
                aentry[j] = null;
                LongHashMap.Entry entry1;

                do
                {
                    entry1 = entry.nextEntry;
                    int k = getHashIndex(entry.hash, i);
                    entry.nextEntry = par1ArrayOfLongHashMapEntry[k];
                    par1ArrayOfLongHashMapEntry[k] = entry;
                    entry = entry1;
                }
                while (entry1 != null);
            }
        }
    }

    // JAVADOC METHOD $$ func_76159_d
    public Object remove(long par1)
    {
        LongHashMap.Entry entry = this.removeKey(par1);
        return entry == null ? null : entry.value;
    }

    // JAVADOC METHOD $$ func_76152_e
    final LongHashMap.Entry removeKey(long par1)
    {
        int j = getHashedKey(par1);
        int k = getHashIndex(j, this.hashArray.length);
        LongHashMap.Entry entry = this.hashArray[k];
        LongHashMap.Entry entry1;
        LongHashMap.Entry entry2;

        for (entry1 = entry; entry1 != null; entry1 = entry2)
        {
            entry2 = entry1.nextEntry;

            if (entry1.key == par1)
            {
                ++this.modCount;
                --this.numHashElements;

                if (entry == entry1)
                {
                    this.hashArray[k] = entry2;
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

    // JAVADOC METHOD $$ func_76156_a
    private void createKey(int par1, long par2, Object par4Obj, int par5)
    {
        LongHashMap.Entry entry = this.hashArray[par5];
        this.hashArray[par5] = new LongHashMap.Entry(par1, par2, par4Obj, entry);

        if (this.numHashElements++ >= this.capacity)
        {
            this.resizeTable(2 * this.hashArray.length);
        }
    }

    static class Entry
        {
            // JAVADOC FIELD $$ field_76150_a
            final long key;
            // JAVADOC FIELD $$ field_76148_b
            Object value;
            // JAVADOC FIELD $$ field_76149_c
            LongHashMap.Entry nextEntry;
            final int hash;
            private static final String __OBFID = "CL_00001493";

            Entry(int par1, long par2, Object par4Obj, LongHashMap.Entry par5LongHashMapEntry)
            {
                this.value = par4Obj;
                this.nextEntry = par5LongHashMapEntry;
                this.key = par2;
                this.hash = par1;
            }

            public final long getKey()
            {
                return this.key;
            }

            public final Object getValue()
            {
                return this.value;
            }

            public final boolean equals(Object par1Obj)
            {
                if (!(par1Obj instanceof LongHashMap.Entry))
                {
                    return false;
                }
                else
                {
                    LongHashMap.Entry entry = (LongHashMap.Entry)par1Obj;
                    Long olong = Long.valueOf(this.getKey());
                    Long olong1 = Long.valueOf(entry.getKey());

                    if (olong == olong1 || olong != null && olong.equals(olong1))
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
                return LongHashMap.getHashedKey(this.key);
            }

            public final String toString()
            {
                return this.getKey() + "=" + this.getValue();
            }
        }
}