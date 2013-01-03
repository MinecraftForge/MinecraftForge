package net.minecraftforge.craftcond;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;

public class ConditionLogic extends Condition
{
	private enum LogicConditions
	{
		OR,
		AND,
		NOT
	};
	
	private final LogicConditions _logicType;
	private final Condition[] _args;
	
	private ConditionLogic(LogicConditions logicType, Condition[] args)
	{
		_logicType = logicType;
		_args = args;
	}
	
	public static ConditionLogic logicalOr(Condition... args)
	{
		return new ConditionLogic(LogicConditions.OR, args);
	}
	
	public static ConditionLogic logicalAnd(Condition... args)
	{
		return new ConditionLogic(LogicConditions.AND, args);
	}
	
	public static ConditionLogic logicalNot(Condition condition)
	{
		return new ConditionLogic(LogicConditions.NOT, new Condition[] { condition });
	}

	@Override
	public boolean isVerified(InventoryCrafting inventory, Entity crafter, World world, int x, int y, int z)
	{
		switch (_logicType)
		{
			case OR:
				for (Condition cond : _args)
					if (cond.isVerified(inventory, crafter, world, x, y, z))
						return true;
				return false;
			case AND:
				if (_args.length < 2)
					return _args[0].isVerified(inventory, crafter, world, x, y, z);
				for (int i = 1; i < _args.length; i++)
					if (!(_args[0].isVerified(inventory, crafter, world, x, y, z) && _args[i].isVerified(inventory, crafter, world, x, y, z)))
						return false;
				return true;
			case NOT:
				return !_args[0].isVerified(inventory, crafter, world, x, y, z);
		}
		
		return false;
	}
}
