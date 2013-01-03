package net.minecraftforge.craftcond;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;

public abstract class Condition
{
    public abstract boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z);
}
