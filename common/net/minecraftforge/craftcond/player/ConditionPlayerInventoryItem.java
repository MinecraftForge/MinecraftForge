package net.minecraftforge.craftcond.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionPlayerInventoryItem extends BaseCondition
{
	protected ConditionPlayerInventoryItem(Object[] args)
	{
		super(args);
	}
	
	public static ConditionPlayerInventoryItem haveItem(int itemId)
	{
		return new ConditionPlayerInventoryItem(new Object[] { itemId });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (crafter instanceof EntityPlayer)
			return ((EntityPlayer)crafter).inventory.hasItem(getIntArg(0));
		return false;
	}

}
