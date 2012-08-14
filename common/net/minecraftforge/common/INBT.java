package net.minecraftforge.common;

import net.minecraft.src.NBTTagCompound;

public interface INBT {
	
	/**
	 * This is required to name the NBT file
	 * @return	the name of the file
	 */
    public String nbtName();
    
    /**
     * @return	true if you want this NBT to be saved per player
     */
    public boolean savePerPlayer();
    
    /**
     * This gets called every time the player exits from world and after 40 ticks
     * @param nbttagcompound	NBTTagCompound to wich you write your data
     */
    public void writeToNBT(NBTTagCompound nbttagcompound);
    
    /**
     * This gets called every time the world gets loaded.
     * @param nbttagcompound	NBTTagCompound from wich you read your data
     */
    public void readFromNBT(NBTTagCompound nbttagcompound);
    
    /**
     * This gets called before first NBT read is preformed, usefull for clearing lists.
     */
    public void beforeWorldLoad();
}
