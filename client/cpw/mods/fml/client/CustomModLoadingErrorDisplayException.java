package cpw.mods.fml.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import cpw.mods.fml.common.IFMLHandledException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * If a mod throws this exception during loading, it will be called back to render
 * the error screen through the methods below. This error will not be cleared, and will
 * not allow the game to carry on, but might be useful if your mod wishes to report
 * a fatal configuration error in a pretty way.
 *
 * Throw this through a proxy. It won't work on the dedicated server environment.
 * @author cpw
 *
 */
@SideOnly(Side.CLIENT)
public abstract class CustomModLoadingErrorDisplayException extends RuntimeException implements IFMLHandledException
{
    /**
     * Called after the GUI is inited by the parent code. You can do extra stuff here, maybe?
     *
     * @param errorScreen The error screen we're painting
     * @param fontRenderer A font renderer for you
     */
    public abstract void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer);

    /**
     * Draw your error to the screen.
     *
     * <br/><em>Warning: Minecraft is in a deep error state.</em> <strong>All</strong> it can do is stop.
     * Do not try and do anything involving complex user interaction here.
     *
     * @param errorScreen The error screen to draw to
     * @param fontRenderer A font renderer for you
     * @param mouseRelX Mouse X
     * @param mouseRelY Mouse Y
     * @param tickTime tick time
     */
    public abstract void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime);
}
