package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;

public interface ICanMineHandler 
{
    /**
     * Called when a player tries to place or romove a block
     * 
     * This is only called on the server side.
     * 
     * Return true from this function to allow the placing/removal of the block
     * Return false from this function to deny the placing/removal of the block 
     * 
     * @param player The player trying to mine or place
     * @param X is the X coord of the block
     * @param Y is the Y coord of the block
     * @param Z is the Z coord of the block
     */
    public boolean canMine(EntityPlayer player, int X, int Y, int Z);
}
