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

import net.minecraftforge.fml.common.LoaderState.ModState;

/**
 * Called after {@link FMLServerStoppingEvent} when the server has completely shut down.
 * Called immediately before shutting down, on the dedicated server, and before returning
 * to the main menu on the client.
 *
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 * @author cpw
 */
public class FMLServerStoppedEvent extends FMLStateEvent {

    public FMLServerStoppedEvent(Object... data)
    {
        super(data);
    }
    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

}
