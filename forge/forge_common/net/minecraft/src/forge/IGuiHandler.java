package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public interface IGuiHandler
{
    /**
     * Returns a Container to be displayed to the user. 
     * On the client side, this needs to return a instance of GuiScreen
     * On the server side, this needs to return a instance of Container
     *
     * @param ID The Gui ID Number
     * @param player The player viewing the Gui
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);
}
