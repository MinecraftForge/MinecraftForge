/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChunkDataEventSaveNullWorldTest.MODID)
@Mod.EventBusSubscriber
public class ChunkDataEventSaveNullWorldTest {
  static final String MODID = "chunk_data_event_save_null_world_test";
  private static final Logger LOGGER = LogManager.getLogger(MODID);

  @SubscribeEvent
  public static void onChunkSave(final ChunkDataEvent.Save event)
  {
    if(event.getLevel() == null)
    {
      LOGGER.info("Chunk at {} had null world", event.getChunk().getPos());
    }
  }
}
