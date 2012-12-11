package cpw.mods.fml.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IGuiHandler
{
    /**
     * Returns a Server side Container to be displayed to the user.
     * 
     * @param ID
     *            The Gui ID Number
     * @param player
     *            The player viewing the Gui
     * @param world
     *            The current world
     * @param x
     *            X Position
     * @param y
     *            Y Position
     * @param z
     *            Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);
    /**
     * Returns a Container to be displayed to the user. On the client side, this
     * needs to return a instance of GuiScreen On the server side, this needs to
     * return a instance of Container
     * 
     * @param ID
     *            The Gui ID Number
     * @param player
     *            The player viewing the Gui
     * @param world
     *            The current world
     * @param x
     *            X Position
     * @param y
     *            Y Position
     * @param z
     *            Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);
}
