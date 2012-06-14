package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;

public interface ICanPlaceBlockHandler {

	/*
	 * Called when a player tries to place a block
	 * 
	 * server side only
	 * 
	 * if returned false the placement will be blocked
	 * 
	 * @param player The player
	 * @param X X-coordinate of the block
	 * @param Y Y-coordinate of the block
	 * @param Z Z-coordinate of the block
	 */
	
	public boolean canPlaceBlock(EntityPlayer player, int X, int Y, int Z);
	
}