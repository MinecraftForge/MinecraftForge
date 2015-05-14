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

package cpw.mods.fml.common.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.relauncher.Side;

public abstract class FMLStateEvent extends FMLEvent
{
    public FMLStateEvent(Object... data)
    {

    }

    public abstract ModState getModState();

    public Side getSide()
    {
        return FMLCommonHandler.instance().getSide();
    }
}
