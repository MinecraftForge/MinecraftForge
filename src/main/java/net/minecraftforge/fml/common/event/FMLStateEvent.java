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

package net.minecraftforge.fml.common.event;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.relauncher.Side;

/**
 * The parent of all mod-state changing events
 */
public abstract class FMLStateEvent extends FMLEvent
{
    public FMLStateEvent(Object... data)
    {

    }

    /**
     * The current state of the mod
     * @return The current state of the mod
     */
    public abstract ModState getModState();

    /**
     * The side we're loading on. {@link Side#CLIENT} means we're loading in the client, {@link Side#SERVER} means
     * we're loading in the dedicated server.
     * @return Return which side we're loading on.
     */
    public Side getSide()
    {
        return FMLCommonHandler.instance().getSide();
    }
}
