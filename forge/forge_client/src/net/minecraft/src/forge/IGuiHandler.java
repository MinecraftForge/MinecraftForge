package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.World;

public interface IGuiHandler
{
    /**
     * Returns a GuiScreen to be displayed to the user. This is client side
     *
     * @param ID The Gui ID Number
     * @param player The player viewing the Gui
     * @param world The current world
     * @param X X Position
     * @param Y Y Position
     * @param Z Z Position
     * @return A GuiScreen to be displayed to the user, null if none.
     */
    public GuiScreen getGuiScreen(int ID, EntityPlayerSP player, World world, int X, int Y, int Z);
}
