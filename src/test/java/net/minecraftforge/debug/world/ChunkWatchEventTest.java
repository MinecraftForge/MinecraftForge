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

package net.minecraftforge.debug.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod(ChunkWatchEventTest.MODID)
public class ChunkWatchEventTest
{
    public static final String MODID = "chunkwatchworldtest";

    private static final boolean ENABLED = false;
    private static Logger logger;
    private static Object2IntMap<UUID> watchedByPlayer = new Object2IntOpenHashMap<>();

    public ChunkWatchEventTest()
    {
        logger = LogManager.getLogger();

        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(ChunkWatchEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onUnwatch(ChunkWatchEvent.UnWatch event)
    {
        int watched = watchedByPlayer.getInt(event.getPlayer().getUniqueID());
        --watched;
        watchedByPlayer.put(event.getPlayer().getUniqueID(), watched);
        logger.info("Unwatching chunk {} in dimension {}. Player's dimension: {}, total chunks watched by player {}",
                event.getPos(), getDimensionName(event.getWorld()), getDimensionName(event.getPlayer().getEntityWorld()),
                watched);
    }

    @SubscribeEvent
    public static void onWatch(ChunkWatchEvent.Watch event)
    {
        int watched = watchedByPlayer.getInt(event.getPlayer().getUniqueID());
        ++watched;
        watchedByPlayer.put(event.getPlayer().getUniqueID(), watched);
        logger.info("Watching chunk {} in dimension {}. Player's dimension: {}, total chunks watched by player {}",
                event.getPos(), getDimensionName(event.getWorld()), getDimensionName(event.getPlayer().getEntityWorld()),
                watched);
    }

    @Nullable
    private static ResourceLocation getDimensionName(World w) {
        return w.getDimension().getType().getRegistryName();
    }
}

