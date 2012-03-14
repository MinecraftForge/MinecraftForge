package net.minecraft.src;

class IntHashMapEntry
{
    final int hashEntry;
    Object valueEntry;
    IntHashMapEntry nextEntry;
    final int slotHash;

    IntHashMapEntry(int par1, int par2, Object par3Obj, IntHashMapEntry par4IntHashMapEntry)
    {
        this.valueEntry = par3Obj;
        this.nextEntry = par4IntHashMapEntry;
        this.hashEntry = par2;
        this.slotHash = par1;
    }

    public final int getHash()
    {
        return this.hashEntry;
    }

    public final Object getValue()
    {
        return this.valueEntry;
    }

    public final boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof IntHashMapEntry))
        {
            return false;
        }
        else
        {
            IntHashMapEntry var2 = (IntHashMapEntry)par1Obj;
            Integer var3 = Integer.valueOf(this.getHash());
            Integer var4 = Integer.valueOf(var2.getHash());

            if (var3 == var4 || var3 != null && var3.equals(var4))
            {
                Object var5 = this.getValue();
                Object var6 = var2.getValue();

                if (var5 == var6 || var5 != null && var5.equals(var6))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public final int hashCode()
    {
        return IntHashMap.getHash(this.hashEntry);
    }

    public final String toString()
    {
        return this.getHash() + "=" + this.getValue();
    }
}
