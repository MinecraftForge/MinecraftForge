/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

/**
 * Called before the server begins loading anything. Called after {@link InterModProcessEvent} on the dedicated
 * server, and after the player has hit "Play Selected World" in the client. Called before {@link FMLServerStartingEvent}.
 *
 * You can obtain a reference to the server with this event.
 * @author cpw
 */
public class FMLServerAboutToStartEvent extends ServerLifecycleEvent {

    public FMLServerAboutToStartEvent(MinecraftServer server)
    {
        super(server);
    }
}
