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

package net.minecraftforge.debug.client.rendering;

import net.minecraft.init.Biomes;
import net.minecraftforge.client.event.RebuildChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Mod(modid = RebuildChunkEventTest.MODID, name = "RebuildChunkEventTest", version = "1.0", acceptableRemoteVersions = "*", clientSideOnly = true)
public class RebuildChunkEventTest
{

    public static final String MODID = "rebuild_chunk_event_test";

    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onRebuildChunkEvent(final RebuildChunkEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        if (event.getWorldView().getBiome(event.getPosition()) == Biomes.RIVER)
        {
            event.setCanceled(true);
        }

    }

}
