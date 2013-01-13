package net.minecraftforge.craft.condition.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;

public class ConditionPlayerInventoryItem extends BaseCondition
{
    private final int _itemId;
    
	private ConditionPlayerInventoryItem(int itemId)
	{
	    _itemId = itemId;
	}
	
	public static ConditionPlayerInventoryItem haveItem(int itemId)
	{
		return new ConditionPlayerInventoryItem(itemId);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (crafter instanceof EntityPlayer)
			return ((EntityPlayer)crafter).inventory.hasItem(_itemId);
		return false;
	}

}
