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
	private final IRecipe _recipe;
	private final ItemStack _result;
	private final World _world;
	private final int _x;
	private final int _y;
	private final int _z;

	private ItemStack _newResult;

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

	    _recipe = recipe;
	    _result = result;
	    _world = world;
	    _x = x;
	    _y = y;
	    _z = z;
	}

	/**
	 * Get Craft Recipe
	 * @return IRecipe Instance
	 */
	public IRecipe getRecipe()
	{
	    return _recipe;
	}

	/**
	 * Get Craft Result Item
	 * @return ItemStack Instance
	 */
	public ItemStack getItemResult()
	{
	    return _newResult != null ? _newResult : _result;
	}

	/**
	 * Set The Resulting Item
	 * @param newResult Craft Result
	 */
	public void setItemResult(ItemStack newResult)
	{
	    _newResult = newResult;
	}

	/**
	 * Deny Recipe
	 */
	public void denyResult()
	{
	    setCanceled(true);
	}

	/**
	 * Get Entity World
	 * @return Entity World
	 */
	public World getWorld()
	{
	    return _world;
	}

	/**
	 * Get Event X Position on World
	 * @return X Position
	 */
	public double getWorldX()
	{
	    return _x;
	}

	/**
     * Get Event Y Position on World
     * @return Y Position
     */
	public double getWorldY()
    {
        return _y;
    }

	/**
     * Get Event Z Position on World
     * @return Z Position
     */
	public double getWorldZ()
    {
        return _z;
    }
}
