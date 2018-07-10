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

package net.minecraftforge.debug.world;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ChunkWatchEventTest.MODID, name = "Chunk Watch Event Test", version = "1.0", acceptableRemoteVersions = "*")
public class ChunkWatchEventTest
{
    public static final String MODID = "chunkwatchworldtest";

    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(ChunkWatchEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onUnwatch(ChunkWatchEvent.UnWatch event)
    {
        logger.info("Unwatching chunk {} in dimension {}. Player's dimension: {} ", event.getChunk(), event.getChunkInstance().getWorld().provider.getDimension(), event.getPlayer().getEntityWorld().provider.getDimension());
    }

    @SubscribeEvent
    public static void onWatch(ChunkWatchEvent.Watch event)
    {
        logger.info("Watching chunk {} in dimension {}. Player's dimension: {} ", event.getChunk(), event.getChunkInstance().getWorld().provider.getDimension(), event.getPlayer().getEntityWorld().provider.getDimension());
    }
}
