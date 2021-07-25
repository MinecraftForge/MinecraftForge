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
import net.minecraftforge.client.event.RenderLevelEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RenderLevelEventTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderLevelEventTest {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "render_level_event_test";
    public static final boolean ENABLED = false;
    private static int eventCount = 0;
    
    @SubscribeEvent
    public static void onBegin(RenderLevelEvent.BeginEvent event) {
        if (!ENABLED) {
            return;
        }
        
        eventCount++;
        LOGGER.info("[RLE] begin");
    }
    
    @SubscribeEvent
    public static void onEnd(RenderLevelEvent.EndEvent event) {
        if (!ENABLED) {
            return;
        }
        
        eventCount++;
        LOGGER.info("[RLE] end");
    }
    
    @SubscribeEvent
    public static void onTerrainUpdate(RenderLevelEvent.TerrainUpdateEvent event) {
        if (!ENABLED) {
            return;
        }
        
        eventCount++;
        LOGGER.info("[RLE] terrain update");
    }
    
    @SubscribeEvent
    public static void onBlockLayer(RenderLevelEvent.BlockLayerEvent event) {
        if (!ENABLED) {
            return;
        }
        
        eventCount++;
        LOGGER.info("[RLE] block layer: " + event.getRenderType().toString());
    }
    
    @SubscribeEvent
    public static void onRenderTypeFinish(RenderLevelEvent.RenderTypeFinishEvent event) {
        if (!ENABLED) {
            return;
        }
        
        eventCount++;
        LOGGER.info("[RLE] render type: " + event.getRenderType().toString());
    }
    
    @SubscribeEvent
    public static void onTerrainAndEntitiesDone(RenderLevelEvent.TerrainAndEntitiesDoneEvent event) {
        if (!ENABLED) {
            return;
        }
        
        eventCount++;
        LOGGER.info("[RLE] terrain and tile entities done");
    }
    
    
    
    @SubscribeEvent
    public static void onWeatherDone(RenderLevelEvent.WeatherDoneEvent event) {
        if (!ENABLED) {
            return;
        }
        
        eventCount++;
        LOGGER.info("[RLE] weather done");
    }
    
    @SubscribeEvent
    public static void onRenderTickEnd(RenderTickEvent event) {
        if (!ENABLED) {
            return;
        }
        
        if (event.phase == Phase.END && eventCount != 0) {
            LOGGER.info("[RLE] event count: " + eventCount);
            eventCount = 0;
        }
    }
}
