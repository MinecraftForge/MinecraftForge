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

import net.minecraftforge.event.world.ChunkDataEvent;
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
    if(event.getWorld() == null)
    {
      LOGGER.info("Chunk at {} had null world", event.getChunk().getPos());
    }
  }
}
