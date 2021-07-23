/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;

/**
 * This is the second of four commonly called events during mod core startup.
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
 * <em>This event is fired before construction of the dedicated server. Use {@link FMLServerAboutToStartEvent}
 * or {@link FMLServerStartingEvent} to do stuff with the server, in both dedicated
 * and integrated server contexts</em>
 *
 * This is a parallel dispatch event.
 */
public class FMLDedicatedServerSetupEvent extends ParallelDispatchEvent
{
    public FMLDedicatedServerSetupEvent(ModContainer container, ModLoadingStage stage)
    {
        super(container, stage);
    }
}
