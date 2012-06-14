package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;

public interface ICanMineBlockHandler {

	/*
	 * Called when a player tries to remove a block
	 * 
	 * server side only
	 * 
	 * if returned false the removal will be blocked
	 * 
	 * @param player The player
	 * @param X X-coordinate of the block
	 * @param Y Y-coordinate of the block
	 * @param Z Z-coordinate of the block
	 */
	
	public boolean canMineBlock(EntityPlayer player, int X, int Y, int Z);
	
}