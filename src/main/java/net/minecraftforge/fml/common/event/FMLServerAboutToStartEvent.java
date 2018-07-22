/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
