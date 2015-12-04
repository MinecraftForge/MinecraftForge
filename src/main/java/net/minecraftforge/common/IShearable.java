package net.minecraftforge.common;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 *
 * This allows for mods to create there own Shear-like items
 * and have them interact with Blocks/Entities without extra work.
 * Also, if your block/entity supports the Shears, this allows you
 * to support mod-shears as well.
 *
 */
public interface IShearable
{
    /**
     * Checks if the object is currently shearable
     * Example: Sheep return false when they have no wool
     *
     * @param item The itemstack that is being used, Possible to be null
     * @param world The current world
     * @param pos Block's position in world.
     * @return If this is shearable, and onSheared should be called.
     */
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos);

    /**
     * Performs the shear function on this object.
     * This is called for both client, and server.
     * The object should perform all actions related to being sheared,
     * except for dropping of the items, and removal of the block.
     * As those are handled by ItemShears itself.
     *
     * Returns a list of items that resulted from the shearing process.
     *
     * For entities, they should trust there internal location information
     * over the values passed into this function.
     *
     * @param item The itemstack that is being used, Possible to be null
     * @param world The current world
     * @param pos If this is a block, the block's position in world.
     * @param fortune The fortune level of the shears being used
     * @return A ArrayList containing all items from this shearing. Possible to be null.
     */
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune);
}
