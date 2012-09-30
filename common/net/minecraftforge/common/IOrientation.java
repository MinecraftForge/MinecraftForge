package net.minecraftforge.common;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public interface IOrientation
{
    /**
     * Called before the default orientation code used to perform something prior to orienting the object
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param entityplayer
     * @return true to skip default orientation code
     */
    public boolean beforeOrientation(World world, int x, int y, int z, EntityPlayer entityplayer);
    
    /**
     * Called after the default orientation code to perform additional or exclusive operation using the variables
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param entityplayer
     * @param currentOrientation The orientation set by the default method
     * @return The old or a new orientation value
     */
    public int afterOrientation(World world, int x, int y, int z, EntityPlayer entityplayer, int currentOrientation);
}
