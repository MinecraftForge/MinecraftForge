package net.minecraft.src.forge;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.World;

public interface IGuiHandler 
{
    /**
     * Returns a Container to be displayed to the user. This is server side
     * 
     * @param ID The Gui ID Number
     * @param player The player viewing the Gui
     * @param world The current world
     * @param X X Position
     * @param Y Y Position
     * @param Z Z Position
     * @return A GuiScreen to be displayed to the user, null if none.
     */
    public Container getGuiContainer(int ID, EntityPlayerMP player, World world, int X, int Y, int Z);
}
