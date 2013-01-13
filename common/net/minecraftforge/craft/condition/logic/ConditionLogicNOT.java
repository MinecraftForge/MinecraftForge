package net.minecraftforge.craft.condition.logic;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.craft.condition.BaseCondition;
import net.minecraftforge.craft.condition.ConditionException;

public class ConditionLogicNOT extends BaseCondition
{
    private final BaseCondition _condition;
    
	private ConditionLogicNOT(BaseCondition condition)
	{
	    _condition = condition;
	}
	
	public static ConditionLogicNOT invert(BaseCondition condition)
	{
	    if (condition == null)
            throw new ConditionException("Cannot Invert NULL");
	    
		return new ConditionLogicNOT(condition);
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		return !_condition.isVerified(inventory, crafter, world, x, y, z);
	}
}
