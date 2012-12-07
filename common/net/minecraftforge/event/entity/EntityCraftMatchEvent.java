package net.minecraftforge.event.entity;

import net.minecraft.src.Entity;
import net.minecraft.src.IRecipe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityCraftMatchEvent extends EntityEvent
{
	private final IRecipe _recipe;
	private final ItemStack _result;
	private final World _world;
	private final double _x;
	private final double _y;
	private final double _z;

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
	public EntityCraftMatchEvent(Entity entity, IRecipe recipe, ItemStack result, World world, double x, double y, double z)
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
	 * Get Event X Coord on World
	 * @return X Coord
	 */
	public double getWorldX()
	{
	    return _x;
	}

	/**
         * Get Event Y Coord on World
         * @return Y Coord
         */
	public double getWorldY()
        {
            return _x;
        }

	/**
         * Get Event Z Coord on World
         * @return Z Coord
         */
	public double getWorldZ()
        {
            return _x;
        }

	/**
	 * Get Event Vector on World
	 * @return Vector
	 */
	public Vec3 getVector()
	{
	    return Vec3.createVectorHelper(_x, _y, _z);
	}

	/**
	 * Set Event Result: Not Used in this Event
	 */
	@Override
	@Deprecated
	public void setResult(Result value) { }
}
