package net.minecraft.src.forge;

import net.minecraft.src.EntityMinecart;

/**
 * Used to create hashmap values for Minecart/type pairs
 * Written by CovertJaguar
 */
public class MinecartKey
{
    public final Class<? extends EntityMinecart> minecart;
    public final int type;

    public MinecartKey(Class<? extends EntityMinecart> cls, int typtID)
    {
        minecart = cls;
        type = typtID;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        final MinecartKey other = (MinecartKey)obj;
        if (this.minecart != other.minecart && (this.minecart == null || !this.minecart.equals(other.minecart)))
        {
            return false;
        }

        return (this.type == other.type);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + (this.minecart != null ? this.minecart.hashCode() : 0);
        hash = 59 * hash + this.type;
        return hash;
    }
}
