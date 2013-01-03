package net.minecraftforge.craftcond;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ConditionPlayer extends Condition
{
	private ConditionPlayer()
	{
		
	}
	
	public static ConditionPlayer haveInventoryItem(ItemStack item)
	{
		return new ConditionPlayer();
	}
	
	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		return false;
	}

}
