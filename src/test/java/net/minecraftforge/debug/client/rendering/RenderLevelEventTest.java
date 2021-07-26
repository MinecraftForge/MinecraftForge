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

import com.google.common.base.Stopwatch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

@Mod(RenderLevelEventTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderLevelEventTest {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "render_level_event_test";
    public static final boolean ENABLED = false;
    public static final boolean REGISTER_EVENTS = true;
    public static final boolean DO_LOG = false;
    private static final int EVENT_BLOCK_COUNT = 50;
    private static final int WAIT_UNTIL_FRAME = 5000;
    private static final int FRAME_COUNT = 5000;
    private static int frameCount = 0;
    private static int eventCount = 0;
    
    private static final Stopwatch timer = Stopwatch.createUnstarted();
    
    public RenderLevelEventTest() {
        if (!ENABLED || !REGISTER_EVENTS) {
            return;
        }
    
        for (int i = 0; i < EVENT_BLOCK_COUNT; i++) {
            MinecraftForge.EVENT_BUS.register(new EventBlock());
        }
        
    }
    
    @SubscribeEvent
    public static void onBegin(RenderLevelEvent.BeginEvent event) {
        if (!ENABLED) {
            return;
        }
        
        eventCount++;
        if (DO_LOG) {
            LOGGER.info("[RLE] begin");
        }
        
        timer.start();
    }
    
    @SubscribeEvent
    public static void onEnd(RenderLevelEvent.EndEvent event) {
        if (!ENABLED) {
            return;
        }
        timer.stop();
        
        eventCount++;
        if (DO_LOG) {
            LOGGER.info("[RLE] end");
        }
    }
    
    private static class EventBlock {
        
        @SubscribeEvent
        public void onTerrainUpdate(RenderLevelEvent.TerrainUpdateEvent event) {
            eventCount++;
            if (DO_LOG) {
                LOGGER.info("[RLE] terrain update");
            }
        }
        
        @SubscribeEvent
        public void onBlockLayer(RenderLevelEvent.BlockLayerEvent event) {
            eventCount++;
            if (DO_LOG) {
                LOGGER.info("[RLE] block layer: " + event.getRenderType().toString());
            }
        }
        
        @SubscribeEvent
        public void onRenderTypeFinish(RenderLevelEvent.RenderTypeFinishEvent event) {
            eventCount++;
            if (DO_LOG) {
                LOGGER.info("[RLE] render type: " + event.getRenderType().toString());
            }
        }
        
        @SubscribeEvent
        public void onTerrainAndEntitiesDone(RenderLevelEvent.TerrainAndEntitiesDoneEvent event) {
            eventCount++;
            if (DO_LOG) {
                LOGGER.info("[RLE] terrain and tile entities done");
            }
        }
        
        
        @SubscribeEvent
        public void onWeatherDone(RenderLevelEvent.WeatherDoneEvent event) {
            eventCount++;
            if (DO_LOG) {
                LOGGER.info("[RLE] weather done");
            }
        }
    }
    
    @SubscribeEvent
    public static void onRenderTickEnd(RenderTickEvent event) {
        if (!ENABLED) {
            return;
        }
        
        if (event.phase == Phase.END && eventCount != 0) {
            
            if(frameCount >= WAIT_UNTIL_FRAME && frameCount % FRAME_COUNT == 0){
                final double timeElapsed = timer.elapsed(TimeUnit.NANOSECONDS) / (double)FRAME_COUNT;
                final double potentialFPS = 1_000_000_000.0 / timeElapsed;
                
                LOGGER.info("[RLE] NS spent rendering level: " + timeElapsed);
                LOGGER.info("[RLE] Theoretical peak FPS:     " + potentialFPS);
                
                timer.reset();
                if(!DO_LOG){
                    LOGGER.info("[RLE] event count: " + eventCount);
                }
            }
            
            if(DO_LOG){
                LOGGER.info("[RLE] event count: " + eventCount);
            }
            eventCount = 0;
            frameCount++;
        }
        
    }
}
