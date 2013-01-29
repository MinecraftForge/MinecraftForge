package net.minecraftforge.craft.condition.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;
import net.minecraftforge.craft.condition.ConditionException;

public class ConditionPlayerUsername extends BaseCondition
{
    private final String[] _usernames;
    
	private ConditionPlayerUsername(String[] usernames)
	{
		_usernames = usernames;
	}
	
	public static ConditionPlayerUsername equals(String... usernames)
	{
	    for (String username : usernames)
            if (username == null)
                throw new ConditionException("Player Name can't be NULL");
	    
		return new ConditionPlayerUsername(usernames);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		if (crafter instanceof EntityPlayer)
		{
		    for (String username : _usernames)
		    {
		        if (username.equalsIgnoreCase(((EntityPlayer)crafter).username))
		            return true;
		    }
		}
		return false;
	}
}
