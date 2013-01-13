package net.minecraftforge.event.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.EntityEvent;

@Cancelable
public class ItemCraftMatchEvent extends EntityEvent
{
	public final IRecipe recipe;
	public final ItemStack result;
	public final World world;
	public final int x;
	public final int y;
	public final int z;

	public ItemStack newResult;

	/**
	 * This Event is Called when an Entity put a valid
	 * item combination into a craft table.
	 * 
	 * @param entity The Entity
	 * @param recipe Matching Recipe
	 * @param result Expected Recipe Result
	 * @param world Event World Origin
	 * @param x Event X Origin
	 * @param y Event Y Origin
	 * @param z Event Z Origin
	 */
	public ItemCraftMatchEvent(Entity entity, IRecipe recipe, ItemStack result, World world, int x, int y, int z)
	{
	    super(entity);

	    this.recipe = recipe;
	    this.result = result;
	    this.world = world;
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.newResult = result;
	}
}

