package net.minecraft.src;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap
{
    /** An array of HashEntries representing the heads of hash slot lists */
    private transient IntHashMapEntry[] slots = new IntHashMapEntry[16];

    /** The number of items stored in this map */
    private transient int count;

    /** The grow threshold */
    private int threshold = 12;

    /** The scale factor used to determine when to grow the table */
    private final float growFactor = 0.75F;

    /** A serial stamp used to mark changes */
    private transient volatile int versionStamp;

    /** The set of all the keys stored in this MCHash object */
    private Set keySet = new HashSet();

    /**
     * Makes the passed in integer suitable for hashing by a number of shifts
     */
    private static int computeHash(int par0)
    {
        par0 ^= par0 >>> 20 ^ par0 >>> 12;
        return par0 ^ par0 >>> 7 ^ par0 >>> 4;
    }

    /**
     * Computes the index of the slot for the hash and slot count passed in.
     */
    private static int getSlotIndex(int par0, int par1)
    {
        return par0 & par1 - 1;
    }

    /**
     * Returns the object associated to a key
     */
    public Object lookup(int par1)
    {
        int var2 = computeHash(par1);

        for (IntHashMapEntry var3 = this.slots[getSlotIndex(var2, this.slots.length)]; var3 != null; var3 = var3.nextEntry)
        {
            if (var3.hashEntry == par1)
            {
                return var3.valueEntry;
            }
        }

        return null;
    }

    /**
     * Return true if an object is associated with the given key
     */
    public boolean containsItem(int par1)
    {
        return this.lookupEntry(par1) != null;
    }

    /**
     * Returns the key/object mapping for a given key as a MCHashEntry
     */
    final IntHashMapEntry lookupEntry(int par1)
    {
        int var2 = computeHash(par1);

        for (IntHashMapEntry var3 = this.slots[getSlotIndex(var2, this.slots.length)]; var3 != null; var3 = var3.nextEntry)
        {
            if (var3.hashEntry == par1)
            {
                return var3;
            }
        }

        return null;
    }

    /**
     * Adds a key and associated value to this map
     */
    public void addKey(int par1, Object par2Obj)
    {
        this.keySet.add(Integer.valueOf(par1));
        int var3 = computeHash(par1);
        int var4 = getSlotIndex(var3, this.slots.length);

        for (IntHashMapEntry var5 = this.slots[var4]; var5 != null; var5 = var5.nextEntry)
        {
            if (var5.hashEntry == par1)
            {
                var5.valueEntry = par2Obj;
            }
        }

        ++this.versionStamp;
        this.insert(var3, par1, par2Obj, var4);
    }

    /**
     * Increases the number of hash slots
     */
    private void grow(int par1)
    {
        IntHashMapEntry[] var2 = this.slots;
        int var3 = var2.length;

        if (var3 == 1073741824)
        {
            this.threshold = Integer.MAX_VALUE;
        }
        else
        {
            IntHashMapEntry[] var4 = new IntHashMapEntry[par1];
            this.copyTo(var4);
            this.slots = var4;
            this.threshold = (int)((float)par1 * this.growFactor);
        }
    }

    /**
     * Copies the hash slots to a new array
     */
    private void copyTo(IntHashMapEntry[] par1ArrayOfIntHashMapEntry)
    {
        IntHashMapEntry[] var2 = this.slots;
        int var3 = par1ArrayOfIntHashMapEntry.length;

        for (int var4 = 0; var4 < var2.length; ++var4)
        {
            IntHashMapEntry var5 = var2[var4];

            if (var5 != null)
            {
                var2[var4] = null;
                IntHashMapEntry var6;

                do
                {
                    var6 = var5.nextEntry;
                    int var7 = getSlotIndex(var5.slotHash, var3);
                    var5.nextEntry = par1ArrayOfIntHashMapEntry[var7];
                    par1ArrayOfIntHashMapEntry[var7] = var5;
                    var5 = var6;
                }
                while (var6 != null);
            }
        }
    }

    /**
     * Removes the specified object from the map and returns it
     */
    public Object removeObject(int par1)
    {
        this.keySet.remove(Integer.valueOf(par1));
        IntHashMapEntry var2 = this.removeEntry(par1);
        return var2 == null ? null : var2.valueEntry;
    }

    /**
     * Removes the specified entry from the map and returns it
     */
    final IntHashMapEntry removeEntry(int par1)
    {
        int var2 = computeHash(par1);
        int var3 = getSlotIndex(var2, this.slots.length);
        IntHashMapEntry var4 = this.slots[var3];
        IntHashMapEntry var5;
        IntHashMapEntry var6;

        for (var5 = var4; var5 != null; var5 = var6)
        {
            var6 = var5.nextEntry;

            if (var5.hashEntry == par1)
            {
                ++this.versionStamp;
                --this.count;

                if (var4 == var5)
                {
                    this.slots[var3] = var6;
                }
                else
                {
                    var4.nextEntry = var6;
                }

                return var5;
            }

            var4 = var5;
        }

        return var5;
    }

    /**
     * Removes all entries from the map
     */
    public void clearMap()
    {
        ++this.versionStamp;
        IntHashMapEntry[] var1 = this.slots;

        for (int var2 = 0; var2 < var1.length; ++var2)
        {
            var1[var2] = null;
        }

        this.count = 0;
    }

    /**
     * Adds an object to a slot
     */
    private void insert(int par1, int par2, Object par3Obj, int par4)
    {
        IntHashMapEntry var5 = this.slots[par4];
        this.slots[par4] = new IntHashMapEntry(par1, par2, par3Obj, var5);

        if (this.count++ >= this.threshold)
        {
            this.grow(2 * this.slots.length);
        }
    }

    /**
     * Return the Set of all keys stored in this MCHash object
     */
    public Set getKeySet()
    {
        return this.keySet;
    }

    /**
     * Returns the hash code for a key
     */
    static int getHash(int par0)
    {
        return computeHash(par0);
    }
}
