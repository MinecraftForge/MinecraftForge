package net.minecraftforge.craft.condition.logic;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;
import net.minecraftforge.craft.condition.ConditionException;

public class ConditionLogicOR extends BaseCondition
{
    private final BaseCondition[] _conditions;
    
	private ConditionLogicOR(BaseCondition[] conditions)
	{
		_conditions = conditions;
	}
	
	public static ConditionLogicOR compare(BaseCondition... conditions)
	{
	    if (conditions.length == 0)
            throw new ConditionException("Missing Conditions");
        
        for (BaseCondition cond : conditions)
            if (cond == null)
                throw new ConditionException("Cannot Compare NULL");
        
		return new ConditionLogicOR(conditions);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		for (BaseCondition cond : _conditions)
			if (cond.isVerified(inventory, crafter, world, x, y, z))
				return true;
		
		return false;
	}
}
