package net.minecraftforge.common;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;

public interface INBT {

	/**
	 * This is required to name the NBT file
	 * 
	 * @return the name of the file
	 */
	public String nbtName();

	/**
	 * @return true if you want this NBT to be saved per player
	 */
	public boolean savePerPlayer();

	/**
	 * This gets called every time the player exits from world and after 40
	 * ticks
	 * 
	 * @param nbttagcompound NBTTagCompound to which you write your data
	 * @param player The player for which it's writing at the moment, null if savePerPlayer equals false
	 */
	public void writeToNBT(NBTTagCompound nbttagcompound, EntityPlayer player);

	/**
	 * This gets called every time the world gets loaded.
	 * 
	 * @param nbttagcompound NBTTagCompound from which you read your data
	 * @param player The player for which it's reading at the moment, null if savePerPlayer equals false
	 */
	public void readFromNBT(NBTTagCompound nbttagcompound, EntityPlayer player);
}
