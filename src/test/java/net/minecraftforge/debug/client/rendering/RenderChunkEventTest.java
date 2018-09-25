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

import java.util.Random;

import net.minecraftforge.client.event.RenderChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Mod(modid = RenderChunkEventTest.MODID, name = "RenderChunkEventTest", version = "1.0", acceptableRemoteVersions = "*", clientSideOnly = true)
public class RenderChunkEventTest {

    static final String MODID = "render_chunk_event_test";

    @SubscribeEvent
    public static void onRenderChunkEvent(final RenderChunkEvent event) {
        if (new Random().nextBoolean()) {
//            event.setCanceled(true);
        }
    }

}
