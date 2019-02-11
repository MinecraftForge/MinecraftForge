/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.event.lifecycle;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.ModContainer;

import java.util.function.Supplier;

/**
 * This is the second of four commonly called events during mod lifecycle startup.
 *
 * Called before {@link InterModEnqueueEvent}
 * Called after {@link FMLCommonSetupEvent}
 *
 * Called on {@link net.minecraftforge.api.distmarker.Dist#DEDICATED_SERVER} - the dedicated game server.
 *
 * Alternative to {@link FMLClientSetupEvent}.
 *
 * Do dedicated server specific activities with this event.
 *
 * This is a parallel dispatch event.
 */
public class FMLDedicatedServerSetupEvent extends ModLifecycleEvent
{
    private final Supplier<DedicatedServer> serverSupplier;

    public FMLDedicatedServerSetupEvent(Supplier<DedicatedServer> server, ModContainer container)
    {
        super(container);
        this.serverSupplier = server;
    }

    public Supplier<DedicatedServer> getServerSupplier()
    {
        return serverSupplier;
    }
}
