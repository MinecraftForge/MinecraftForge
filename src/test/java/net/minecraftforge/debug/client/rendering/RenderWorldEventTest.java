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

package net.minecraftforge.debug.client.rendering;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldEvent.RenderWorldBlockLayerEvent;
import net.minecraftforge.client.event.RenderWorldEvent.RenderWorldRenderTypeFinishEvent;
import net.minecraftforge.client.event.RenderWorldEvent.RenderWorldTerrainUpdateEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RenderWorldEventTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderWorldEventTest
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "render_world_event_test";
    public static final boolean ENABLED = false;
    private static int eventCount = 0;

    @SubscribeEvent
    public static void onTerrainUpdate(RenderWorldTerrainUpdateEvent event)
    {
        if (!ENABLED)
            return;

        eventCount++;
        LOGGER.info("[RWE] terrain update");
    }

    @SubscribeEvent
    public static void onBlockLayer(RenderWorldBlockLayerEvent event)
    {
        if (!ENABLED)
            return;

        eventCount++;
        LOGGER.info("[RWE] block layer: " + event.getRenderType().toString());
    }

    @SubscribeEvent
    public static void onRenderType(RenderWorldRenderTypeFinishEvent event)
    {
        if (!ENABLED)
            return;

        eventCount++;
        LOGGER.info("[RWE] render type: " + event.getRenderType().toString());
    }

    @SubscribeEvent
    public static void onRenderTickEnd(RenderTickEvent event)
    {
        if (!ENABLED)
            return;

        if (event.phase == Phase.END && eventCount != 0)
        {
            LOGGER.info("[RWE] event count: " + eventCount);
            eventCount = 0;
        }
    }
}