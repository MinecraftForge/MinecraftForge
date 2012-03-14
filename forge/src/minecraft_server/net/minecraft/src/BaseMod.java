package net.minecraft.src;

import java.util.Random;
import net.minecraft.server.MinecraftServer;

public abstract class BaseMod
{
    public int addFuel(int var1, int var2)
    {
        return 0;
    }

    public boolean dispenseEntity(World var1, double var2, double var4, double var6, int var8, int var9, ItemStack var10)
    {
        return false;
    }

    public void generateNether(World var1, Random var2, int var3, int var4) {}

    public void generateSurface(World var1, Random var2, int var3, int var4) {}

    public String getName()
    {
        return this.getClass().getSimpleName();
    }

    public String getPriorities()
    {
        return "";
    }

    public abstract String getVersion();

    public abstract void load();

    public void modsLoaded() {}

    public void onItemPickup(EntityPlayer var1, ItemStack var2) {}

    public void onTickInGame(MinecraftServer var1) {}

    public void takenFromCrafting(EntityPlayer var1, ItemStack var2, IInventory var3) {}

    public void takenFromFurnace(EntityPlayer var1, ItemStack var2) {}

    public String toString()
    {
        return this.getName() + ' ' + this.getVersion();
    }
}
