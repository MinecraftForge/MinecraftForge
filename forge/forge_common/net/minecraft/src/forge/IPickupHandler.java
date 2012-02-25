package net.minecraft.src.forge;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public interface IPickupHandler
{

    /**
     * Raised when a player collides with a EntityItem.
     * The handler may consume all, or part of the stack.
     * The handler will only be called if the stack size is > 0
     * The event may be cut part way through if the stack size
     * falls to 0 or a previous handler returns false;
     * Will only be called if delay before pickup is 0.
     *
     * The Entity will destroyed if the stack size falls to 0.
     *
     * @param player Player that picked up the item
     * @param item Item picked up as entity. May be manipulated
     * @return True If processing should continue.
     */
    public boolean onItemPickup(EntityPlayer player, EntityItem item);

}