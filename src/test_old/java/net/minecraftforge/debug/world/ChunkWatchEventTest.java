/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

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
        int watched = watchedByPlayer.getInt(event.getPlayer().getUUID());
        --watched;
        watchedByPlayer.put(event.getPlayer().getUUID(), watched);
        logger.info("Unwatching chunk {} in dimension {}. Player's dimension: {}, total chunks watched by player {}",
                event.getPos(), getDimensionName(event.getLevel()), getDimensionName(event.getPlayer().getCommandSenderWorld()),
                watched);
    }

    @SubscribeEvent
    public static void onWatch(ChunkWatchEvent.Watch event)
    {
        int watched = watchedByPlayer.getInt(event.getPlayer().getUUID());
        ++watched;
        watchedByPlayer.put(event.getPlayer().getUUID(), watched);
        logger.info("Watching chunk {} in dimension {}. Player's dimension: {}, total chunks watched by player {}",
                event.getPos(), getDimensionName(event.getLevel()), getDimensionName(event.getPlayer().getCommandSenderWorld()),
                watched);
    }

    @Nullable
    private static ResourceLocation getDimensionName(Level w)
    {
        return w.dimension().location();
    }
}
