/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.common.IFMLHandledException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
public abstract class CustomModLoadingErrorDisplayException extends EnhancedRuntimeException implements IFMLHandledException
{
    public CustomModLoadingErrorDisplayException() {
    }

    public CustomModLoadingErrorDisplayException(String message, Throwable cause)
    {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;

    /**
     * Called after the GUI is initialized by the parent code. You can do extra stuff here, maybe?
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

    @Override public void printStackTrace(EnhancedRuntimeException.WrappedPrintStream s){}; // Do Nothing unless the modder wants to.
}
