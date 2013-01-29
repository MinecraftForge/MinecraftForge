package net.minecraftforge.craft.condition.logic;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;
import net.minecraftforge.craft.condition.ConditionException;

public class ConditionLogicAND extends BaseCondition
{
    private final BaseCondition[] _conditions;
    
	private ConditionLogicAND(BaseCondition[] conditions)
	{
	    _conditions = conditions;
	}
	
	public static ConditionLogicAND compare(BaseCondition... conditions)
	{
	    if (conditions.length == 0)
	        throw new ConditionException("Missing Conditions");
	    
	    for (BaseCondition cond : conditions)
	        if (cond == null)
	            throw new ConditionException("Cannot Compare NULL");
	    
		return new ConditionLogicAND(conditions);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
	    boolean firstCondition = _conditions[0].isVerified(inventory, crafter, world, x, y, z);
	    
		if (_conditions.length == 1 || !firstCondition)
			return firstCondition;
		
		for (int i = 1; i < _conditions.length; i++)
			if (!(firstCondition && _conditions[i].isVerified(inventory, crafter, world, x, y, z)))
				return false;
		return true;
	}
}
