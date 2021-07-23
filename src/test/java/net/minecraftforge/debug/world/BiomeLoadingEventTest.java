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

package net.minecraftforge.debug.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BiomeLoadingEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BiomeLoadingEventTest.MODID)
public class BiomeLoadingEventTest {
    static final String MODID = "biome_loading_event_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final boolean ENABLED = false;
    public BiomeLoadingEventTest(){
        if(ENABLED) {
            MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoading);
        }
    }
    public void onBiomeLoading(BiomeLoadingEvent event){
        ResourceLocation biome = event.getName();
        LOGGER.info(String.format("Biome loaded: %s", biome.toString()));
    }
}
