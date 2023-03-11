/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Simple test for {@link ChunkEvent.Load#isNewChunk()}. Will log a message to console each time
 * the event is fired for a newly generated chunk.
 *
 * <p>A simple way to check that it's behaving as expected is as follows:</p>
 * <ul>
 * <li>Generate a new world (and log in if using dedicated server). You should see many log messages about
 * the spawn chunks being generated.</li>
 * <li>Don't move around in game, to make sure we only generate the spawn chunks, and any chunks loaded
 * by the player chunk loader for the players initial login position. (to make this test as
 * reproducible as possible without lots of complexity)</li>
 * <li>Once a couple seconds pass without any new chunk log messages, close out of the world/stop the server.</li>
 * <li>Load the world/start the server (and log in if using dedicated server) again. For the same reasons as before,
 * don't move around.</li>
 * <li>You shouldn't see any log messages for new chunks this time.
 * (until you start moving around to generate more chunks)</li>
 * </ul>
 */
@Mod(ChunkEventLoadNewChunkTest.MODID)
@Mod.EventBusSubscriber
public class ChunkEventLoadNewChunkTest
{
    static final String MODID = "chunk_event_load_new_chunk_test";
    private static final boolean ENABLED = true;
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onChunkLoad(final ChunkEvent.Load event)
    {
        if (!ENABLED)
        {
            return;
        }

        if (!event.isNewChunk())
        {
            return;
        }

        LOGGER.info("Loaded freshly generated chunk at {}{}", ((ServerLevel) event.getLevel()).dimension().location(), event.getChunk().getPos());
    }
}
