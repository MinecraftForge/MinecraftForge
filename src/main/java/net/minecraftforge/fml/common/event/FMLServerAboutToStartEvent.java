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

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.LoaderState.ModState;

/**
 * Called before the server begins loading anything. Called after {@link FMLPostInitializationEvent} on the dedicated
 * server, and after the player has hit "Play Selected World" in the client. Called before {@link FMLServerStartingEvent}.
 *
 * You can obtain a reference to the server with this event.
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 * @author cpw
 */
public class FMLServerAboutToStartEvent extends FMLStateEvent {

    private MinecraftServer server;

    public FMLServerAboutToStartEvent(Object... data)
    {
        super(data);
        this.server = (MinecraftServer) data[0];
    }
    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

    public MinecraftServer getServer()
    {
        return server;
    }
}
