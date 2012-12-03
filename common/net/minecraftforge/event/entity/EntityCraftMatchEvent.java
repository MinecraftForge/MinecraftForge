package net.minecraftforge.event.entity;

import net.minecraft.src.Entity;
import net.minecraft.src.IRecipe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityCraftMatchEvent extends EntityEvent
{
	public final IRecipe recipe;
	public final ItemStack result;
	public final World world;
	public final double x;
	public final double y;
	public final double z;
	
	public ItemStack newResult;
	
	public EntityCraftMatchEvent(Entity entity, IRecipe recipe, ItemStack result, World world, double x, double y, double z)
	{
		super(entity);
		
		this.recipe = recipe;
		this.result = result;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
