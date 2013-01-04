package net.minecraftforge.craftcond.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craftcond.BaseCondition;

public class ConditionPlayerUsername extends BaseCondition
{
	private ConditionPlayerUsername(Object[] args)
	{
		super(args);
	}
	
	public static ConditionPlayerUsername equals(String username)
	{
		return new ConditionPlayerUsername(new Object[] { username });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (crafter instanceof EntityPlayer)
			return getStringArg(0).equalsIgnoreCase(((EntityPlayer)crafter).username);
		return false;
	}
}
